package lufra.lecompteurdelaroulotte

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import fragments.HomeFragment
import library.Counter
import library.MyDatabase
import library.Project


class MainActivity: AppCompatActivity(){
    private val TAG = "===== MAINACTIVITY ====="
    lateinit var projectsList: ArrayList<Project>
    var actualProject: Project? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = MyDatabase(this)

        //Todo: regarder si la bdd a des données et si oui les enregistrer dans projectsList
        projectsList = ArrayList<Project>()

        openFragment(HomeFragment() as Fragment)
    }

    fun createProject(projectName: String){
        projectsList.add(Project(projectName))
    }


    fun createCounter(counterName: String){
        actualProject!!.addCounter(Counter(counterName, -1, false, null))
    }

    fun openFragment(frag: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame, frag).commit()
    }

    //Todo: backward button to handle
}