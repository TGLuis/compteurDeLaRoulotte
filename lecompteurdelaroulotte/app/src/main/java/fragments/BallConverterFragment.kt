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
import java.text.DecimalFormat

class BallConverterFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== CONVERTER FRAGMENT ====="

    private lateinit var editText_ballsRequired: EditText
    private lateinit var editText_sizeBallsRequired: EditText
    private lateinit var editText_sizeBallsAvailable: EditText
    private lateinit var spinner_unitBallsRequired: Spinner
    private lateinit var spinner_unitBallsAvailable: Spinner
    private lateinit var textView_result: TextView

    // correspondence with R.array.string_array_units
    private val units = ArrayList<String>(listOf("meter", "yard"))

    // Spinners
    private var selectedUnits = arrayOf(0, 0)

    private val decFormat = DecimalFormat("#.#")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_ball_converter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        editText_ballsRequired = context.findViewById(R.id.balls_required)
        editText_sizeBallsRequired = context.findViewById(R.id.size_balls_required)
        editText_sizeBallsAvailable = context.findViewById(R.id.size_balls_available)
        spinner_unitBallsRequired = context.findViewById(R.id.unit_balls_required)
        spinner_unitBallsAvailable = context.findViewById(R.id.unit_balls_available)
        textView_result = context.findViewById(R.id.balls_i_need)

        // Set up the spinners
        spinner_unitBallsRequired.onItemSelectedListener = listener(0)
        spinner_unitBallsAvailable.onItemSelectedListener = listener(1)

        // Set up the editable texts
        editText_sizeBallsAvailable.addTextChangedListener(textWatcher())
        editText_sizeBallsRequired.addTextChangedListener(textWatcher())
        editText_ballsRequired.addTextChangedListener(textWatcher())

        context.setMenu("home")
        context.title = context.getString(R.string.balls_converter)
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

    private fun textWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                process()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }
    }

    private fun process() {
        var ballsRequired = 0f
        var sizeRequired = 0f
        var sizeAvailable = 0f
        var result = 0f
        try {
            ballsRequired = (editText_ballsRequired.text.toString()).toFloat()
            sizeRequired = computeToMeter(editText_sizeBallsRequired.text.toString().toFloat(), selectedUnits[0])
            sizeAvailable = computeToMeter(editText_sizeBallsAvailable.text.toString().toFloat(), selectedUnits[1])
        } catch (e: NumberFormatException) {}
        finally {
            if (sizeAvailable != 0.0f) {
                result = ballsRequired * sizeRequired / sizeAvailable
            }
        }
        textView_result.text = decFormat.format(result)
    }


    private fun computeToMeter(value: Float, unitFrom: Int): Float {
        return when (units[unitFrom]) {
            "meter" -> value
            "yard" -> 0.9144f*value
            else -> value
        }
    }
}
