package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Counter
import library.Dialogs
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

class CounterFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== COUNTERFRAGMENT ====="
    private lateinit var counter: Counter

    private lateinit var editText_name: EditText
    private lateinit var editText_state: EditText
    private lateinit var editText_max: EditText
    private lateinit var textView_tours: TextView
    private lateinit var editText_position: EditText
    private lateinit var checkBox_attach: CheckBox
    private lateinit var spinner_attached: Spinner
    private lateinit var button_cancel: Button
    private lateinit var button_save: Button
    private lateinit var button_delete: Button
    private lateinit var imageButton_max: ImageButton
    private lateinit var imageButton_tours: ImageButton
    private lateinit var imageButton_position: ImageButton
    private lateinit var imageButton_attach: ImageButton
    private lateinit var imageButton_attached: ImageButton
    private lateinit var isChanged: HashMap<String, Boolean>
    private lateinit var newValue: HashMap<String, String>

    private lateinit var selectedItem: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_counter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context.currentProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        setAllView()

        counter = context.currentCounter!!

        val max = counter.max
        val tours = if (max == 0) 0 else counter.etat / max

        editText_max.setText(max.toString())
        editText_name.setText(counter.name)
        editText_state.setText(counter.etat.toString())
        editText_position.setText((counter.order + 1).toString())
        textView_tours.text = tours.toString()

        checkBox_attach.isChecked = counter.attachedMain

        selectedItem = context.getString(R.string.none)
        val array = buildArrayOfCounters()
        val adapteur = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, array.toArray())
        adapteur.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_attached.adapter = adapteur
        spinner_attached.onItemSelectedListener = spinnerListener(array)
        if (counter.counterAttached != null) {
            spinner_attached.setSelection(adapteur.getPosition(counter.counterAttached!!.name))
        }

        button_save.setOnClickListener {
            newValue = HashMap()
            newValue["name"] = editText_name.text.toString()
            newValue["attach"] = checkBox_attach.isChecked.toString()
            newValue["attached"] = selectedItem
            if (!checkValidityAndSet(newValue))
                return@setOnClickListener

            isChanged = HashMap()

            val changes = getNumberOfChanges()
            val numberOfChanges = changes.first
            val str = changes.second

            if (isChanged["name"]!! && context.currentProject!!.getCounter(newValue["name"]!!) != null) {
                editText_name.error = context.getString(R.string.counter_already)
            } else if (newValue["max"]!!.toInt() < 0) {
                editText_max.error = context.getString(R.string.max_condition)
            } else {
                if (numberOfChanges > 0) {
                    Dialogs.displayConfirmationDialog(context, str, ::saveCounter)
                } else {
                    Toast.makeText(context, R.string.nothing_changed, Toast.LENGTH_SHORT).show()
                }
            }
        }

        button_delete.setOnClickListener {
            Dialogs.displayConfirmationDialog(context, context.getString(R.string.delete_counter), ::deleteCurrentCounter)
        }
        button_cancel.setOnClickListener { context.onBackPressed() }
        imageButton_max.setOnClickListener { Dialogs.displayInfoDialog(context, R.string.help_max) }
        imageButton_tours.setOnClickListener { Dialogs.displayInfoDialog(context, R.string.help_tours) }
        imageButton_position.setOnClickListener { Dialogs.displayInfoDialog(context, R.string.help_position) }
        imageButton_attach.setOnClickListener { Dialogs.displayInfoDialog(context, R.string.help_attach) }
        imageButton_attached.setOnClickListener { Dialogs.displayInfoDialog(context, R.string.help_attached) }

        context.title = "${context.currentProject.toString()} -> ${counter.name}"
    }

    private fun setAllView() {
        editText_name = context.findViewById(R.id.counter_name)
        editText_max = context.findViewById(R.id.counter_max)
        editText_state = context.findViewById(R.id.counter_state)
        textView_tours = context.findViewById(R.id.counter_tours)
        editText_position = context.findViewById(R.id.counter_position)
        checkBox_attach = context.findViewById(R.id.counter_attach)
        spinner_attached = context.findViewById(R.id.counter_attached)
        button_save = context.findViewById(R.id.save)
        button_cancel = context.findViewById(R.id.cancel)
        button_delete = context.findViewById(R.id.delete)
        imageButton_max = context.findViewById(R.id.info_max)
        imageButton_tours = context.findViewById(R.id.info_tours)
        imageButton_position = context.findViewById(R.id.info_position)
        imageButton_attach = context.findViewById(R.id.info_attach)
        imageButton_attached = context.findViewById(R.id.info_attached)
    }

    private fun spinnerListener(array: ArrayList<String>): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedItem = array[p2]
            }
        }
    }

    private fun buildArrayOfCounters(): ArrayList<String>{
        val array = ArrayList<String>(context.currentProject!!.getCounters().size)
        array.add(context.getString(R.string.none))
        context.currentProject!!.getCounters().forEach {
            if (it != counter)
                array.add(it.name)
        }
        return array
    }

    private fun checkValidityAndSet(values: HashMap<String, String>): Boolean {
        try {
            values["etat"] = editText_state.text.toString()
            values["etat"]!!.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, R.string.state_not_valid, Toast.LENGTH_SHORT).show()
            return false
        }
        try {
            values["max"] = editText_max.text.toString()
            values["max"]!!.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, R.string.max_not_valid, Toast.LENGTH_SHORT).show()
            return false
        }
        try {
            values["position"] = (editText_position.text.toString().toInt() - 1).toString()
            values["position"]!!.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, R.string.position_not_valid, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun getNumberOfChanges(): Pair<Int, String>  {
        var changes = 0
        var str = context.getString(R.string.modif) + "\n"
        isChanged["name"] = newValue["name"] != counter.name
        if (isChanged["name"]!!) {
            changes++
            str += "- " + context.getString(R.string.counter_name) + "\n"
        }
        isChanged["etat"] = newValue["etat"]!!.toInt() != counter.etat
        if (isChanged["etat"]!!) {
            changes++
            str += "- " + context.getString(R.string.counter_state) + "\n"
        }
        isChanged["max"] = newValue["max"]!!.toInt() != counter.max
        if (isChanged["max"]!!) {
            changes++
            str += "- " + context.getString(R.string.counter_max) + "\n"
        }
        isChanged["attach"] = newValue["attach"]!!.toBoolean() != counter.attachedMain
        if (isChanged["attach"]!!) {
            changes++
            str += "- " + context.getString(R.string.counter_attach_main) + "\n"
        }
        isChanged["position"] = newValue["position"]!!.toInt() != counter.order
        if (isChanged["position"]!!) {
            changes++
            str += "- " + context.getString(R.string.counter_position)
        }
        val cond1 = newValue["attached"] != context.getString(R.string.none) && counter.counterAttached == null
        val cond2 = counter.counterAttached != null && newValue["attached"] != counter.counterAttached!!.name
        isChanged["attached"] = cond1 || cond2
        if (isChanged["attached"]!!) {
            changes++
            str += "- " + context.getString(R.string.counter_attached_id) + "\n"
        }
        str += "\n" + context.getString(R.string.sure_to_modif)
        return Pair(changes, str)
    }

    private fun saveCounter() {
        if (isChanged["name"]!!) {
            context.updateCounterName(counter, newValue["name"]!!)
            counter.name = newValue["name"]!!
        }
        if (isChanged["etat"]!!) counter.etat = newValue["etat"]!!.toInt()
        if (isChanged["max"]!!) counter.max = newValue["max"]!!.toInt()
        if (isChanged["attach"]!!) {
            counter.attachedMain = newValue["attach"]!!.toBoolean()
            if (counter.attachedMain) {
                context.currentProject!!.attach(counter)
            } else {
                context.currentProject!!.detach(counter)
            }
        }
        if (isChanged["position"]!!) {
            var position = newValue["position"]!!.toInt()
            if (newValue["position"]!!.toInt() < counter.order) {
                if (position < 0) position = 0
                for (i in position until counter.order) {
                    context.currentProject!!.myCounters[i].order++
                }
            } else {
                if (position >= context.currentProject!!.myCounters.size) {
                    position = context.currentProject!!.myCounters.size - 1
                }
                for (i in counter.order + 1..position) {
                    context.currentProject!!.myCounters[i].order--
                }
            }
            counter.order = position
            context.currentProject!!.myCounters.sortBy { it.order }
        }
        if (isChanged["attached"]!!) {
            if (newValue["attached"] == context.getString(R.string.none)) {
                counter.detach()
            } else {
                counter.attach(context.currentProject!!.getCounter(newValue["attached"]!!)!!)
            }
        }
        context.onBackPressed()
    }

    private fun deleteCurrentCounter() {
        context.deleteCounter(context.currentCounter!!)
        context.onBackPressed()
    }
}