package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Comment
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.lang.Exception
import java.util.ArrayList

class CommentFragment: Fragment() {
    private val TAG = "===== ADDCOMMENTFRAGMENT ====="
    private lateinit var context: MainActivity
    private var comment: Comment? = null
    private var add: Boolean = true

    private lateinit var S_counters: Spinner
    private lateinit var ET_start_line: EditText
    private lateinit var ET_end_line: EditText
    private lateinit var expl: AlertDialog.Builder
    private lateinit var selectedItem: String
    private lateinit var ET_mess: EditText
    private lateinit var B_cancel: Button
    private lateinit var B_save: Button

    /*inner class CustomWatcher: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            try {
                if (ET_end_line.text.toString().toInt() < ET_start_line.text.toString().toInt()) {
                    ET_end_line.setText(ET_start_line.text.toString())
            } catch (e: java.lang.NumberFormatException) {}
        }
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        comment = context.actualComment
        add = comment == null
        comment = if (add) Comment(0, 0, 0) else comment!!.clone()

        expl = AlertDialog.Builder(context)
        expl.setTitle(R.string.info)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)

        ET_start_line = context.findViewById(R.id.line_start)
        //ET_start_line.addTextChangedListener(CustomWatcher())

        ET_end_line = context.findViewById(R.id.line_end)
        //ET_end_line.addTextChangedListener(CustomWatcher())
        if(!add){
            ET_end_line.setText(comment!!.end.toString())
            ET_start_line.setText(comment!!.start.toString())
        }

        S_counters = context.findViewById(R.id.the_counter)
        val arr = ArrayList<String>(context.actualProject!!.getCounters().size+1)
        val the_proj = context.getString(R.string.the_project)
        arr.add(the_proj)
        context.actualProject!!.getCounters().forEach {arr.add(it.name)}
        selectedItem = the_proj
        if (!add) {
            selectedItem = comment!!.counter
        }
        val adaptor = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        S_counters.adapter = adaptor
        S_counters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0)
                    comment!!.counter = arr[p2]
                else
                    comment!!.counter = ""
            }
        }
        if (!add) {
            val temp_selected = if(comment!!.counter == "") the_proj else comment!!.counter
            S_counters.setSelection(adaptor.getPosition(temp_selected))
        }

        ET_mess = context.findViewById(R.id.noteText)
        if (!add) {
            ET_mess.setText(comment!!.comment)
        }

        B_cancel = context.findViewById(R.id.button_cancel)
        B_cancel.setOnClickListener {
            context.openFragment(context.frags.pop())
        }

        B_save = context.findViewById(R.id.button_save)
        B_save.setOnClickListener {
            val start = try { ET_start_line.text.toString().toInt() } catch (e: NumberFormatException) { 0 }
            val end = try { ET_end_line.text.toString().toInt() } catch (e: NumberFormatException) { 0 }
            comment!!.start = start
            if (start > end) {
                comment!!.end = start
                ET_end_line.setText(start.toString())
            } else {
                comment!!.end = end
            }
            comment!!.comment = ET_mess.text.toString()
            val prem = if(add) context.getString(R.string.pre_rule) else context.getString(R.string.pre_rule_modif)
            val mess = ET_mess.text.toString()
            val to_display = prem + "\n" + context.getString(R.string.from_row) + " " + start.toString() + " " +
                    context.getString(R.string.to_row) + " " + end.toString() + "\n" + mess
            val dial = AlertDialog.Builder(context)
            dial.setTitle(R.string.confirm)
                    .setPositiveButton(R.string.save) { dialog, _ ->
                        if (add){
                            context.addCommentToProject(comment!!)
                        }else{
                            context.updateComment(context.actualComment!!, comment!!)
                            context.actualProject!!.constructRappel()
                        }
                        context.openFragment(context.frags.pop())
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

        context.actualFragment = CommentFragment()
        context.title = context.getString(R.string.add_comment)
    }
}