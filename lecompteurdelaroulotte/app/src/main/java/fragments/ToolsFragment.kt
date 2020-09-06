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

    private lateinit var button_converter: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_tools, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_converter = context.findViewById(R.id.tools_converter_btn)
        button_converter.setOnClickListener {
            context.openFragment(ConverterFragment())
        }

        context.title = context.getString(R.string.ToolsTitle)
    }
}