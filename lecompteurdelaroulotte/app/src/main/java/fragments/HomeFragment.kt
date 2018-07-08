package fragments

import android.os.Bundle
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import library.MyDatabase

class HomeFragment: Fragment() {
    private val TAG = "===== MAINFRAGMENT ====="
    private var context: AppCompatActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as AppCompatActivity
        val mainView = inflater!!.inflate(R.layout.fragment_acceuil, container, false)

        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Do smthg
    }
}