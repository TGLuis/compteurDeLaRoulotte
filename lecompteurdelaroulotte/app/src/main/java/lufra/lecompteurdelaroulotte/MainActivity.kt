package lufra.lecompteurdelaroulotte

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import fragments.HomeFragment
import library.Counter
import library.MyDatabase
import library.Project
import java.util.*


class MainActivity: AppCompatActivity(){
    private val TAG = "===== MAINACTIVITY ====="
    lateinit var projectsList: ArrayList<Project>
    var actualProject: Project? = null
    var actualCounter: Counter? = null
    lateinit var frags: Stack<Fragment>
    lateinit var db: MyDatabase

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = MyDatabase(this)

        projectsList = db.getAllProjects()

        frags = Stack()
        frags.push(HomeFragment() as Fragment)
        openFragment(HomeFragment() as Fragment)
    }

    fun createProject(projectName: String){
        db.addProjectDB(projectName, " ")
        projectsList.add(Project(projectName))
    }

    fun deleteProject(proj: Project){
        db.deleteProjectDB(proj.toString())
        projectsList.remove(proj)
    }


    fun createCounter(counterName: String){
        db.addCounterDB(actualProject.toString(), counterName, 0, 0, 0, false, null)
        actualProject!!.addCounter(Counter(counterName, 0, false, null))
    }

    fun openFragment(frag: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame, frag).commit()
    }

    override fun onBackPressed() {
        if(!frags.isEmpty())
            openFragment(frags.pop())
    }

    override fun onDestroy() {
        saveState()
        super.onDestroy()
    }

    override fun onPause() {
        saveState()
        super.onPause()
    }

    override fun onStop() {
        saveState()
        super.onStop()
    }

    private fun saveState(){
        if(!projectsList.isEmpty()){
            projectsList.forEach{
                val proj = it
                db.updateProjectDB(proj.toString(), proj.etat, proj.notes)
                val coun = proj.getCounters()
                if(! coun.isEmpty()){
                    coun.forEach{
                        db.updateCounterDB(proj.toString(), it.name, it.etat, it.tours, it.max, it.attachedMain, it.counterAttached)
                    }
                }
            }
        }
    }
}