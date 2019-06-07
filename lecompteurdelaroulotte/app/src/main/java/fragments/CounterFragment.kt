package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Counter
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.lang.Exception

class CounterFragment: Fragment() {
    private val TAG = "===== COUNTERFRAGMENT ====="
    private lateinit var context: MainActivity
    private lateinit var counter: Counter

    private lateinit var ET_name: EditText
    private lateinit var ET_state: EditText
    private lateinit var ET_max: EditText
    private lateinit var TV_tours: TextView
    private lateinit var ET_position: EditText
    private lateinit var CB_attach: CheckBox
    private lateinit var S_attached: Spinner
    private lateinit var B_cancel: Button
    private lateinit var B_save: Button
    private lateinit var B_delete: Button
    private lateinit var IB_max: ImageButton
    private lateinit var IB_tours: ImageButton
    private lateinit var IB_position: ImageButton
    private lateinit var IB_attach: ImageButton
    private lateinit var IB_attached: ImageButton
    private lateinit var expl: AlertDialog.Builder

    private lateinit var selectedItem: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_counter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        counter = context.actualCounter!!

        ET_name = context.findViewById(R.id.counter_name)
        ET_name.setText(counter.name)

        ET_max = context.findViewById(R.id.counter_max)
        val max = counter.max
        ET_max.setText(max.toString())

        ET_state = context.findViewById(R.id.counter_state)
        ET_state.setText(counter.etat.toString())

        TV_tours = context.findViewById(R.id.counter_tours)
        val tours= if(max==0) 0 else counter.etat/max
        TV_tours.text = tours.toString()

        ET_position = context.findViewById(R.id.counter_position)
        ET_position.setText((counter.order+1).toString())

        CB_attach = context.findViewById(R.id.counter_attach)
        CB_attach.isChecked = counter.attachedMain

        S_attached = context.findViewById(R.id.counter_attached)
        selectedItem = context.getString(R.string.none)
        val arr = ArrayList<String>(context.actualProject!!.getCounters().size)
        arr.add(context.getString(R.string.none))
        context.actualProject!!.getCounters().forEach {
            if(it != counter)
                arr.add(it.name)
        }
        val adapteur = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
        adapteur.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        S_attached.adapter = adapteur
        S_attached.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedItem = arr[p2]
            }
        }
        if(counter.counterAttached != null){
            S_attached.setSelection(adapteur.getPosition(counter.counterAttached!!.name))
        }

        B_save = context.findViewById(R.id.save)
        B_save.setOnClickListener {
            val temp_name = ET_name.text.toString()
            val temp_etat = ET_state.text.toString().toInt()
            val temp_max = ET_max.text.toString().toInt()
            var temp_pos = ET_position.text.toString().toInt()-1
            val temp_attach = CB_attach.isChecked
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
            if(order_changed){
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
            if (name_changed && context.actualProject!!.getCounter(temp_name) != null) {
                ET_name.error = context.getString(R.string.counter_already)
            } else if (temp_max<0){
                ET_max.error = context.getString(R.string.max_condition)
            }else{
                if(changes > 0) {
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
                                    if (counter.attachedMain){
                                        context.actualProject!!.attach(counter)
                                    }else {
                                        context.actualProject!!.detach(counter)
                                    }
                                }
                                if (order_changed) {
                                    if (temp_pos < counter.order){
                                        if (temp_pos < 0) temp_pos = 0
                                        for(i in temp_pos until counter.order){
                                            context.actualProject!!.myCounters[i].order++
                                        }
                                    }else{
                                        if (temp_pos >= context.actualProject!!.myCounters.size) {
                                            temp_pos = context.actualProject!!.myCounters.size-1
                                        }
                                        for(i in counter.order+1..temp_pos){
                                            context.actualProject!!.myCounters[i].order--
                                        }
                                    }
                                    counter.order = temp_pos
                                }
                                if (attached_changed) {
                                    if (temp_attached == context.getString(R.string.none)) {
                                        counter.detach()
                                    } else {
                                        counter.attach(context.actualProject!!.getCounter(temp_attached)!!)
                                    }
                                }
                                context.openFragment(context.frags.pop())
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                context.openFragment(context.frags.pop())
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                }
            }
        }

        B_cancel = context.findViewById(R.id.cancel)
        B_cancel.setOnClickListener {
            context.openFragment(context.frags.pop())
        }

        B_delete = context.findViewById(R.id.delete)
        B_delete.setOnClickListener {
            val confirm = AlertDialog.Builder(context)
            confirm.setTitle(R.string.confirm)
                    .setMessage(R.string.delete_counter)
                    .setPositiveButton(R.string.yes){ dialog, _ ->
                        context.deleteCounter(context.actualCounter!!)
                        context.openFragment(context.frags.pop())
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel){ dialog, _ ->
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

        IB_max = context.findViewById(R.id.info_max)
        IB_max.setOnClickListener {
            expl.setMessage(R.string.help_max).create().show()
        }

        IB_tours = context.findViewById(R.id.info_tours)
        IB_tours.setOnClickListener {
            expl.setMessage(R.string.help_tours).create().show()
        }

        IB_position = context.findViewById(R.id.info_position)
        IB_position.setOnClickListener {
            expl.setMessage(R.string.help_position).create().show()
        }

        IB_attach = context.findViewById(R.id.info_attach)
        IB_attach.setOnClickListener {
            expl.setMessage(R.string.help_attach).create().show()
        }

        IB_attached = context.findViewById(R.id.info_attached)
        IB_attached.setOnClickListener {
            expl.setMessage(R.string.help_attached).create().show()
        }

        context.actualFragment = CounterFragment()
        context.title = "${context.actualProject.toString()} -> ${counter.name}"
    }
}