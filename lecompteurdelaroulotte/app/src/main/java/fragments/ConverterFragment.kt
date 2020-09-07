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

        // We set up the spinners
        spinner_value1.adapter = prepareSpinner("length")
        spinner_value1.setSelection(lengthUnits.indexOf("cm"))
        spinner_value1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedUnit1 = p2
                if (firstEnter1) firstEnter1 = false
                else computeLenConverter(false)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinner_value2.adapter = prepareSpinner("length")
        spinner_value2.setSelection(lengthUnits.indexOf("cm"))
        spinner_value2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedUnit2 = p2
                if (firstEnter2) firstEnter2 = false
                else computeLenConverter(true)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // We set up the editable texts
        val convLengthVal1EditTextWatcher = object : TextWatcher {
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
        val convLengthVal2EditTextWatcher = object : TextWatcher {
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
        editText_value1.addTextChangedListener(convLengthVal1EditTextWatcher)
        editText_value2.addTextChangedListener(convLengthVal2EditTextWatcher)
        computeText = false
        editText_value1.setText("1")
        computeLenConverter(true)
        computeText = true
        context.title = context.getString(R.string.ConverterTitle)
    }

    private fun prepareSpinner(type: String) : SpinnerAdapter {
        val adapter : ArrayAdapter<String> = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, lengthUnits)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        return adapter
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

    /**
     * @param val1: First value used to compute val2
     * @param magn1: First magnitude
     * @param magn2: Second magnitude
     * @return val2
     */
    private fun computeLenConverter(val1: Float, magn1: Int, magn2: Int) : Float {
        if (magn1 == magn2) return val1
        if (magn1 > lengthUnits.indexOf("mm") || magn2 > lengthUnits.indexOf("mm")) { // Weird units w/o metrics
            // First with magn1
            when (magn1) {
                lengthUnits.indexOf("inch") -> {
                    when (magn2) {
                        lengthUnits.indexOf("mm") -> { // mm -> inch
                            return val1 * 25.4f
                        }
                        lengthUnits.indexOf("cm") -> { // cm -> inch
                            return val1 * 2.54f
                        }
                        lengthUnits.indexOf("dm") -> { // dm -> inch
                            return val1 * 0.254f
                        }
                        lengthUnits.indexOf("m") -> {  // m -> inch
                            return val1 * 0.0254f
                        }
                        lengthUnits.indexOf("feet") -> {   // feet -> inch
                            return val1 * 12
                        }
                    }
                }
                lengthUnits.indexOf("feet") -> {
                    when (magn2) {
                        lengthUnits.indexOf("mm") -> { // mm -> feet
                            return val1 * 304.79999f
                        }
                        lengthUnits.indexOf("cm") -> { // cm -> feet
                            return val1 * 30.479999f
                        }
                        lengthUnits.indexOf("dm") -> { // dm -> feet
                            return val1 * 3.0479999f
                        }
                        lengthUnits.indexOf("m") -> {  // m -> feet
                            return val1 * 0.30479999f
                        }
                        lengthUnits.indexOf("inch") -> {   // inch -> feet
                            return val1 * 0.083333f
                        }
                    }
                }
            }
            // Second with magn2
            when (magn2) {
                lengthUnits.indexOf("inch") -> {
                    when (magn1) {
                        lengthUnits.indexOf("mm") -> { // inch -> mm
                            return val1 * 0.03937007874f
                        }
                        lengthUnits.indexOf("cm") -> { // inch -> cm
                            return val1 * 0.3937007874f
                        }
                        lengthUnits.indexOf("dm") -> { // inch -> dm
                            return val1 * 3.937007874f
                        }
                        lengthUnits.indexOf("m") -> {  // inch -> m
                            return val1 * 39.37007874f
                        }
                        lengthUnits.indexOf("feet") -> {   // inch -> feet
                            return val1 * 0.083333f
                        }
                    }
                }
                lengthUnits.indexOf("feet") -> {
                    when (magn1) {
                        lengthUnits.indexOf("mm") -> { // feet -> mm
                            return val1 * 0.00328084f
                        }
                        lengthUnits.indexOf("cm") -> { // feet -> cm
                            return val1 * 0.0328084f
                        }
                        lengthUnits.indexOf("dm") -> { // feet -> dm
                            return val1 * 0.328084f
                        }
                        lengthUnits.indexOf("m") -> {  // feet -> m
                            return val1 * 3.28084f
                        }
                        lengthUnits.indexOf("inch") -> {   // feet -> inch
                            return val1 * 12
                        }
                    }
                }
            }
        } else {    // Only metrics not weird units, we can use recursion
            return computeMetrics(val1, magn1, magn2)
        }
        return 0f   // because java
    }

    private fun computeMetrics(val1: Float, magn1: Int, magn2: Int) : Float {
        return when {
            magn1 == magn2 -> val1
            magn1 > magn2 -> computeMetrics(val1 * 10, magn1-1, magn2)
            magn1 < magn2 -> computeMetrics(val1 * 0.1f, magn1+1, magn2)
            else -> 0f
        }
    }
}



