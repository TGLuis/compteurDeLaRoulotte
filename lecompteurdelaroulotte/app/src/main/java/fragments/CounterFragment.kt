package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import library.Counter
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
    private lateinit var expl: AlertDialog.Builder

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

        counter = context.currentCounter!!

        editText_name = context.findViewById(R.id.counter_name)
        editText_name.setText(counter.name)

        editText_max = context.findViewById(R.id.counter_max)
        val max = counter.max
        editText_max.setText(max.toString())

        editText_state = context.findViewById(R.id.counter_state)
        editText_state.setText(counter.etat.toString())

        textView_tours = context.findViewById(R.id.counter_tours)
        val tours = if (max == 0) 0 else counter.etat / max
        textView_tours.text = tours.toString()

        editText_position = context.findViewById(R.id.counter_position)
        editText_position.setText((counter.order + 1).toString())

        checkBox_attach = context.findViewById(R.id.counter_attach)
        checkBox_attach.isChecked = counter.attachedMain

        spinner_attached = context.findViewById(R.id.counter_attached)
        selectedItem = context.getString(R.string.none)
        val arr = ArrayList<String>(context.currentProject!!.getCounters().size)
        arr.add(context.getString(R.string.none))
        context.currentProject!!.getCounters().forEach {
            if (it != counter)
                arr.add(it.name)
        }
        val adapteur = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
        adapteur.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_attached.adapter = adapteur
        spinner_attached.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedItem = arr[p2]
            }
        }
        if (counter.counterAttached != null) {
            spinner_attached.setSelection(adapteur.getPosition(counter.counterAttached!!.name))
        }

        button_save = context.findViewById(R.id.save)
        button_save.setOnClickListener {
            val temp_name = editText_name.text.toString()
            val temp_etat: Int
            val temp_max: Int
            var temp_pos: Int
            try {
                temp_etat = editText_state.text.toString().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, R.string.state_not_valid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                temp_max = editText_max.text.toString().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, R.string.max_not_valid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                temp_pos = editText_position.text.toString().toInt() - 1
            } catch (e: NumberFormatException) {
                Toast.makeText(context, R.string.position_not_valid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val temp_attach = checkBox_attach.isChecked
            val temp_attached = selectedItem

            var changes = 0
            var str = context.getString(R.string.modif) + "\n"
            val name_changed = temp_name != counter.name
            if (name_changed) {
                changes++
                str += "- " + context.getString(R.string.counter_name) + "\n"
            }
            val etat_changed = temp_etat != counter.etat
            if (etat_changed) {
                changes++
                str += "- " + context.getString(R.string.counter_state) + "\n"
            }
            val max_changed = temp_max != counter.max
            if (max_changed) {
                changes++
                str += "- " + context.getString(R.string.counter_max) + "\n"
            }
            val attach_changed = temp_attach != counter.attachedMain
            if (attach_changed) {
                changes++
                str += "- " + context.getString(R.string.counter_attach_main) + "\n"
            }
            val order_changed = temp_pos != counter.order
            if (order_changed) {
                changes++
                str += "- " + context.getString(R.string.counter_position)
            }
            val cond1 = temp_attached != context.getString(R.string.none) && counter.counterAttached == null
            val cond2 = counter.counterAttached != null && temp_attached != counter.counterAttached!!.name
            val attached_changed = cond1 || cond2
            if (attached_changed) {
                changes++
                str += "- " + context.getString(R.string.counter_attached_id) + "\n"
            }
            str += "\n" + context.getString(R.string.sure_to_modif)
            if (name_changed && context.currentProject!!.getCounter(temp_name) != null) {
                editText_name.error = context.getString(R.string.counter_already)
            } else if (temp_max < 0) {
                editText_max.error = context.getString(R.string.max_condition)
            } else {
                if (changes > 0) {
                    val confirm = AlertDialog.Builder(context)
                    confirm.setTitle(context.getString(R.string.confirm))
                            .setMessage(str)
                            .setPositiveButton(R.string.save) { dialog, _ ->
                                if (name_changed) {
                                    context.updateCounterName(counter, temp_name)
                                    counter.name = temp_name
                                }
                                if (etat_changed) counter.etat = temp_etat
                                if (max_changed) counter.max = temp_max
                                if (attach_changed) {
                                    counter.attachedMain = temp_attach
                                    if (counter.attachedMain) {
                                        context.currentProject!!.attach(counter)
                                    } else {
                                        context.currentProject!!.detach(counter)
                                    }
                                }
                                if (order_changed) {
                                    if (temp_pos < counter.order) {
                                        if (temp_pos < 0) temp_pos = 0
                                        for (i in temp_pos until counter.order) {
                                            context.currentProject!!.myCounters[i].order++
                                        }
                                    } else {
                                        if (temp_pos >= context.currentProject!!.myCounters.size) {
                                            temp_pos = context.currentProject!!.myCounters.size - 1
                                        }
                                        for (i in counter.order + 1..temp_pos) {
                                            context.currentProject!!.myCounters[i].order--
                                        }
                                    }
                                    counter.order = temp_pos
                                }
                                if (attached_changed) {
                                    if (temp_attached == context.getString(R.string.none)) {
                                        counter.detach()
                                    } else {
                                        counter.attach(context.currentProject!!.getCounter(temp_attached)!!)
                                    }
                                }
                                context.onBackPressed()
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                } else {
                    Toast.makeText(context, R.string.nothing_changed, Toast.LENGTH_SHORT).show()
                }
            }
        }

        button_cancel = context.findViewById(R.id.cancel)
        button_cancel.setOnClickListener {
            context.onBackPressed()
        }

        button_delete = context.findViewById(R.id.delete)
        button_delete.setOnClickListener {
            val confirm = AlertDialog.Builder(context)
            confirm.setTitle(R.string.confirm)
                    .setMessage(R.string.delete_counter)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        context.deleteCounter(context.currentCounter!!)
                        context.onBackPressed()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
        }

        expl = AlertDialog.Builder(context)
        expl.setTitle(R.string.info)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)

        imageButton_max = context.findViewById(R.id.info_max)
        imageButton_max.setOnClickListener {
            expl.setMessage(R.string.help_max).create().show()
        }

        imageButton_tours = context.findViewById(R.id.info_tours)
        imageButton_tours.setOnClickListener {
            expl.setMessage(R.string.help_tours).create().show()
        }

        imageButton_position = context.findViewById(R.id.info_position)
        imageButton_position.setOnClickListener {
            expl.setMessage(R.string.help_position).create().show()
        }

        imageButton_attach = context.findViewById(R.id.info_attach)
        imageButton_attach.setOnClickListener {
            expl.setMessage(R.string.help_attach).create().show()
        }

        imageButton_attached = context.findViewById(R.id.info_attached)
        imageButton_attached.setOnClickListener {
            expl.setMessage(R.string.help_attached).create().show()
        }

        context.title = "${context.currentProject.toString()} -> ${counter.name}"
    }
}