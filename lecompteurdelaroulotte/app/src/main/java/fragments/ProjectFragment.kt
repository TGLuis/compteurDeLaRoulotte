package fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import library.Counter
import library.Project
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.util.*
import android.util.DisplayMetrics
import android.support.constraint.ConstraintLayout.LayoutParams as LayoutParams1
import android.widget.RelativeLayout




class ProjectFragment: Fragment() {
    private val TAG = "== PROJECTFRAGMENT =="
    private lateinit var context: MainActivity
    private lateinit var project: Project

    private lateinit var mCounter: ConstraintLayout
    private lateinit var listViewCounters: ListView
    private lateinit var buttonPlus: Button
    private lateinit var buttonMinus: Button
    private lateinit var counters: ArrayList<Counter>
    private lateinit var nombre: TextView
    private lateinit var addCounter: AlertDialog.Builder
    private lateinit var nombres: ArrayList<Tuple>
    private lateinit var warning: AlertDialog.Builder
    private lateinit var comment: TextView
    private lateinit var names: ArrayList<Tuple>
    lateinit var adapteur: CounterAdapter
    var previous_message: String? = null

    inner class Tuple(t: TextView, c: Counter) {
        var t: TextView? = t
        var c: Counter? = c
    }

    inner class CounterAdapter(context: MainActivity, list: ArrayList<Counter>) : ArrayAdapter<Counter>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val count = super.getItem(position)!!
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

            names.add(Tuple(projectView.findViewById(R.id.counter_name),count))

            val nb = projectView.findViewById<TextView>(R.id.count)
            nombres.add(Tuple(nb,count))
            val state = if(count.max == 0) count.etat else count.etat%count.max+1
            nb.text = state.toString()

            val linked = projectView.findViewById<ImageView>(R.id.linked)
            if(count.attachedMain) linked.setImageDrawable(context.getDrawable(R.drawable.gear_color))

            val buttonM = projectView.findViewById<Button>(R.id.button_minus)
            buttonM.setOnClickListener{
                up(false, count)
            }

            val buttonP = projectView.findViewById<Button>(R.id.button_plus)
            buttonP.setOnClickListener{
                up(true, count)
            }

            val comment = projectView.findViewById<TextView>(R.id.comment_of_the_counter)
            val com = project.getMessageForCounter(count, false)
            if(com == null){
                comment.text = ""
                comment.maxHeight = 0
            }else{
                comment.text = com
                comment.maxHeight = 10000
            }

            return projectView
        }

        /**
         * When we update the state of the counter @param:count
         * @param:b if it is true -> increment
         *          if it is false -> decrement
         */
        private fun up(b: Boolean, count: Counter) {
            if (!b && count.attachedMain && project.etat == 0) {
                Toast.makeText(context, R.string.problem_etat_nul, Toast.LENGTH_LONG).show()
            } else if (count.attachedMain && (b || (!b && count.etat > 0))){
                project.update(b)
            } else if (b || (!b && count.etat > 0)){
                count.update(b)
            }
            nombre.text = project.etat.toString()
            adapteur.notifyDataSetChanged()
            affiche()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context.actualProject == null) {
            context.openFragment(HomeFragment())
        }
        else {
            mCounter = context.findViewById(R.id.MCounter)
            /**
             * We have to put a listerner on the layout to wait the rendering of the screen to get the
             * height of each element
             */
            context.findViewById<ConstraintLayout>(R.id.id_fragment_project)
                    .viewTreeObserver
                    .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            mCounter.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            getAndSetHeight()
                        }
                    })

            project = context.actualProject!!

            nombres = ArrayList()
            names = ArrayList()

            warning = AlertDialog.Builder(context)
            warning.setTitle(R.string.warning)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
            comment = context.findViewById(R.id.message)

            counters = project.getCounters()
            addCounter = AlertDialog.Builder(context)

            nombre = context.findViewById(R.id.count)
            nombre.text = context.actualProject!!.etat.toString()

            buttonMinus = context.findViewById(R.id.button_minus)
            buttonMinus.setOnClickListener {
                if (project.etat > 0) {
                    up(false)
                }
            }

            buttonPlus = context.findViewById(R.id.button_plus)
            buttonPlus.setOnClickListener {
                up(true)
            }

            counters.sortWith(Comparator { a, b -> a.order - b.order })
            listViewCounters = context.findViewById(R.id.listCounters)
            adapteur = this.CounterAdapter(context, counters)
            listViewCounters.adapter = adapteur

            //Log.e(TAG, project.allRapel())

            affiche(true)
            context.setMenu("project")
            context.title = project.toString()
        }
    }

    /**
     * When we update the state of the main counter
     * @param:b if it is true -> increment
     *          if it is false -> decrement
     */
    private fun up(b: Boolean){
        project.update(b)
        nombre.text = project.etat.toString()
        nombres.forEach {
            val state = if(it.c!!.max == 0) it.c!!.etat else it.c!!.etat%it.c!!.max+1
            it.t!!.text = state.toString()
        }
        affiche()
    }


    /**
     * This function will handle all the message for every counters and for the main.
     * It will also handle if you need to display a warning in the case where a message appear
     * for the first time.
     * @param:force is used to force the warning to display all the messages. In the case where the
     *        screen ProjectFragment is open for the first time.
     */
    fun affiche(force: Boolean = false){
        val mess = project.getMessageAll()
        if(force && mess != null){
            warn(mess)
            comment.text = mess
        }else if(mess != null){
            if (previous_message == null){
                warn(mess)
            }else{
                var completeMess = ""
                val otherMess = project.getMessageForMain()
                if(otherMess != null && !previous_message!!.contains(otherMess)){
                    completeMess += otherMess
                }
                counters.forEach {
                    val messC = project.getMessageForCounter(it, true)
                    if(messC != null && !previous_message!!.contains(messC)){
                        completeMess += messC
                    }
                }
                if(completeMess != "") warn(completeMess)
            }
            comment.text = mess
        }else{
            comment.text = ""
        }
        previous_message = mess
        getAndSetHeight()
    }

    /**
     * Display in warning the string @param:mess.
     */
    private fun warn(mess: String){
        warning.setMessage(mess)
                .create()
                .show()
    }

    private fun getAndSetHeight(){
        // todo bug here doesn't load the good height with mCounter.height -> 1 temps en retard :/
        mCounter.requestLayout()
        val view = context.findViewById<ListView>(R.id.listCounters)
        val params = view.layoutParams
        if(params is ViewGroup.MarginLayoutParams){
            val p: ViewGroup.MarginLayoutParams = params as ViewGroup.MarginLayoutParams
            p.setMargins(5, mCounter.height+2, 5, 5)
        }
        view.requestLayout()
    }
}
