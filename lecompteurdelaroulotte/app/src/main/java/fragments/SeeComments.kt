package fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import library.Comment
import library.Dialogs
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import java.util.ArrayList

class SeeComments: SeeFragment() {
    override var TAG: String = "===== SEECOMMENTS ====="

    private lateinit var comments: ArrayList<Comment>
    private lateinit var adapteurComments: CommentsAdapter
    private lateinit var selectedComment: Comment

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

            val textView_comment = projectView.findViewById<TextView>(R.id.text_comment)
            val comment_text = comment.getString(activity as MainActivity)
            textView_comment.text = comment_text
            textView_comment.setOnClickListener {
                (context as MainActivity).currentComment = comment
                (context as MainActivity).openFragment(CommentFragment())
            }

            val imageButton_delete = projectView.findViewById<ImageButton>(R.id.delete_image)
            imageButton_delete.setOnClickListener{
                selectedComment = comment
                Dialogs.displayConfirmationDialog(context, getString(R.string.delete_comment) + "\n" + comment_text, ::deleteComment)
            }

            return projectView
        }

        private fun deleteComment() {
            (context as MainActivity).deleteCommentOfProject(selectedComment)
            adapteurComments.notifyDataSetChanged()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
}
