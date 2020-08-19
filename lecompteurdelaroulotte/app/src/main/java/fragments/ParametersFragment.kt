package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import library.Helper
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import java.util.ArrayList


class ParametersFragment : MyFragment() {
    private lateinit var context: MainActivity

    private lateinit var CB_screen_on: CheckBox
    private lateinit var CB_volume_on: CheckBox
    private lateinit var S_language: Spinner
    private lateinit var B_save: Button
    private lateinit var B_cancel: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_parameters, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        CB_screen_on = context.findViewById(R.id.check_box_screen_on)
        CB_volume_on = context.findViewById(R.id.check_box_volume_buttons)
        B_save = context.findViewById(R.id.button_save)
        B_cancel = context.findViewById(R.id.button_cancel)

        CB_volume_on.isChecked = context.volumeOn
        CB_screen_on.isChecked = context.screenOn

        S_language = context.findViewById(R.id.spinner_language)
        val arr = ArrayList<String>(Helper.languagesAvailable())
        val adapteur = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
        var selectedLanguage = arr[0]
        adapteur.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        S_language.adapter = adapteur
        S_language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLanguage = arr[p2]
            }
        }

        B_cancel.setOnClickListener {
            context.onBackPressed()
        }
        B_save.setOnClickListener {
            var restart = false
            if (context.screenOn != CB_screen_on.isChecked ||
                    context.volumeOn != CB_volume_on.isChecked ||
                    context.language != selectedLanguage)
                restart = true
            context.volumeOn = CB_volume_on.isChecked
            context.screenOn = CB_screen_on.isChecked
            context.language = selectedLanguage
            Helper.saveProperties()
            if (restart) {
                context.recreate()
            } else {
                context.onBackPressed()
            }
        }
        context.title = context.getString(R.string.parameters)
    }

    override fun TAG(): String {
        return "= PARAMETERSFRAGMENT ="
    }
}