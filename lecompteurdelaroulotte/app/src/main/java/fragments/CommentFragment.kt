package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Comment
import library.Dialogs
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import kotlin.collections.ArrayList

class CommentFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== ADDCOMMENTFRAGMENT ====="
    private var comment: Comment? = null
    private var addingAComment: Boolean = true

    private lateinit var spinner_counters: Spinner
    private lateinit var editText_startLine: EditText
    private lateinit var editText_endLine: EditText
    private lateinit var imageButton_whichCounter: ImageButton
    private lateinit var editText_message: EditText
    private lateinit var button_cancel: Button
    private lateinit var button_save: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context.currentProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        imageButton_whichCounter = context.findViewById(R.id.info_which_counter)
        editText_startLine = context.findViewById(R.id.line_start)
        editText_endLine = context.findViewById(R.id.line_end)
        spinner_counters = context.findViewById(R.id.the_counter)
        editText_message = context.findViewById(R.id.noteText)
        button_cancel = context.findViewById(R.id.button_cancel)
        button_save = context.findViewById(R.id.button_save)

        comment = context.currentComment
        addingAComment = comment == null
        // if not addingAComment it means the user actually modify an existing one
        comment = if (addingAComment) Comment(context.getNextCommentIdentifiant(), 0, 0) else comment!!.clone()

        imageButton_whichCounter.setOnClickListener {
            Dialogs.displayInfoDialog(context, R.string.help_which_counter)
        }

        val theProject = context.getString(R.string.the_project)
        val arr = listOfCounters(theProject)

        val adaptor = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_counters.adapter = adaptor
        spinner_counters.onItemSelectedListener = getItemListener(arr)

        if (!addingAComment) {
            val tempSelected = if (comment!!.counter == "") theProject else comment!!.counter
            spinner_counters.setSelection(adaptor.getPosition(tempSelected))
            editText_message.setText(comment!!.comment)
            editText_startLine.setText(comment!!.start.toString())
            editText_endLine.setText(comment!!.end.toString())
        }

        button_cancel.setOnClickListener {
            context.onBackPressed()
        }

        button_save.setOnClickListener {
            Dialogs.displayConfirmationDialog(context, buildMessage(), ::save)
        }

        context.title = context.getString(R.string.add_comment)  // todo what if modify??
    }

    private fun buildMessage(): String {
        val start = try {
            editText_startLine.text.toString().toInt()
        } catch (e: NumberFormatException) {
            0
        }
        val end = try {
            editText_endLine.text.toString().toInt()
        } catch (e: NumberFormatException) {
            0
        }
        comment!!.start = start
        if (start > end) {
            comment!!.end = start
            editText_endLine.setText(start.toString())
        } else {
            comment!!.end = end
        }
        comment!!.comment = editText_message.text.toString()
        val prem = if (addingAComment) context.getString(R.string.pre_rule) else context.getString(R.string.pre_rule_modif)
        val mess = editText_message.text.toString()
        return prem + "\n" + context.getString(R.string.from_row) + " " + start.toString() + " " +
                context.getString(R.string.to_row) + " " + end.toString() + "\n" + mess
    }

    private fun save() {
        if (addingAComment) {
            context.addCommentToProject(comment!!)
        } else {
            context.updateComment(context.currentComment!!, comment!!)
            context.currentProject!!.constructRappel()
        }
        context.onBackPressed()
    }

    private fun listOfCounters(theProject: String): ArrayList<String> {
        val array = ArrayList<String>(context.currentProject!!.getCounters().size + 1)
        array.add(0, theProject)
        context.currentProject!!.getCounters().forEach { array.add(it.name) }
        return array
    }

    private fun getItemListener(array: ArrayList<String>): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0)
                    comment!!.counter = array[p2]
                else
                    comment!!.counter = ""
            }
        }
    }
}