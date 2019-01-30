package lufra.lecompteurdelaroulotte

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import fragments.*
import kotlinx.android.synthetic.main.simple_text_input.view.*
import library.*
import java.util.*


class MainActivity: AppCompatActivity(){
    private val TAG = "===== MAINACTIVITY ====="
    lateinit var projectsList: ArrayList<Project>
    var actualProject: Project? = null
    var actualCounter: Counter? = null
    var actualFragment: Fragment? = null
    var actualRule: Rule? = null
    lateinit var toolbar: android.support.v7.widget.Toolbar
    lateinit var nav_view: NavigationView
    lateinit var drawer_layout: DrawerLayout
    lateinit var frags: Stack<Fragment>
    lateinit var db: MyDatabase

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Database
        db = MyDatabase(this)
        projectsList = db.getAllProjects()

        // Fragments
        frags = Stack()
        frags.push(HomeFragment() as Fragment)
        openFragment(HomeFragment() as Fragment)

        // Toolbar
        toolbar = this.findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        drawer_layout = this.findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //NavigationView
        nav_view = this.findViewById(R.id.nav_view)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    fun setMenu(which: String){
        // todo: rajouter projet en cours pour arriver sur ProjectFragment
        // todo: gérer les problèmes de stacks frag quand on clique plein de fois sur le même item du menu!!
        // todo: clear la stack frag quand on revient sur le home
        val context = this
        nav_view.menu.clear()
        when(which){
            "home" -> {
                projectsList.forEach {
                    val proj = it
                    nav_view.menu.add(it.name).apply {
                        setOnMenuItemClickListener {
                            context.actualProject = proj
                            context.frags.push(HomeFragment())
                            drawer_layout.closeDrawers()
                            context.openFragment(ProjectFragment())
                            true
                        }
                    }
                }
            }
            "project" -> {
                nav_view.menu.add(R.string.home).apply{
                    setOnMenuItemClickListener {
                        context.openFragment(HomeFragment())
                        drawer_layout.closeDrawers()
                        true
                    }
                }
                nav_view.menu.add(R.string.edit_notes).apply{
                    setOnMenuItemClickListener {
                        context.frags.push(actualFragment)
                        drawer_layout.closeDrawers()
                        context.openFragment(NotesFragment())
                        true
                    }
                }
                nav_view.menu.add(R.string.add_counter).apply {
                    setOnMenuItemClickListener {
                        val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, context.nav_view as ViewGroup, false)
                        val addCounter = AlertDialog.Builder(context)
                        addCounter.setView(viewInflated)
                                .setTitle(R.string.counter_name_id)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    val counterName = viewInflated.input_text.text.toString()
                                    if (actualProject!!.has_counter(counterName)){
                                        Toast.makeText(context,R.string.counter_already, Toast.LENGTH_SHORT).show()
                                    }else{
                                        context.createCounter(counterName)
                                        drawer_layout.closeDrawers()
                                        context.setMenu("project")
                                    }
                                    dialog.dismiss()
                                }
                                .setNegativeButton(R.string.cancel) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                        true
                    }
                }
                if(context.actualProject!!.myCounters.size > 0){
                    context.actualProject!!.myCounters.forEach {
                        val count = it
                        val text = getResources().getString(R.string.counter) + it.name
                        nav_view.menu.add(text).apply{
                            setOnMenuItemClickListener {
                                context.frags.push(actualFragment)
                                drawer_layout.closeDrawers()
                                context.actualCounter = count
                                context.openFragment(CounterFragment())
                                true
                            }
                        }
                    }
                }
                nav_view.menu.add(R.string.my_rules).apply{
                    setOnMenuItemClickListener {
                        context.frags.push(actualFragment)
                        drawer_layout.closeDrawers()
                        context.openFragment(SeeRulesFragment())
                        true
                    }
                }
            }
        }

    }

    fun createProject(projectName: String){
        db.addProjectDB(projectName, " ")
        projectsList.add(Project(projectName))
        setMenu("home")
    }

    fun deleteProject(proj: Project){
        db.deleteProjectDB(proj.toString())
        projectsList.remove(proj)
        setMenu("home")
    }

    fun createCounter(counterName: String){
        val order = actualProject!!.getCounters().size
        db.addCounterDB(actualProject.toString(), counterName, 0, 0, 0, order, false, null)
        actualProject!!.addCounter(Counter(counterName, 0, order, false, null))
    }

    fun deleteCounter(counter: Counter){
        if (actualCounter == counter){
            actualCounter = null
        }
        db.deleteCounterDB(actualProject.toString(), counter.name)
        actualProject!!.deleteCounter(counter)
    }

    fun updateCounterName(c: Counter, new_name: String){
        db.deleteCounterDB(actualProject.toString(), c.name)
        db.addCounterDB(actualProject.toString(), new_name, c.etat, c.tours, c.max, c.order, c.attachedMain, c.counterAttached)
    }

    fun addRuleToProject(r: Rule){
        actualProject!!.addRule(r)
        db.addRuleDB(actualProject!!.toString(), r)
    }

    fun updateRule(r: Rule, new_r: Rule){
        db.deleteRuleDB(actualProject!!.toString(), r)
        actualProject!!.deleteRule(r)
        db.addRuleDB(actualProject!!.toString(), new_r)
        actualProject!!.addRule(new_r)
    }

    fun deleteRuleOfProject(r: Rule){
        actualProject!!.deleteRule(r)
        db.deleteRuleDB(actualProject!!.toString(), r)
    }

    fun deleteStepOfRule(r: Rule, s: Step){
        db.deleteStepDB(actualProject!!.toString(), r, s)
        actualProject!!.deleteStepOfRule(r, s)
    }

    fun openFragment(frag: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame, frag).commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (!frags.isEmpty())
                openFragment(frags.pop())
            else {
                saveState()
                super.onBackPressed()
            }
        }
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
                        db.updateCounterDB(proj.toString(), it.name, it.etat, it.tours, it.max, it.order, it.attachedMain, it.counterAttached)
                    }
                }
            }
        }
    }

    fun createTextFromRule(r: Rule): String{
        var s = getString(R.string.rule)
        s+=": " + getString(R.string.from_row) + " " + r.start.toString() + "\n"
        for(i in 0 until r.steps.size){
            if(i != 0){
                s += getString(R.string.andthen) + " "
            }
            if (r.steps[i].augm) s += getString(R.string.augmentation)
            else s += getString(R.string.diminution)
            s += " " + r.steps[i].one.toString() + " " + getString(R.string.rule_text1) + " "
            s += r.steps[i].two.toString() + " " + getString(R.string.rows) + " "
            s += r.steps[i].three.toString() + " " + getString(R.string.stitch) + "\n"
        }
        return s
    }
}