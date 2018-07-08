package lufra.lecompteurdelaroulotte

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import fragments.HomeFragment
import library.MyDatabase


class MainActivity: AppCompatActivity(){
    private val TAG = "===== MAINACTIVITY ====="

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = MyDatabase(this)

        openFragment(HomeFragment() as Fragment)
    }

    fun openFragment(frag: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame, frag).commit()
    }
}