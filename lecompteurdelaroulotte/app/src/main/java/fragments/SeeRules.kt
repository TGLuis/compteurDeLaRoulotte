package fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import library.Rule
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import java.util.ArrayList

class SeeRules: SeeFragment() {
    override var TAG: String = "===== SEERULES ====="

    private lateinit var rules: ArrayList<Rule>
    private lateinit var adapteurRules: RulesAdapter

    inner class RulesAdapter(context: Context, list: ArrayList<Rule>) : ArrayAdapter<Rule>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rule = super.getItem(position)
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null){
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_rule_item, parent, false)
                viewHolder.msg = projectView.findViewById(R.id.text_rule)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }
            viewHolder.msg!!.text = rule.toString()

            val TV_rule = projectView.findViewById<TextView>(R.id.text_rule)
            val rule_text = rule!!.getString(context as MainActivity)
            TV_rule.text = rule_text
            TV_rule.setOnClickListener {
                (context as MainActivity).currentRule = rule
                (context as MainActivity).openFragment(RuleFragment())
            }

            val IB_del = projectView.findViewById<ImageButton>(R.id.delete_image)
            IB_del.setOnClickListener{
                val alert = AlertDialog.Builder(context)
                alert.setTitle(R.string.confirm)
                    .setMessage(getString(R.string.delete_rule) + "\n" + rule_text)
                    .setPositiveButton(R.string.yes){ dialog, _ ->
                        (context as MainActivity).deleteRuleOfProject(rule)
                        adapteurRules.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            }

            return projectView
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rules = context.currentProject!!.myRules
        button_add.text = getString(R.string.add_rule)
        button_add.setOnClickListener {
            context.currentRule = null
            context.openFragment(RuleFragment())
        }

        adapteurRules = this.RulesAdapter(context, rules)
        listView.adapter = adapteurRules
        context.title = "${context.currentProject.toString()} -> ${context.getString(R.string.my_rules)}"
    }
}
