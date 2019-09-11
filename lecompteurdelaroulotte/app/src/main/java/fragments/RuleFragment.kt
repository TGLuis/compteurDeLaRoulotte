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
import java.lang.Exception
import java.util.ArrayList

class RuleFragment: Fragment(){
    private val TAG = "===== ADDRULESFRAGMENT ====="
    private lateinit var context: MainActivity
    private var rule: Rule? = null
    private var add: Boolean = true

    private lateinit var LV_steps: ListView
    private lateinit var steps: ArrayList<Step>
    private lateinit var CB_startNow: CheckBox
    private lateinit var ET_otherStart: EditText
    private lateinit var S_counters: Spinner
    private lateinit var ET_comment: EditText
    private lateinit var IB_infoStart: ImageButton
    private lateinit var IB_infoStep: ImageButton
    private lateinit var IB_infoCounter: ImageButton
    private lateinit var IB_infoComment: ImageButton
    private lateinit var expl: AlertDialog.Builder
    private lateinit var selectedItem: String

    private lateinit var B_cancel: Button
    private lateinit var B_add_step: Button
    private lateinit var B_save: Button

    private lateinit var adapteur: StepsAdapter

    inner class CustomWatcher(private var st: Step, private var i: Int) : TextWatcher{

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            when(i){
                1 -> try {st.one =   s.toString().toInt()} catch(e: NumberFormatException){}
                2 -> try {st.two =   s.toString().toInt()} catch(e: NumberFormatException){}
                3 -> try {st.three = s.toString().toInt()} catch(e: NumberFormatException){}
            }
        }
    }

    inner class StepsAdapter(context: Context, list: ArrayList<Step>) : ArrayAdapter<Step>(context, 0, list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val step = super.getItem(position)
            val projectView: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_step_item, parent, false)

            val a = projectView.findViewById<EditText>(R.id.enter_fois)
            a.hint = step.one.toString()
            a.addTextChangedListener(CustomWatcher(step,1))
            val b = projectView.findViewById<EditText>(R.id.enter_rows)
            b.hint = step.two.toString()
            b.addTextChangedListener(CustomWatcher(step,2))
            val c = projectView.findViewById<EditText>(R.id.enter_stitches)
            c.hint = step.three.toString()
            c.addTextChangedListener(CustomWatcher(step,3))

            val augment = projectView.findViewById<CheckBox>(R.id.augm_check)
            augment.isChecked = step.augm
            augment.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
                step.augm = b
            }

            val IB_del = projectView.findViewById<ImageButton>(R.id.delete_image)

            if(step == steps[0]){
                val tv = projectView.findViewById<TextView>(R.id.andthen)
                tv.text = ""

                IB_del.alpha = 0F
            }else{
                IB_del.setOnClickListener {
                    val dial = AlertDialog.Builder(context)
                    dial.setTitle(R.string.confirm)
                            .setMessage(R.string.delete_step)
                            .setPositiveButton(R.string.yes) { dialog, _ ->
                                if (add){
                                    rule!!.steps.remove(step)
                                }else{
                                    (context as MainActivity).deleteStepOfRule(rule!!, step)
                                }
                                adapteur = StepsAdapter(context, steps)
                                LV_steps.adapter = adapteur
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                    try{
                        dial.create()
                    }catch (e: Exception){} finally { dial.show() }
                }
            }

            return projectView
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_rule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context.actualProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        rule = context.actualRule
        add = rule == null
        rule = if (add) Rule(0, context.actualProject!!.myRules.size) else rule!!.clone()

        expl = AlertDialog.Builder(context)
        expl.setTitle(R.string.info)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }

        CB_startNow = context.findViewById(R.id.begin_now)
        ET_otherStart = context.findViewById(R.id.begin_other)
        if (!add) {
            ET_otherStart.setText(rule!!.start.toString())
        }

        S_counters = context.findViewById(R.id.the_counter)
        val arr = ArrayList<String>(context.actualProject!!.getCounters().size+1)
        val the_proj = context.getString(R.string.the_project)
        arr.add(the_proj)
        context.actualProject!!.getCounters().forEach {arr.add(it.name)}
        selectedItem = the_proj
        if (!add) {
            selectedItem = rule!!.counter
        }
        val adaptor = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        S_counters.adapter = adaptor
        S_counters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0)
                    rule!!.counter = arr[p2]
                else
                    rule!!.counter = ""
            }
        }
        if (!add) {
            val temp_selected = if(rule!!.counter == "") the_proj else rule!!.counter
            S_counters.setSelection(adaptor.getPosition(temp_selected))
        }

        ET_comment = context.findViewById(R.id.the_comment)
        if(!add){
            ET_comment.setText(rule!!.comment)
        }

        IB_infoStart = context.findViewById(R.id.info_start)
        IB_infoStart.setOnClickListener {
            expl.setMessage(R.string.help_start).create().show()
        }

        IB_infoStep = context.findViewById(R.id.info_step)
        IB_infoStep.setOnClickListener {
            expl.setMessage(R.string.help_step).create().show()
        }

        IB_infoCounter = context.findViewById(R.id.info_which_counter)
        IB_infoCounter.setOnClickListener {
            expl.setMessage(R.string.help_which_counter).create().show()
        }

        IB_infoComment = context.findViewById(R.id.info_comment)
        IB_infoComment.setOnClickListener {
            expl.setMessage(R.string.help_comment_rule).create().show()
        }

        steps = if (add) {
                    ArrayList()
                } else {
                    rule!!.steps
                }
        if(add){
            steps.add(Step(true, 1, 1, 1))
        }
        rule!!.steps = steps

        LV_steps = context.findViewById(R.id.listSteps)
        adapteur = this.StepsAdapter(context, steps)
        LV_steps.adapter = adapteur

        B_cancel = context.findViewById(R.id.button_cancel)
        B_cancel.setOnClickListener {
            context.onBackPressed()
        }

        B_add_step = context.findViewById(R.id.button_add_step)
        B_add_step.setOnClickListener {
            steps.add(Step(true, 1, 1, 1))
            adapteur.notifyDataSetChanged()
        }

        B_save = context.findViewById(R.id.button_save)
        B_save.setOnClickListener {
            val start = if (CB_startNow.isChecked) context.actualProject!!.etat else try {
                ET_otherStart.text.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }
            rule!!.start = start
            rule!!.comment = ET_comment.text.toString()
            val prem = if(add) context.getString(R.string.pre_rule) else context.getString(R.string.pre_rule_modif)
            val mess = context.createTextFromRule(rule!!)
            val dial = AlertDialog.Builder(context)
            dial.setTitle(R.string.confirm)
                    .setPositiveButton(R.string.save) { dialog, _ ->
                        if (add){
                            context.addRuleToProject(rule!!)
                        }else{
                            context.updateRule(context.actualRule!!, rule!!)
                        }
                        context.onBackPressed()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setMessage(prem + "\n" + mess)
                    .setCancelable(false)
                    .create()
                    .show()
        }
        context.title = context.getString(R.string.add_rule)
    }
}