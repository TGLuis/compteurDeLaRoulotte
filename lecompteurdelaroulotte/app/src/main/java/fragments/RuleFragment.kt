package fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Dialogs
import library.Rule
import library.Step
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import kotlin.collections.ArrayList

class RuleFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== ADDRULESFRAGMENT ====="
    private var rule: Rule? = null
    private var addingRule: Boolean = true

    private lateinit var listView_steps: ListView
    private lateinit var steps: ArrayList<Step>
    private lateinit var checkBox_startNow: CheckBox
    private lateinit var editText_otherStart: EditText
    private lateinit var spinner_counters: Spinner
    private lateinit var editText_comment: EditText
    private lateinit var imageButton_infoStart: ImageButton
    private lateinit var imageButton_infoCounter: ImageButton
    private lateinit var imageButton_infoComment: ImageButton
    private lateinit var selectedItem: String
    private lateinit var selectedStep: Step

    private lateinit var button_cancel: Button
    private lateinit var button_addStep: Button
    private lateinit var button_save: Button

    private lateinit var adapteur: StepsAdapter

    class CustomWatcher(private var st: Step, private var i: Int) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            when (i) {
                1 -> try {
                    st.one = s.toString().toInt()
                } catch (e: NumberFormatException) {
                }
                2 -> try {
                    st.two = s.toString().toInt()
                } catch (e: NumberFormatException) {
                }
                3 -> try {
                    st.three = s.toString().toInt()
                } catch (e: NumberFormatException) {
                }
            }
        }
    }

    inner class StepsAdapter(context: Context, list: ArrayList<Step>) : ArrayAdapter<Step>(context, 0, list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val step = super.getItem(position)
            val projectView: View = convertView?: LayoutInflater.from(context).inflate(R.layout.list_step_item, parent, false)

            val a = projectView.findViewById<EditText>(R.id.enter_fois)
            a.hint = step!!.one.toString()
            a.addTextChangedListener(CustomWatcher(step, 1))
            val b = projectView.findViewById<EditText>(R.id.enter_rows)
            b.hint = step.two.toString()
            b.addTextChangedListener(CustomWatcher(step, 2))
            val c = projectView.findViewById<EditText>(R.id.enter_stitches)
            c.hint = step.three.toString()
            c.addTextChangedListener(CustomWatcher(step, 3))

            val augment = projectView.findViewById<CheckBox>(R.id.augm_check)
            augment.isChecked = step.augm
            augment.setOnCheckedChangeListener { _ , aug: Boolean ->
                step.augm = aug
            }

            val imageButton_delete = projectView.findViewById<ImageButton>(R.id.delete_image)

            if (step == steps[0]) {
                val tv = projectView.findViewById<TextView>(R.id.andthen)
                tv.text = ""
                imageButton_delete.setImageResource(R.drawable.ic_baseline_info_24)
                imageButton_delete.setOnClickListener{
                    Dialogs.displayInfoDialog(context, R.string.help_step)
                }
            } else {
                imageButton_delete.setOnClickListener {
                    selectedStep = step
                    Dialogs.displayConfirmationDialog(context, context.getString(R.string.delete_step), ::deleteStep)
                }
            }

            return projectView
        }

        private fun deleteStep() {
            if (addingRule) {
                rule!!.steps.remove(selectedStep)
            } else {
                (context as MainActivity).deleteStepOfRule(rule!!, selectedStep)
            }
            adapteur = StepsAdapter(context, steps)
            listView_steps.adapter = adapteur
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_rule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context.currentProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        setAllView()

        rule = context.currentRule
        addingRule = rule == null
        rule = if (addingRule) Rule(context.getNextRuleIdentifiant(), context.currentProject!!.myRules.size) else rule!!.clone()

        val theProject = context.getString(R.string.the_project)
        val arr = buildCounterArray(theProject)
        selectedItem = theProject

        val adaptor = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_counters.adapter = adaptor
        spinner_counters.onItemSelectedListener = spinnerListener(arr)

        if (!addingRule) {
            selectedItem = rule!!.counter
            editText_otherStart.setText(rule!!.start.toString())
            val temp_selected = if (rule!!.counter == "") theProject else rule!!.counter
            spinner_counters.setSelection(adaptor.getPosition(temp_selected))
            editText_comment.setText(rule!!.comment)
        }

        imageButton_infoStart.setOnClickListener {
            Dialogs.displayInfoDialog(context, R.string.help_start)
        }

        imageButton_infoCounter.setOnClickListener {
            Dialogs.displayInfoDialog(context, R.string.help_which_counter)
        }

        imageButton_infoComment.setOnClickListener {
            Dialogs.displayInfoDialog(context, R.string.help_comment_rule)
        }

        steps = if (addingRule) {ArrayList()} else {rule!!.steps}
        if (addingRule) {
            steps.add(Step(true, 1, 1, 1))
        }
        rule!!.steps = steps

        adapteur = this.StepsAdapter(context, steps)
        listView_steps.adapter = adapteur

        button_cancel.setOnClickListener {
            context.onBackPressed()
        }

        button_addStep.setOnClickListener {
            steps.add(Step(true, 1, 1, 1))
            adapteur.notifyDataSetChanged()
        }

        button_save.setOnClickListener {
            save()
        }
        context.title = context.getString(R.string.add_rule)
    }

    private fun setAllView() {
        checkBox_startNow = context.findViewById(R.id.begin_now)
        editText_otherStart = context.findViewById(R.id.begin_other)
        spinner_counters = context.findViewById(R.id.the_counter)
        editText_comment = context.findViewById(R.id.the_comment)
        imageButton_infoStart = context.findViewById(R.id.info_start)
        imageButton_infoCounter = context.findViewById(R.id.info_which_counter)
        imageButton_infoComment = context.findViewById(R.id.info_comment)
        listView_steps = context.findViewById(R.id.listSteps)
        button_cancel = context.findViewById(R.id.button_cancel)
        button_addStep = context.findViewById(R.id.button_add_step)
        button_save = context.findViewById(R.id.button_save)
    }

    private fun save() {
        val start = if (checkBox_startNow.isChecked) context.currentProject!!.etat else try {
            editText_otherStart.text.toString().toInt()
        } catch (e: NumberFormatException) {
            0
        }
        rule!!.start = start
        rule!!.comment = editText_comment.text.toString()
        val prem = if (addingRule) context.getString(R.string.pre_rule) else context.getString(R.string.pre_rule_modif)
        val mess = rule!!.getString(context)
        Dialogs.displayConfirmationDialog(context, prem + "\n" + mess, ::saveAndQuit)
    }

    private fun saveAndQuit() {
        if (addingRule) {
            context.addRuleToProject(rule!!)
        } else {
            context.updateRule(context.currentRule!!, rule!!)
        }
        context.onBackPressed()
    }

    private fun buildCounterArray(theProject: String): ArrayList<String> {
        val array = ArrayList<String>(context.currentProject!!.getCounters().size + 1)
        array.add(theProject)
        context.currentProject!!.getCounters().forEach { array.add(it.name) }
        return array
    }

    private fun spinnerListener(array: ArrayList<String>): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0)
                    rule!!.counter = array[p2]
                else
                    rule!!.counter = ""
            }
        }
    }
}