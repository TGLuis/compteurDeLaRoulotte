package fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Rule
import library.Step
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.util.ArrayList

class AddRuleFragment: Fragment(){
    private val TAG = "===== ADDRULESFRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var LV_steps: ListView
    private lateinit var steps: ArrayList<Step>
    private lateinit var all_steps: ArrayList<Step>
    private lateinit var CB_startNow: CheckBox
    private lateinit var ET_otherStart: EditText
    private lateinit var CB_augm: CheckBox
    private lateinit var IB_infoStart: ImageButton
    private lateinit var IB_infoAugm: ImageButton
    private lateinit var expl: AlertDialog.Builder

    private lateinit var B_cancel: Button
    private lateinit var B_add_step: Button
    private lateinit var B_save: Button

    inner class CustomWatcher: TextWatcher{
        var st: Step
        var i: Int

        constructor(st: Step, i: Int){
            this.st = st
            this.i = i
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            when(i){
                1 -> st.one = s.toString().toInt()
                2 -> st.two = s.toString().toInt()
                3 -> st.three = s.toString().toInt()
            }
        }
    }


    inner class StepsAdapter(context: Context, list: ArrayList<Step>) : ArrayAdapter<Step>(context, 0, list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val step = super.getItem(position)
            val projectView: View
            if (convertView == null){
                projectView = LayoutInflater.from(context).inflate(R.layout.list_step_item, parent, false)
            } else {
                projectView = convertView
            }
            val a = projectView.findViewById<EditText>(R.id.enter_fois)
            a.setText(step.one.toString())
            a.addTextChangedListener(CustomWatcher(step,1))
            val b = projectView.findViewById<EditText>(R.id.enter_rows)
            b.setText(step.two.toString())
            b.addTextChangedListener(CustomWatcher(step,2))
            val c = projectView.findViewById<EditText>(R.id.enter_stitches)
            c.setText(step.three.toString())
            c.addTextChangedListener(CustomWatcher(step,3))

            return projectView
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_add_rule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        expl = AlertDialog.Builder(context)
        expl.setTitle(R.string.info)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }

        CB_startNow = context.findViewById(R.id.begin_now)
        ET_otherStart = context.findViewById(R.id.begin_other)
        CB_augm = context.findViewById(R.id.augm_check)

        IB_infoStart = context.findViewById(R.id.info_start)
        IB_infoStart.setOnClickListener {
            //expl.setMessage(R.string.help_start).create().show()
        }

        IB_infoAugm = context.findViewById(R.id.info_augm)
        IB_infoAugm.setOnClickListener {
            //expl.setMessage(R.string.help_augm).create().show()
        }

        val first_step = Step(0,0,0)
        val a = context.findViewById<EditText>(R.id.enter_fois)
        a.setText(first_step.one.toString())
        a.addTextChangedListener(CustomWatcher(first_step,1))
        val b = context.findViewById<EditText>(R.id.enter_rows)
        b.setText(first_step.two.toString())
        b.addTextChangedListener(CustomWatcher(first_step,2))
        val c = context.findViewById<EditText>(R.id.enter_stitches)
        c.setText(first_step.three.toString())
        c.addTextChangedListener(CustomWatcher(first_step,3))

        all_steps = ArrayList<Step>()
        all_steps.add(first_step)
        steps = ArrayList<Step>()

        LV_steps = context.findViewById(R.id.listSteps)
        val adapteur = this.StepsAdapter(context, steps)
        LV_steps.adapter = adapteur

        B_cancel = context.findViewById(R.id.button_cancel)
        B_cancel.setOnClickListener{
            context.openFragment(context.frags.pop())
        }

        B_add_step = context.findViewById(R.id.button_add_step)
        B_add_step.setOnClickListener {
            val s = Step(0,0,0)
            all_steps.add(s)
            steps.add(s)
            adapteur.notifyDataSetChanged()
        }

        B_save = context.findViewById(R.id.button_save)
        B_save.setOnClickListener {
            //TODO: c'est ici que le bat blesse
            //all_steps contient l'arrayList avec toutes les steps!! normalement à jour
        }

        context.title = context.getString(R.string.add_rule)
    }
}