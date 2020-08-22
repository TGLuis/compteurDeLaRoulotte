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

    private lateinit var converterBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_tools, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        converterBtn = context.findViewById(R.id.tools_converter_btn)
        converterBtn.setOnClickListener {
            context.openFragment(ConverterFragment())
        }

        context.title = context.getString(R.string.ToolsTitle)
    }

    override fun TAG(): String {
        return "===== TOOLS FRAGMENT ====="
    }
}