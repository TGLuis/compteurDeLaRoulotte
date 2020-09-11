package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

abstract class SeeFragment: MyFragment() {
    protected lateinit var context: MainActivity
    override var TAG: String = "===== SEEFRAGMENT ====="

    protected lateinit var listView: ListView
    protected lateinit var button_add: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_see, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context.currentProject == null) {
            context.openFragment(HomeFragment())
            return
        }

        button_add = context.findViewById(R.id.button_add)
        listView = context.findViewById(R.id.list)
    }
}