package fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import library.Comment
import library.Rule
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import java.util.ArrayList

class SeeFragment: MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== SEEFRAGMENT ====="

    private lateinit var listView: ListView
    private lateinit var button_add: Button
    private lateinit var comments: ArrayList<Comment>
    private lateinit var rules: ArrayList<Rule>
    private lateinit var adapteurComments: CommentsAdapter
    private lateinit var adapteurRules: RulesAdapter

    inner class CommentsAdapter(context: Context, list: ArrayList<Comment>) : ArrayAdapter<Comment>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val comment = super.getItem(position)!!
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
            val comment_text = comment.getString(activity as MainActivity)
            TV_comment.text = comment_text
            TV_comment.setOnClickListener {
                (context as MainActivity).currentComment = comment
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
        if (context.currentProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        button_add = context.findViewById(R.id.button_add)
        listView = context.findViewById(R.id.list)

        when(context.seeWhat){
            "Comments" -> {
                comments = context.currentProject!!.myComments
                button_add.text = getString(R.string.add_comment)
                button_add.setOnClickListener {
                    context.currentComment = null
                    context.openFragment(CommentFragment())
                }

                adapteurComments = this.CommentsAdapter(context, comments)
                listView.adapter = adapteurComments
                context.title = "${context.currentProject.toString()} -> ${context.getString(R.string.my_comments)}"
            }
            "Rules" -> {
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
    }
}