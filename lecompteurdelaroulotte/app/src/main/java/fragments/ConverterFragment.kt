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
    private lateinit var imageButton_reverse: ImageButton

    // correspondence with R.array.string_array_units
    private val lengthUnits = ArrayList<String>(listOf("m", "dm", "cm", "mm", "inch", "feet"))

    // EditText
    private var computeText = true // To avoid infinite loop
    private var lastModified = 0

    // Spinners
    private var selectedUnits = arrayOf(0, 0)

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
        imageButton_reverse = context.findViewById(R.id.converter_arrow)

        imageButton_reverse.setOnClickListener { inverseSpinners() }

        // Set up the spinners
        spinner_value1.onItemSelectedListener = listener(0)
        spinner_value2.onItemSelectedListener = listener(1)

        // Set up the editable texts
        editText_value1.addTextChangedListener(textWatcher(editText_value2, 0))
        editText_value2.addTextChangedListener(textWatcher(editText_value1, 1))

        computeText = true
        editText_value1.setText("1")

        context.setMenu("home")
        context.title = context.getString(R.string.ConverterTitle)
    }

    private fun inverseSpinners() {
        val t = spinner_value1.selectedItemPosition
        spinner_value1.setSelection(spinner_value2.selectedItemPosition)
        spinner_value2.setSelection(t)
    }

    private fun listener(myUnit: Int): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedUnits[myUnit] = p2
                process()
            }
        }
    }

    private fun textWatcher(otherEditText: EditText, myUnit: Int): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                lastModified = myUnit
                processChange(p0.toString(), otherEditText, myUnit)
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }
    }

    private fun process() {
        if (lastModified == 1) {
            processChange(editText_value1.text.toString(), editText_value2, 0)
        } else {
            processChange(editText_value2.text.toString(), editText_value1, 1)
        }
    }

    private fun processChange(str: String, otherEditText: EditText, myUnit: Int) {
        if (computeText) {
            val value = if (str != "") { str.toFloat() } else { 0f }
            computeText = false
            val newVal = computeLenConverter(value, selectedUnits[myUnit], selectedUnits[1-myUnit])
            otherEditText.setText(newVal.toString())
        } else {
            computeText = true
        }
    }


    private fun computeToMeter(value: Float, unitFrom: Int): Float {
        return when (lengthUnits[unitFrom]) {
            "m" -> value
            "dm" -> value/10
            "cm" -> value/100
            "mm" -> value/1000
            "inch" -> value*0.0254f
            "feet" -> value*0.30479999f
            else -> value
        }
    }

    private fun computeFromMeter(value: Float, unitTo: Int): Float {
        return when (lengthUnits[unitTo]) {
            "m" -> value
            "dm" -> value*10
            "cm" -> value*100
            "mm" -> value*1000
            "inch" -> value*39.37007874f
            "feet" -> value*3.28084f
            else -> value
        }
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
