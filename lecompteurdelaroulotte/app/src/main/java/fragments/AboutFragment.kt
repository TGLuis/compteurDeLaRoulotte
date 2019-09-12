package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R

class AboutFragment: MyFragment() {
    private lateinit var context: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context.title = context.getString(R.string.AboutTitle)
    }

    override fun TAG(): String {
        return "===== ABOUTFRAGMENT ====="
    }
}