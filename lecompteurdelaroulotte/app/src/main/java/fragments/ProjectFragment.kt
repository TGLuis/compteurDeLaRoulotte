package fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Counter
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R

class ProjectFragment: Fragment() {
    private val TAG = "===== PROJECTRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var listViewCounters: ListView
    private lateinit var buttonEditNotes: Button
    private lateinit var buttonEditRules: Button
    private lateinit var buttonAddCounter: Button
    private lateinit var buttonPlus: Button
    private lateinit var buttonMinus: Button
    private lateinit var counters: ArrayList<Counter>

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

        counters = context.actualProject!!.getCounters()

        buttonMinus = context.findViewById(R.id.button_minus)
        buttonMinus.setOnClickListener({
            //TODO
        })

        buttonPlus = context.findViewById(R.id.button_plus)
        buttonPlus.setOnClickListener({
            //TODO
        })

        listViewCounters = context.findViewById(R.id.listCounters)
        val adapteur = this.CounterAdapter(context, counters)
        listViewCounters.adapter = adapteur
        listViewCounters.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            //TODO
        }

        buttonEditNotes = context.findViewById(R.id.button_notes)
        buttonEditNotes.setOnClickListener({
            //TODO
        })

        buttonEditRules = context.findViewById(R.id.button_rules)
        buttonEditRules.setOnClickListener({
            //TODO
        })

        buttonAddCounter = context.findViewById(R.id.button_add_counter)
        buttonAddCounter.setOnClickListener({
            //TODO
        })
    }
}