package fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

class ConverterFragment : MyFragment() {
    private lateinit var context: MainActivity

    private lateinit var convLengthVal1EditText: EditText
    private lateinit var convLengthVal2EditText: EditText
    private lateinit var convLengthVal1Spinner: Spinner
    private lateinit var convLengthVal2Spinner: Spinner

    // EditText
    private var convLengthVal1 = 1f
    private var convLengthVal2 = 1f
    private var computeText = true // Pour éviter une boucle infinie
    private var firstEnter1 = true
    private var firstEnter2 = true

    // Spinners
    private val lengthMagnitudes = ArrayList<String>(listOf("m", "dm", "cm", "mm", "inch", "feet"))
    private var selectedLengthMagnitude1 = 0
    private var selectedLengthMagnitude2 = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_converter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        convLengthVal1EditText = context.findViewById(R.id.converter_length_value_1)
        convLengthVal2EditText = context.findViewById(R.id.converter_length_value_2)
        convLengthVal1Spinner = context.findViewById(R.id.converter_spinner_length_1)
        convLengthVal2Spinner = context.findViewById(R.id.converter_spinner_length_2)

        // We set up the spinners
        convLengthVal1Spinner.adapter = prepareSpinner("length")
        convLengthVal1Spinner.setSelection(lengthMagnitudes.indexOf("cm"))
        convLengthVal1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLengthMagnitude1 = p2
//                computeText = true
                if (firstEnter1)
                    firstEnter1 = false
                else
                    computeLenConverter(false)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        convLengthVal2Spinner.adapter = prepareSpinner("length")
        convLengthVal2Spinner.setSelection(lengthMagnitudes.indexOf("cm"))
        convLengthVal2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLengthMagnitude2 = p2
                if (firstEnter2)
                    firstEnter2 = false
                else
                    computeLenConverter(true)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // We set up the editable texts
        val convLengthVal1EditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (computeText) {
                    val str = p0.toString()
                    convLengthVal1 = if (str != "") {
                        str.toFloat()
                    } else {
                        0f
                    }
                    computeLenConverter(true)
                } else {
                    computeText = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }
        val convLengthVal2EditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (computeText) {
                    val str = p0.toString()
                    convLengthVal2 = if (str != "") {
                        str.toFloat()
                    } else {
                        0f
                    }
                    computeLenConverter(false)
                } else {
                    computeText = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }
        convLengthVal1EditText.addTextChangedListener(convLengthVal1EditTextWatcher)
        convLengthVal2EditText.addTextChangedListener(convLengthVal2EditTextWatcher)
        computeText = false
        convLengthVal1EditText.setText("1")
        computeLenConverter(true)
        computeText = true
        context.title = context.getString(R.string.ConverterTitle)
    }

    override fun onPause() {
        super.onPause()

        Log.e(TAG(), selectedLengthMagnitude1.toString())
        Log.e(TAG(), selectedLengthMagnitude2.toString())
    }

    private fun prepareSpinner(type: String) : SpinnerAdapter {
        val adapter : ArrayAdapter<String>
//        if (type == "length") {
            adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, lengthMagnitudes)
//        }
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        return adapter
    }

    /**
     * Compute the equivalent using value1 or value2
     * @param val1IsFirst: if true, first value is val1, so val2 is computed. Else, val1 is computed from val2
     */
    private fun computeLenConverter(val1IsFirst: Boolean): Float {
        return if (val1IsFirst) {
            val res = computeLenConverter(convLengthVal1, selectedLengthMagnitude1, selectedLengthMagnitude2)
            convLengthVal2 = res
            computeText = false
            convLengthVal2EditText.setText(res.toString())
            res
        } else {
            val res = computeLenConverter(convLengthVal2, selectedLengthMagnitude2, selectedLengthMagnitude1)
            convLengthVal1 = res
            computeText = false
            convLengthVal1EditText.setText(res.toString())
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
        if (magn1 > lengthMagnitudes.indexOf("mm") || magn2 > lengthMagnitudes.indexOf("mm")) { // Weird units w/o metrics
            // First with magn1
            when (magn1) {
                lengthMagnitudes.indexOf("inch") -> {
                    when (magn2) {
                        lengthMagnitudes.indexOf("mm") -> { // mm -> inch
                            return val1 * 25.4f
                        }
                        lengthMagnitudes.indexOf("cm") -> { // cm -> inch
                            return val1 * 2.54f
                        }
                        lengthMagnitudes.indexOf("dm") -> { // dm -> inch
                            return val1 * 0.254f
                        }
                        lengthMagnitudes.indexOf("m") -> {  // m -> inch
                            return val1 * 0.0254f
                        }
                        lengthMagnitudes.indexOf("feet") -> {   // feet -> inch
                            return val1 * 12
                        }
                    }
                }
                lengthMagnitudes.indexOf("feet") -> {
                    when (magn2) {
                        lengthMagnitudes.indexOf("mm") -> { // mm -> feet
                            return val1 * 304.79999f
                        }
                        lengthMagnitudes.indexOf("cm") -> { // cm -> feet
                            return val1 * 30.479999f
                        }
                        lengthMagnitudes.indexOf("dm") -> { // dm -> feet
                            return val1 * 3.0479999f
                        }
                        lengthMagnitudes.indexOf("m") -> {  // m -> feet
                            return val1 * 0.30479999f
                        }
                        lengthMagnitudes.indexOf("inch") -> {   // inch -> feet
                            return val1 * 0.083333f
                        }
                    }
                }
            }
            // Second with magn2
            when (magn2) {
                lengthMagnitudes.indexOf("inch") -> {
                    when (magn1) {
                        lengthMagnitudes.indexOf("mm") -> { // inch -> mm
                            return val1 * 0.03937007874f
                        }
                        lengthMagnitudes.indexOf("cm") -> { // inch -> cm
                            return val1 * 0.3937007874f
                        }
                        lengthMagnitudes.indexOf("dm") -> { // inch -> dm
                            return val1 * 3.937007874f
                        }
                        lengthMagnitudes.indexOf("m") -> {  // inch -> m
                            return val1 * 39.37007874f
                        }
                        lengthMagnitudes.indexOf("feet") -> {   // inch -> feet
                            return val1 * 0.083333f
                        }
                    }
                }
                lengthMagnitudes.indexOf("feet") -> {
                    when (magn1) {
                        lengthMagnitudes.indexOf("mm") -> { // feet -> mm
                            return val1 * 0.00328084f
                        }
                        lengthMagnitudes.indexOf("cm") -> { // feet -> cm
                            return val1 * 0.0328084f
                        }
                        lengthMagnitudes.indexOf("dm") -> { // feet -> dm
                            return val1 * 0.328084f
                        }
                        lengthMagnitudes.indexOf("m") -> {  // feet -> m
                            return val1 * 3.28084f
                        }
                        lengthMagnitudes.indexOf("inch") -> {   // feet -> inch
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

    override fun TAG(): String {
        return "===== CONVERTER FRAGMENT ====="
    }
}



