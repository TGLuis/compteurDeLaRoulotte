package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import library.Project
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R

class NotesFragment: Fragment() {
    private val TAG = "===== MAINFRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var project: Project
    private lateinit var ET_notes: EditText

    inner class CustomWatcher: TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            context.actualProject!!.notes = s.toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context.actualProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        project = context.actualProject!!
        val notes = project.notes

        ET_notes = context.findViewById(R.id.noteText)
        ET_notes.run {
            setText(notes)
            addTextChangedListener(CustomWatcher())
        }
        context.title = context.getString(R.string.edit_notes)
    }
}
