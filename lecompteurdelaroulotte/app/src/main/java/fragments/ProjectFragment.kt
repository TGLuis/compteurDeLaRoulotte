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
    private val TAG = "===== PROJECTRAGMENT ====="
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

    inner class CounterAdapter(context: Context, list: ArrayList<Counter>) : ArrayAdapter<Counter>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val counte = super.getItem(position)
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null){
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_counter_item, parent, false)
                //viewHolder.msg = projectView.findViewById(R.id.counter_text)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }
            //viewHolder.msg!!.text = counte.toString()
            return projectView
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

        counters = project.getCounters()
        addCounter = AlertDialog.Builder(context)

        nombre = context.findViewById(R.id.count)
        nombre.text = context.actualProject!!.etat.toString()

        buttonMinus = context.findViewById(R.id.button_minus)
        buttonMinus.setOnClickListener{
            if (project.etat > 0){
                project.update(false)
                nombre.text = project.etat.toString()
            }
        }

        buttonPlus = context.findViewById(R.id.button_plus)
        buttonPlus.setOnClickListener{
            project.update(true)
            nombre.text = project.etat.toString()
        }

        listViewCounters = context.findViewById<ListView>(R.id.listCounters)
        val adapteur = this.CounterAdapter(context, counters)
        listViewCounters.adapter = adapteur
        listViewCounters.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            //TODO
        }

        buttonEditNotes = context.findViewById(R.id.button_notes)
        buttonEditNotes.setOnClickListener{
            //TODO
        }

        buttonEditRules = context.findViewById(R.id.button_rules)
        buttonEditRules.setOnClickListener{
            //TODO
        }

        buttonAddCounter = context.findViewById(R.id.button_add_counter)
        buttonAddCounter.setOnClickListener{
            val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, view as ViewGroup, false)
            addCounter.setView(viewInflated)
                    .setTitle(R.string.counter_name_id)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        val counterName = viewInflated.input_text.text.toString()
                        //Toast.makeText(context,"something", Toast.LENGTH_SHORT).show()
                        context.createCounter(counterName)
                        adapteur.notifyDataSetChanged()
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
}