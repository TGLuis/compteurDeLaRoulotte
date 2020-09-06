package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import library.Comment
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import java.util.*

class CommentFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== ADDCOMMENTFRAGMENT ====="
    private var comment: Comment? = null
    private var add: Boolean = true

    private lateinit var spinner_counters: Spinner
    private lateinit var editText_startLine: EditText
    private lateinit var editText_endLine: EditText
    private lateinit var imageButton_whichCounter: ImageButton
    private lateinit var editText_message: EditText
    private lateinit var button_cancel: Button
    private lateinit var button_save: Button
    private lateinit var expl: AlertDialog.Builder
    private lateinit var selectedItem: String

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

        comment = context.currentComment
        add = comment == null
        comment = if (add) Comment(context.getNextCommentIdentifiant(), 0, 0) else comment!!.clone()

        expl = AlertDialog.Builder(context)
        expl.setTitle(R.string.info)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)

        imageButton_whichCounter = context.findViewById(R.id.info_which_counter)
        imageButton_whichCounter.setOnClickListener {
            expl.setMessage(context.getString(R.string.help_which_counter)).create().show()
        }

        editText_startLine = context.findViewById(R.id.line_start)
        //ET_start_line.addTextChangedListener(CustomWatcher())

        editText_endLine = context.findViewById(R.id.line_end)
        //ET_end_line.addTextChangedListener(CustomWatcher())
        if (!add) {
            editText_endLine.setText(comment!!.end.toString())
            editText_startLine.setText(comment!!.start.toString())
        }

        spinner_counters = context.findViewById(R.id.the_counter)
        val arr = ArrayList<String>(context.currentProject!!.getCounters().size + 1)
        val the_proj = context.getString(R.string.the_project)
        arr.add(the_proj)
        context.currentProject!!.getCounters().forEach { arr.add(it.name) }
        selectedItem = the_proj
        if (!add) {
            selectedItem = comment!!.counter
        }
        val adaptor = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray()!!)
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_counters.adapter = adaptor
        spinner_counters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0)
                    comment!!.counter = arr[p2]
                else
                    comment!!.counter = ""
            }
        }
        if (!add) {
            val temp_selected = if (comment!!.counter == "") the_proj else comment!!.counter
            spinner_counters.setSelection(adaptor.getPosition(temp_selected))
        }

        editText_message = context.findViewById(R.id.noteText)
        if (!add) {
            editText_message.setText(comment!!.comment)
        }

        button_cancel = context.findViewById(R.id.button_cancel)
        button_cancel.setOnClickListener {
            context.onBackPressed()
        }

        button_save = context.findViewById(R.id.button_save)
        button_save.setOnClickListener {
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
            val prem = if (add) context.getString(R.string.pre_rule) else context.getString(R.string.pre_rule_modif)
            val mess = editText_message.text.toString()
            val to_display = prem + "\n" + context.getString(R.string.from_row) + " " + start.toString() + " " +
                    context.getString(R.string.to_row) + " " + end.toString() + "\n" + mess
            val dial = AlertDialog.Builder(context)
            dial.setTitle(R.string.confirm)
                    .setPositiveButton(R.string.save) { dialog, _ ->
                        if (add) {
                            context.addCommentToProject(comment!!)
                        } else {
                            context.updateComment(context.currentComment!!, comment!!)
                            context.currentProject!!.constructRappel()
                        }
                        context.onBackPressed()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setMessage(to_display)
                    .setCancelable(false)
                    .create()
                    .show()
        }

        context.title = context.getString(R.string.add_comment)
    }
}