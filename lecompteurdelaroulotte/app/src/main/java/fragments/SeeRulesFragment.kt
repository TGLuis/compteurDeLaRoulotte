package fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Rule
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.lang.Exception
import java.util.ArrayList

class SeeRulesFragment: Fragment() {
    private val TAG = "===== SEERULESFRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var LV_rules: ListView
    private lateinit var B_addRule: Button
    private lateinit var rules: ArrayList<Rule>
    private lateinit var adapteur: RulesAdapter

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
            val rule_text = (context as MainActivity).createTextFromRule(rule)
            TV_rule.text = rule_text
            TV_rule.setOnClickListener {
                (context as MainActivity).actualRule = rule
                (context as MainActivity).frags.add(SeeRulesFragment())
                (context as MainActivity).openFragment(RuleFragment())
            }

            val IB_del = projectView.findViewById<ImageButton>(R.id.delete_image)
            IB_del.setOnClickListener{
                val alert = AlertDialog.Builder(context)
                alert.setTitle(R.string.confirm)
                        .setMessage(getString(R.string.delete_rule) + "\n" + rule_text)
                        .setPositiveButton(R.string.yes){ dialog, _ ->
                            (context as MainActivity).deleteRuleOfProject(rule)
                            adapteur.notifyDataSetChanged()
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel){dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create()
                        .show()
            }

            return projectView
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_see_rules, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rules = context.actualProject!!.myRules

        B_addRule = context.findViewById(R.id.button_add_rule)
        B_addRule.setOnClickListener {
            context.actualRule = null
            context.frags.add(SeeRulesFragment())
            context.openFragment(RuleFragment())
        }

        LV_rules = context.findViewById(R.id.listRules)
        adapteur = this.RulesAdapter(context, rules)
        LV_rules.adapter = adapteur

        context.actualFragment = SeeRulesFragment()
        context.title = "${context.actualProject.toString()} -> ${context.getString(R.string.my_rules)}"
    }
}