package fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

class ConverterFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== CONVERTER FRAGMENT ====="

    private lateinit var editText_value1: EditText
    private lateinit var editText_value2: EditText
    private lateinit var spinner_value1: Spinner
    private lateinit var spinner_value2: Spinner

    // EditText
    private var value1 = 1f
    private var value2 = 1f
    private var computeText = true // To avoid infinite loop
    private var firstEnter1 = true
    private var firstEnter2 = true

    // Spinners
    private val lengthUnits = ArrayList<String>(listOf("m", "dm", "cm", "mm", "inch", "feet"))
    private var selectedUnit1 = 0
    private var selectedUnit2 = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_converter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        editText_value1 = context.findViewById(R.id.converter_length_value_1)
        editText_value2 = context.findViewById(R.id.converter_length_value_2)
        spinner_value1 = context.findViewById(R.id.converter_spinner_length_1)
        spinner_value2 = context.findViewById(R.id.converter_spinner_length_2)

        // Set up the spinners
        spinner_value1.adapter = prepareSpinner()
        spinner_value1.setSelection(lengthUnits.indexOf("cm"))
        spinner_value1.onItemSelectedListener = listenerOne()

        spinner_value2.adapter = prepareSpinner()
        spinner_value2.setSelection(lengthUnits.indexOf("cm"))
        spinner_value2.onItemSelectedListener = listenerTwo()

        // Set up the editable texts
        editText_value1.addTextChangedListener(textWatcherOne())
        editText_value2.addTextChangedListener(textWatcherTwo())

        computeText = false
        editText_value1.setText("1")
        computeLenConverter(true)
        computeText = true

        context.title = context.getString(R.string.ConverterTitle)
    }

    private fun prepareSpinner() : SpinnerAdapter {
        val adapter : ArrayAdapter<String> = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, lengthUnits)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        return adapter
    }

    private fun listenerOne(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedUnit1 = p2
                if (firstEnter1) firstEnter1 = false
                else computeLenConverter(false)
            }
        }
    }

    private fun listenerTwo(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedUnit2 = p2
                if (firstEnter2) firstEnter2 = false
                else computeLenConverter(true)
            }
        }
    }

    private fun textWatcherOne(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (computeText) {
                    val str = p0.toString()
                    value1 = if (str != "") {
                        str.toFloat()
                    } else {
                        0f
                    }
                    computeLenConverter(true)
                } else {
                    computeText = true
                }
            }
        }
    }

    private fun textWatcherTwo(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (computeText) {
                    val str = p0.toString()
                    value2 = if (str != "") {
                        str.toFloat()
                    } else {
                        0f
                    }
                    computeLenConverter(false)
                } else {
                    computeText = true
                }
            }
        }
    }

    /**
     * Compute the equivalent using value1 or value2
     * @param val1IsFirst: if true, first value is val1, so val2 is computed. Else, val1 is computed from val2
     */
    private fun computeLenConverter(val1IsFirst: Boolean): Float {
        return if (val1IsFirst) {
            val res = computeLenConverter(value1, selectedUnit1, selectedUnit2)
            value2 = res
            computeText = false
            editText_value2.setText(res.toString())
            res
        } else {
            val res = computeLenConverter(value2, selectedUnit2, selectedUnit1)
            value1 = res
            computeText = false
            editText_value1.setText(res.toString())
            res
        }
    }

    private fun computeToMeter(value: Float, unitFrom: Int): Float {
        when (lengthUnits[unitFrom]) {
            "m" -> {
                return value
            }
            "dm" -> {
                return value/10
            }
            "cm" -> {
                return value/100
            }
            "mm" -> {
                return value/1000
            }
            "inch" -> {
                return value*39.37007874f
            }
            "feet" -> {
                return value*3.28084f
            }
        }
        return value
    }

    private fun computeFromMeter(value: Float, unitTo: Int): Float {
        when (lengthUnits[unitTo]) {
            "m" -> {
                return value
            }
            "dm" -> {
                return value*10
            }
            "cm" -> {
                return value*100
            }
            "mm" -> {
                return value*1000
            }
            "inch" -> {
                return value*0.0254f
            }
            "feet" -> {
                return value*0.30479999f
            }
        }
        return value
    }

    /**
     * @param val1: First value used to compute val2
     * @param magn1: First magnitude as index of lengthUnits
     * @param magn2: Second magnitude as index of lengthUnits
     * @return
     */
    private fun computeLenConverter(val1: Float, magn1: Int, magn2: Int) : Float {
        if (magn1 == magn2) return val1
        return computeFromMeter(computeToMeter(val1, magn1), magn2)
    }

}



