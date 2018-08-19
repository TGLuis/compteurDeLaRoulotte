package fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Rule
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.util.ArrayList

class SeeRuleFragment: Fragment() {
    private val TAG = "===== SEERULESFRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var LV_rules: ListView
    private lateinit var B_addRule: Button
    private lateinit var rules: ArrayList<Rule>

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
            TV_rule.text = rule.toString()

            val IB_del = projectView.findViewById<ImageButton>(R.id.delete_image)
            IB_del.setOnClickListener{
                //TODO: delete rule
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
            context.frags.add(SeeRuleFragment())
            context.openFragment(AddRuleFragment())
        }

        LV_rules = context.findViewById(R.id.listRules)
        val adapteur = this.RulesAdapter(context, rules)
        LV_rules.adapter = adapteur

        context.title = "${context.actualProject.toString()} -> ${context.getString(R.string.my_rules)}"
    }
}