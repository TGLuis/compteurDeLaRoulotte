package fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Comment
import library.Rule
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.util.ArrayList

class SeeFragment: Fragment() {
    private val TAG = "===== SEECOMMENTSFRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var LV: ListView
    private lateinit var B_add: Button
    private lateinit var comments: ArrayList<Comment>
    private lateinit var rules: ArrayList<Rule>
    private lateinit var adapteurComments: CommentsAdapter
    private lateinit var adapteurRules: RulesAdapter

    inner class CommentsAdapter(context: Context, list: ArrayList<Comment>) : ArrayAdapter<Comment>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val comment = super.getItem(position)
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null){
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_comment_item, parent, false)
                viewHolder.msg = projectView.findViewById(R.id.text_comment)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }
            viewHolder.msg!!.text = comment.toString()

            val TV_comment = projectView.findViewById<TextView>(R.id.text_comment)
            val comment_text = (activity as MainActivity).createTextFromComment(comment)
            TV_comment.text = comment_text
            TV_comment.setOnClickListener {
                (context as MainActivity).actualComment = comment
                (context as MainActivity).openFragment(CommentFragment())
            }

            val IB_del = projectView.findViewById<ImageButton>(R.id.delete_image)
            IB_del.setOnClickListener{
                val alert = AlertDialog.Builder(context)
                alert.setTitle(R.string.confirm)
                        .setMessage(getString(R.string.delete_comment) + "\n" + comment_text)
                        .setPositiveButton(R.string.yes){ dialog, _ ->
                            (context as MainActivity).deleteCommentOfProject(comment)
                            adapteurComments.notifyDataSetChanged()
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

        return inflater.inflate(R.layout.fragment_see, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        B_add = context.findViewById(R.id.button_add)
        LV = context.findViewById(R.id.list)

        when(context.seeWhat){
            "Comments" -> {
                comments = context.actualProject!!.myComments
                B_add.text = getString(R.string.add_comment)
                B_add.setOnClickListener {
                    context.actualComment = null
                    context.openFragment(CommentFragment())
                }

                adapteurComments = this.CommentsAdapter(context, comments)
                LV.adapter = adapteurComments
                context.title = "${context.actualProject.toString()} -> ${context.getString(R.string.my_comments)}"
            }
            "Rules" -> {
                rules = context.actualProject!!.myRules
                B_add.text = getString(R.string.add_rule)
                B_add.setOnClickListener {
                    context.actualRule = null
                    context.openFragment(RuleFragment())
                }

                adapteurRules = this.RulesAdapter(context, rules)
                LV.adapter = adapteurRules
                context.title = "${context.actualProject.toString()} -> ${context.getString(R.string.my_rules)}"
            }
        }
    }
}