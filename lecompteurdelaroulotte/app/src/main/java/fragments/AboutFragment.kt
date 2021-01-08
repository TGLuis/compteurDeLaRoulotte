package fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

class AboutFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== ABOUTFRAGMENT ====="
    private lateinit var textView_main: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textView_main = context.findViewById(R.id.aboutText)
        textView_main.movementMethod = LinkMovementMethod.getInstance()

        context.setMenu("home")
        context.title = context.getString(R.string.AboutTitle)
    }
}