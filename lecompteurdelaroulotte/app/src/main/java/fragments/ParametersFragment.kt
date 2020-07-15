package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import library.Helper
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R


class ParametersFragment : MyFragment() {
    private lateinit var context: MainActivity

    private lateinit var CB_screen_on: CheckBox
    private lateinit var CB_volume_on: CheckBox
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

        B_cancel.setOnClickListener {
            context.onBackPressed()
        }
        B_save.setOnClickListener {
            var restart = false
            if (context.screenOn != CB_screen_on.isChecked ||
                    CB_volume_on.isChecked != context.volumeOn)
                restart = true
            context.volumeOn = CB_volume_on.isChecked
            context.screenOn = CB_screen_on.isChecked
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