package fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.github.barteksc.pdfviewer.PDFView
import library.Counter
import library.Project
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import java.lang.Math.abs
import java.util.*


class ProjectFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "== PROJECTFRAGMENT =="
    private lateinit var project: Project

    private lateinit var wholeLayout: ConstraintLayout
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
    private lateinit var mpPlus: MediaPlayer
    private lateinit var mpMoins: MediaPlayer
    private lateinit var pdfView: PDFView
    lateinit var adapteur: CounterAdapter
    private var previousMessage: String? = null
    private var PDFHeight: Int = 0

    class Tuple(t: TextView, c: Counter) {
        var t: TextView? = t
        var c: Counter? = c
    }

    inner class CounterAdapter(context: MainActivity, list: ArrayList<Counter>) : ArrayAdapter<Counter>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView? = null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val count = super.getItem(position)!!
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null) {
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_counter_item, parent, false)
                viewHolder.msg = projectView.findViewById(R.id.counter_name)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }
            viewHolder.msg!!.text = count.name

            names.add(Tuple(projectView.findViewById(R.id.counter_name), count))

            val nb = projectView.findViewById<TextView>(R.id.count)
            nombres.add(Tuple(nb, count))
            nb.text = count.toDisplay()

            val linked = projectView.findViewById<ImageView>(R.id.linked)
            if (count.attachedMain) {
                DrawableCompat.setTint(linked.drawable, ContextCompat.getColor(context, R.color.linkOn))
            }

            val buttonM = projectView.findViewById<Button>(R.id.button_minus)
            buttonM.setOnClickListener {
                if ((context as MainActivity).volumeOn)
                    mpMoins.start()
                up(false, count)
            }

            val buttonP = projectView.findViewById<Button>(R.id.button_plus)
            buttonP.setOnClickListener {
                if ((context as MainActivity).volumeOn)
                    mpPlus.start()
                up(true, count)
            }

            val comment = projectView.findViewById<TextView>(R.id.comment_of_the_counter)
            val com = project.getMessageForCounter(count, false)
            if (com == null) {
                comment.text = ""
                comment.maxHeight = 0
            } else {
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
            } else if (count.attachedMain && (b || (!b && count.etat > 0))) {
                project.update(b)
            } else if (b || (!b && count.etat > 0)) {
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
        if (context.currentProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        wholeLayout = context.findViewById(R.id.id_fragment_project)
        wholeLayout.viewTreeObserver
                .addOnGlobalLayoutListener { getAndSetHeight() }

        mpPlus = MediaPlayer.create(context, R.raw.plus)
        mpMoins = MediaPlayer.create(context, R.raw.minus)

        project = context.currentProject!!

        nombres = ArrayList()
        names = ArrayList()

        warning = AlertDialog.Builder(context)
        warning.setTitle(R.string.warning)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
        comment = context.findViewById(R.id.message)

        counters = project.getCounters()
        addCounter = AlertDialog.Builder(context)

        nombre = context.findViewById(R.id.count)
        nombre.text = context.currentProject!!.etat.toString()

        buttonMinus = context.findViewById(R.id.button_minus)
        buttonMinus.setOnClickListener {
            if (project.etat > 0) {
                if (context.volumeOn)
                    mpMoins.start()
                up(false)
            }
        }

        buttonPlus = context.findViewById(R.id.button_plus)
        buttonPlus.setOnClickListener {
            if (context.volumeOn)
                mpPlus.start()
            up(true)
        }

        counters.sortWith { a, b -> a.order - b.order }
        listViewCounters = context.findViewById(R.id.listCounters)
        adapteur = this.CounterAdapter(context, counters)
        listViewCounters.adapter = adapteur

        pdfView = context.findViewById(R.id.pdfView)
        if (context.pdfIsOpen && project.pdf != null) {
            pdfView.visibility = View.VISIBLE
            try {
                pdfView.fromUri(project.pdf)
                        .defaultPage(0)
                        .spacing(10)
                        .load()
            } catch (e: Exception) {
                Toast.makeText(context, R.string.problem_with_pdf, Toast.LENGTH_LONG).show()
                project.pdf = null
            }
        }

        affiche(true)
        context.setMenu("project")
        context.title = project.toString()
    }

    /**
     * When we update the state of the main counter
     * @param:b if it is true -> increment
     *          if it is false -> decrement
     */
    private fun up(b: Boolean) {
        project.update(b)
        nombre.text = project.etat.toString()
        nombres.forEach { it.t!!.text = it.c!!.toDisplay() }
        affiche()
    }


    /**
     * This function will handle all the message for every counters and for the main.
     * It will also handle if you need to display a warning in the case where a message appear
     * for the first time.
     * @param:force is used to force the warning to display all the messages. In the case where the
     *        screen ProjectFragment is open for the first time.
     */
    fun affiche(force: Boolean = false) {
        val mess = project.getMessageAll()
        if (force && mess != null) {
            warn(mess)
            comment.text = mess
        } else if (mess != null) {
            if (previousMessage == null) {
                warn(mess)
            } else {
                /* Find what's different from last message to only warn the new part */
                var completeMess = ""
                val otherMess = project.getMessageForMain()
                if (otherMess != null && !previousMessage!!.contains(otherMess)) {
                    completeMess += otherMess
                }
                counters.forEach {
                    val messC = project.getMessageForCounter(it, true)
                    if (messC != null && !previousMessage!!.contains(messC)) {
                        completeMess += messC
                    }
                }
                if (completeMess != "") warn(completeMess)
            }
            comment.text = mess
        } else {
            comment.text = ""
        }
        previousMessage = mess
    }

    /**
     * Display in warning the string @param:mess.
     */
    private fun warn(mess: String) {
        warning.setMessage(mess)
                .create()
                .show()
    }

    private fun getAndSetHeight() {
        val mainCounterLayout = context.findViewById<ConstraintLayout>(R.id.MCounter)
        if (fragmentManager != null) {
            val currentfrag = fragmentManager!!.findFragmentByTag(TAG)
            if (currentfrag != null && currentfrag.isVisible) {
                val view = context.findViewById<ListView>(R.id.listCounters)
                val params = view.layoutParams
                if (params is ViewGroup.MarginLayoutParams) {
                    val p: ViewGroup.MarginLayoutParams = params
                    if (project.pdf == null || !context.pdfIsOpen) {
                        p.setMargins(5, mainCounterLayout.height + 2, 5, 5)
                        pdfView.visibility = View.INVISIBLE
                        pdfView.layoutParams.height = 0
                    } else {
                        pdfView.visibility = View.VISIBLE
                        if (PDFHeight == 0) {
                            if (abs(pdfView.layoutParams.height - (view.height + pdfView.height) * 2 / 3) < 2) {
                                PDFHeight = pdfView.layoutParams.height
                            }
                            pdfView.layoutParams.height = (view.height + pdfView.height) * 2 / 3
                        }
                        p.setMargins(5, mainCounterLayout.height + 2, 5, pdfView.height)
                    }
                    view.layoutParams = p
                }
                view.requestLayout()
            }
        }
    }
}
