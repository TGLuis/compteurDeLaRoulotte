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
    override var TAG: String = "= PARAMETERSFRAGMENT ="

    private lateinit var checkBox_screenOn: CheckBox
    private lateinit var checkBox_volumeOn: CheckBox
    private lateinit var spinner_language: Spinner
    private lateinit var button_save: Button
    private lateinit var button_cancel: Button
    private lateinit var selectedLanguage: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_parameters, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setAllView()

        checkBox_volumeOn.isChecked = context.volumeOn
        checkBox_screenOn.isChecked = context.screenOn
        selectedLanguage = context.language

        val array = ArrayList(Helper.languagesAvailable())
        val adapteur = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, array.toArray())
        adapteur.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_language.adapter = adapteur
        spinner_language.setSelection(array.indexOf(context.language))
        spinner_language.onItemSelectedListener = spinnerListener(array)

        button_cancel.setOnClickListener { context.onBackPressed() }
        button_save.setOnClickListener { save() }
        context.title = context.getString(R.string.parameters)
    }

    private fun setAllView() {
        checkBox_screenOn = context.findViewById(R.id.check_box_screen_on)
        checkBox_volumeOn = context.findViewById(R.id.check_box_volume_buttons)
        button_save = context.findViewById(R.id.button_save)
        button_cancel = context.findViewById(R.id.button_cancel)
        spinner_language = context.findViewById(R.id.spinner_language)
    }

    private fun save() {
        var restart = false
        val languageDifferent = context.language != selectedLanguage
        if (context.screenOn != checkBox_screenOn.isChecked || context.volumeOn != checkBox_volumeOn.isChecked)
            restart = true
        context.volumeOn = checkBox_volumeOn.isChecked
        context.screenOn = checkBox_screenOn.isChecked
        context.language = selectedLanguage
        Helper.saveProperties()

        if (languageDifferent) context.setNewLocale(context.language)
        else if (restart) context.recreate()
    }

    private fun spinnerListener(array: ArrayList<String>): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLanguage = array[p2]
            }
        }
    }
}