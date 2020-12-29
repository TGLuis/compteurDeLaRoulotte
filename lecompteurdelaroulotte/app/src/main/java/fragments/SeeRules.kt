package fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import library.Dialogs
import library.Rule
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import java.util.*

class SeeRules: SeeFragment() {
    override var TAG: String = "===== SEERULES ====="

    private lateinit var rules: ArrayList<Rule>
    private lateinit var adapteurRules: RulesAdapter
    private lateinit var selectedRule: Rule

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

            val textView_rule = projectView.findViewById<TextView>(R.id.text_rule)
            val rule_text = rule!!.getString(context as MainActivity)
            textView_rule.text = rule_text
            textView_rule.setOnClickListener {
                (context as MainActivity).currentRule = rule
                (context as MainActivity).openFragment(RuleFragment())
            }

            val imageButton_delete = projectView.findViewById<ImageButton>(R.id.delete_image)
            imageButton_delete.setOnClickListener{
                selectedRule = rule
                Dialogs.displayConfirmationDialog(context, getString(R.string.delete_rule) + "\n" + rule_text, ::deleteRule)
            }

            return projectView
        }

        private fun deleteRule() {
            (context as MainActivity).deleteRuleOfProject(selectedRule)
            adapteurRules.notifyDataSetChanged()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rules = context.currentProject!!.myRules
        button_add.text = getString(R.string.rule)
        button_add.setOnClickListener {
            context.currentRule = null
            context.openFragment(RuleFragment())
        }

        adapteurRules = this.RulesAdapter(context, rules)
        listView.adapter = adapteurRules
        context.title = "${context.currentProject.toString()} -> ${context.getString(R.string.my_rules)}"
    }
}
