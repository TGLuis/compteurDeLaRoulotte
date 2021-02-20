package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

class ToolsFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== TOOLS FRAGMENT ====="

    private lateinit var button_unit_converter: Button
    private lateinit var button_balls_converter: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_tools, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_unit_converter = context.findViewById(R.id.tools_unit_converter_btn)
        button_unit_converter.setOnClickListener {
            context.openFragment(UnitConverterFragment())
        }

        button_balls_converter = context.findViewById(R.id.tools_balls_converter_btn)
        button_balls_converter.setOnClickListener {
            context.openFragment(BallConverterFragment())
        }

        context.title = context.getString(R.string.ToolsTitle)
    }
}