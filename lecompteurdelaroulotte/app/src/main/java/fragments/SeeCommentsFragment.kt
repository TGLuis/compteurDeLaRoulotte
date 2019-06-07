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
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.util.ArrayList

class SeeCommentsFragment: Fragment() {
    private val TAG = "===== SEECOMMENTSFRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var LV_comments: ListView
    private lateinit var B_addComment: Button
    private lateinit var comments: ArrayList<Comment>
    private lateinit var adapteur: CommentsAdapter

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
                (context as MainActivity).frags.add(SeeCommentsFragment())
                (context as MainActivity).openFragment(CommentFragment())
            }

            val IB_del = projectView.findViewById<ImageButton>(R.id.delete_image)
            IB_del.setOnClickListener{
                val alert = AlertDialog.Builder(context)
                alert.setTitle(R.string.confirm)
                        .setMessage(getString(R.string.delete_comment) + "\n" + comment_text)
                        .setPositiveButton(R.string.yes){ dialog, _ ->
                            (context as MainActivity).deleteCommentOfProject(comment)
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

        return inflater.inflate(R.layout.fragment_see_comments, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        comments = context.actualProject!!.myComments

        B_addComment = context.findViewById(R.id.button_add_comment)
        B_addComment.setOnClickListener {
            context.actualComment = null
            context.frags.add(SeeCommentsFragment())
            context.openFragment(CommentFragment())
        }

        LV_comments = context.findViewById(R.id.listComments)
        adapteur = this.CommentsAdapter(context, comments)
        LV_comments.adapter = adapteur

        context.actualFragment = SeeCommentsFragment()
        context.title = "${context.actualProject.toString()} -> ${context.getString(R.string.my_comments)}"
    }
}