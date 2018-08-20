package fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.simple_text_input.view.*
import library.Counter
import library.Project
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.util.*

class ProjectFragment: Fragment() {
    private val TAG = "===== PROJECTFRAGMENT ====="
    private lateinit var context: MainActivity
    private lateinit var project: Project

    private lateinit var listViewCounters: ListView
    private lateinit var buttonEditNotes: Button
    private lateinit var buttonEditRules: Button
    private lateinit var buttonAddCounter: Button
    private lateinit var buttonPlus: Button
    private lateinit var buttonMinus: Button
    private lateinit var counters: ArrayList<Counter>
    private lateinit var nombre: TextView
    private lateinit var addCounter: AlertDialog.Builder
    private lateinit var nombres: ArrayList<Tuple>

    private lateinit var warning: AlertDialog.Builder
    private lateinit var comment: TextView

    inner class Tuple {
        var t: TextView? = null
        var c: Counter? = null

        constructor(t: TextView, c: Counter){
            this.t = t
            this.c = c
        }
    }

    inner class CounterAdapter(context: Context, list: ArrayList<Counter>) : ArrayAdapter<Counter>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val count = super.getItem(position)
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null){
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_counter_item, parent, false)
                viewHolder.msg = projectView.findViewById(R.id.counter_name)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }
            viewHolder.msg!!.text = count.name

            val nb = projectView.findViewById<TextView>(R.id.count)
            nombres.add(Tuple(nb,count))
            nb.text = count.etat.toString()

            val param = projectView.findViewById<ImageButton>(R.id.parameter)
            param.setOnClickListener{openCount(count)}

            val buttonM = projectView.findViewById<Button>(R.id.button_minus)
            buttonM.setOnClickListener{
                up(false, count)
            }

            val buttonP = projectView.findViewById<Button>(R.id.button_plus)
            buttonP.setOnClickListener{
                up(true, count)
            }

            return projectView
        }

        fun up(b: Boolean, count: Counter) {
            if (!b && count.attachedMain && project.etat == 0) {
                Toast.makeText(context, R.string.problem_etat_nul, Toast.LENGTH_LONG).show()
            } else if (count.attachedMain && (b || (!b && count.etat > 0))){
                project.update(b)
            } else if (b || (!b && count.etat > 0)){
                count.update(b)
            }
            nombre.text = project.etat.toString()
            nombres.forEach { it.t!!.text = it.c!!.etat.toString() }

            val mess = project.getMessageRule()
            if (mess != null){
                warn(mess)
            }else{
                comment.text = ""
            }
        }

        fun openCount(counter: Counter){
            (context as MainActivity).actualCounter = counter
            (context as MainActivity).frags.push(ProjectFragment())
            (context as MainActivity).openFragment(CounterFragment())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        project = context.actualProject!!

        nombres = ArrayList<Tuple>()

        warning = AlertDialog.Builder(context)
        warning.setTitle(R.string.warning)
                .setPositiveButton(R.string.ok){ dialog, which ->
                    dialog.dismiss()
                }
        comment = context.findViewById(R.id.message)

        counters = project.getCounters()
        addCounter = AlertDialog.Builder(context)

        nombre = context.findViewById(R.id.count)
        nombre.text = context.actualProject!!.etat.toString()

        buttonMinus = context.findViewById(R.id.button_minus)
        buttonMinus.setOnClickListener{
            if (project.etat > 0){
                up(false)
            }
        }

        buttonPlus = context.findViewById(R.id.button_plus)
        buttonPlus.setOnClickListener{
            up(true)
        }

        listViewCounters = context.findViewById(R.id.listCounters)
        val adapteur = this.CounterAdapter(context, counters)
        listViewCounters.adapter = adapteur

        buttonEditNotes = context.findViewById(R.id.button_notes)
        buttonEditNotes.setOnClickListener{
            context.frags.push(ProjectFragment())
            context.openFragment(NotesFragment())
        }

        buttonEditRules = context.findViewById(R.id.button_rules)
        buttonEditRules.setOnClickListener{
            context.frags.push(ProjectFragment())
            context.openFragment(SeeRuleFragment())
        }

        buttonAddCounter = context.findViewById(R.id.button_add_counter)
        buttonAddCounter.setOnClickListener{
            val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, view as ViewGroup, false)
            addCounter.setView(viewInflated)
                    .setTitle(R.string.counter_name_id)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        val counterName = viewInflated.input_text.text.toString()
                        if (project.has_counter(counterName)){
                            Toast.makeText(context,R.string.counter_already, Toast.LENGTH_SHORT).show()
                        }else{
                            context.createCounter(counterName)
                            adapteur.notifyDataSetChanged()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }

        if(Locale.getDefault().language == "fr"){
            val size = resources.getDimension(R.dimen.french_text_size)*0.38F // correspond +- à 16sp
            buttonAddCounter.textSize = size
            buttonEditRules.textSize = size
        }

        context.title = project.toString()
    }

    fun up(b: Boolean){
        project.update(b)
        nombre.text = project.etat.toString()
        nombres.forEach { it.t!!.text = it.c!!.etat.toString() }

        val mess = project.getMessageRule()
        if (mess != null){
            warn(mess)
        }else{
            comment.text = ""
        }
    }

    fun warn(mess: String){
        warning.setMessage(mess)
                .create()
                .show()
        comment.text = mess
    }
}