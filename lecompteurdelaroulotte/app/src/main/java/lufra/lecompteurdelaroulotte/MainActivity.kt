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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
    private lateinit var toolbar: android.support.v7.widget.Toolbar
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    lateinit var frags: Stack<Fragment>
    private lateinit var db: MyDatabase
    private lateinit var editCounter: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Database
        db = MyDatabase(this)
        projectsList = db.getAllProjects(this)

        // Fragments
        frags = Stack()
        frags.push(HomeFragment() as Fragment)
        openFragment(HomeFragment() as Fragment)

        // Toolbar
        toolbar = this.findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = this.findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //NavigationView
        navView = this.findViewById(R.id.nav_view)
        editCounter = AlertDialog.Builder(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * Contextual menu, create dynamically the menu in function of the parameter 'which'
     * If 'which' is "home" then it will create the menu for the home.
     * If 'which' is "project" it will create the menu for every screen with a project.
     */
    fun setMenu(which: String){
        val context = this
        navView.menu.clear()
        when(which){
            "home" -> {
                projectsList.forEach {
                    val proj = it
                    navView.menu.add(it.name).apply {
                        setOnMenuItemClickListener {
                            context.actualProject = proj
                            context.frags.push(HomeFragment())
                            drawerLayout.closeDrawers()
                            context.openFragment(ProjectFragment())
                            true
                        }
                    }
                }
            }
            "project" -> {
                navView.menu.add(R.string.home).apply{
                    setOnMenuItemClickListener {
                        context.openFragment(HomeFragment())
                        drawerLayout.closeDrawers()
                        true
                    }
                }
                navView.menu.add(actualProject!!.name).apply {
                    setOnMenuItemClickListener {
                        if(context.frags.peek() !is ProjectFragment)
                            context.frags.push(actualFragment)
                        drawerLayout.closeDrawers()
                        context.openFragment(ProjectFragment())
                        true
                    }
                }
                navView.menu.add(R.string.edit_notes).apply{
                    setOnMenuItemClickListener {
                        if(context.frags.peek() !is NotesFragment)
                            context.frags.push(actualFragment)
                        drawerLayout.closeDrawers()
                        context.openFragment(NotesFragment())
                        true
                    }
                }
                navView.menu.add(R.string.add_counter).apply {
                    setOnMenuItemClickListener {
                        val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, context.navView as ViewGroup, false)
                        val addCounter = AlertDialog.Builder(context)
                        addCounter.setView(viewInflated)
                                .setTitle(R.string.counter_name_id)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    val counterName = viewInflated.input_text.text.toString()
                                    if (actualProject!!.has_counter(counterName)){
                                        Toast.makeText(context,R.string.counter_already, Toast.LENGTH_SHORT).show()
                                    }else{
                                        context.createCounter(counterName)
                                        drawerLayout.closeDrawers()
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
                    val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_spinner_input, context.navView as ViewGroup, false)
                    val the_spinner = viewInflated.findViewById<Spinner>(R.id.input_spinner)
                    val arr = ArrayList<String>(context.actualProject!!.getCounters().size)
                    this.actualProject!!.getCounters().forEach {arr.add(it.name)}
                    val adapteur = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray())
                    var selectedItem = arr[0]
                    adapteur.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                    the_spinner.adapter = adapteur
                    the_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            selectedItem = arr[p2]
                        }
                    }
                    navView.menu.add(R.string.open_a_counter).apply{
                        setOnMenuItemClickListener {
                            editCounter.setView(viewInflated)
                                    .setTitle(R.string.project_name_id)
                                    .setPositiveButton(R.string.ok) { dialog, _ ->
                                        actualCounter = actualProject!!.myCounters.find { it.name == selectedItem }
                                        context.frags.push(actualFragment)
                                        context.openFragment(CounterFragment())
                                        drawerLayout.closeDrawers()
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
                }
                navView.menu.add(R.string.my_rules).apply{
                    setOnMenuItemClickListener {
                        if(context.frags.peek() !is SeeRulesFragment)
                            context.frags.push(actualFragment)
                        drawerLayout.closeDrawers()
                        context.openFragment(SeeRulesFragment())
                        true
                    }
                }
            }
        }

    }

    /***********************************************************************************************
     *  Fucntions to manage a project and his features
     */
    fun createProject(projectName: String){
        db.addProjectDB(projectName, " ")
        projectsList.add(Project(this, projectName))
        setMenu("home")
    }

    fun deleteProject(proj: Project){
        db.deleteProjectDB(proj.toString())
        projectsList.remove(proj)
        setMenu("home")
    }

    private fun createCounter(counterName: String){
        val order = actualProject!!.getCounters().size
        db.addCounterDB(actualProject.toString(), counterName, 0, 0, order, false, null)
        actualProject!!.addCounter(Counter(counterName, 0, order, false, null))
    }

    fun deleteCounter(counter: Counter){
        if (actualCounter == counter){
            actualCounter = null
        }
        actualProject!!.myRules.forEach {
            if(it.counter == counter.name)
                deleteRuleOfProject(it)
        }
        db.deleteCounterDB(actualProject.toString(), counter.name)
        actualProject!!.deleteCounter(counter)
    }

    fun updateCounterName(c: Counter, new_name: String){
        db.deleteCounterDB(actualProject.toString(), c.name)
        db.addCounterDB(actualProject.toString(), new_name, c.etat, c.max, c.order, c.attachedMain, c.counterAttached)
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

    /***********************************************************************************************
     * override of the activity functions
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (!frags.isEmpty()) {
            openFragment(frags.pop())
        } else {
            saveState()
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        saveState()
        actualFragment = HomeFragment()
        super.onDestroy()
    }

    override fun onPause() {
        saveState()
        actualFragment = HomeFragment()
        super.onPause()
    }

    override fun onStop() {
        saveState()
        actualFragment = HomeFragment()
        super.onStop()
    }

    /***********************************************************************************************
     * Some more functions
     */
    private fun saveState(){
        if(!projectsList.isEmpty()){
            projectsList.forEach{ thisit ->
                val proj = thisit
                db.updateProjectDB(proj.toString(), proj.etat, proj.notes)
                val coun = proj.getCounters()
                if(! coun.isEmpty()){
                    coun.forEach{
                        db.updateCounterDB(proj.toString(), it.name, it.etat, it.max, it.order, it.attachedMain, it.counterAttached)
                    }
                }
            }
        }
    }

    fun createTextFromRule(r: Rule): String{
        var s = getString(R.string.rule)
        s += ": " + r.comment + "\n"
        s += if(r.counter == "") getString(R.string.rule_on_main) else getString(R.string.rule_on_counter) + " " + r.counter + "\n"
        s += "\n" + getString(R.string.from_row) + " " + r.start.toString() + "\n"
        for(i in 0 until r.steps.size){
            if(i != 0){
                s += getString(R.string.andthen) + " "
            }
            s += if (r.steps[i].augm) getString(R.string.augmentation)
            else getString(R.string.diminution)
            s += " " + r.steps[i].one.toString() + " " + getString(R.string.rule_text1) + " "
            s += r.steps[i].two.toString() + " " + getString(R.string.rows) + " "
            s += r.steps[i].three.toString() + " " + getString(R.string.stitch) + "\n"
        }
        return s
    }
}