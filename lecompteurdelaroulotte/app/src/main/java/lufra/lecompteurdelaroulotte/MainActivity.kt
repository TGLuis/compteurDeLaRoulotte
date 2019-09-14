package lufra.lecompteurdelaroulotte

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import fragments.*
import kotlinx.android.synthetic.main.simple_text_input.view.*
import library.*
import java.util.*
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.simple_text_and_box_input.view.*
import kotlinx.android.synthetic.main.simple_text_input.view.input_text
import java.util.jar.Manifest

class MainActivity: AppCompatActivity() {
    private val TAG = "==== MAINACTIVITY ===="

    lateinit var projectsList: ArrayList<Project>
    var actualProject: Project? = null
    var actualCounter: Counter? = null
    var actualRule: Rule? = null
    var actualComment: Comment? = null
    var seeWhat: String = "Comments"

    var screen_on: Boolean = false
    var volume_on: Boolean = true

    lateinit var frags: Stack<MyFragment>
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db: MyDatabase
    private lateinit var editCounter: AlertDialog.Builder
    private val READ_REQUEST_CODE = 42

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)

        //this.deleteDatabase("database.sqlite")
        // Database
        db = MyDatabase(this)
        projectsList = db.getAllProjects(this)

        // properties
        Helper.init(this)
        screen_on = Helper.getConfigValue("screen_on") == "true"
        volume_on = Helper.getConfigValue("volume_on") == "true"
        if(screen_on) {
            // maintain screen open during activity
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        // Fragments
        frags = Stack()
        openFragment(HomeFragment())

        // Toolbar
        toolbar = this.findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = this.findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
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
        navView.menu.add(R.string.home).apply{
            setOnMenuItemClickListener {
                context.openFragment(HomeFragment())
                drawerLayout.closeDrawers()
                true
            }
        }
        when(which){
            "home" -> {
                navView.menu.add(R.string.add_project).apply {
                    setOnMenuItemClickListener {
                        val addProj = AlertDialog.Builder(context)
                        val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, navView as ViewGroup, false)
                        viewInflated.input_text.hint = context.getString(R.string.project_name)
                        addProj.setView(viewInflated)
                                .setTitle(R.string.project_name_id)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    val projectName = viewInflated.input_text.text.toString()
                                    if(context.projectsList.find { it.name == projectName } == null){
                                        context.createProject(projectName)
                                        openFragment(HomeFragment())
                                        dialog.dismiss()
                                    }else {
                                        Toast.makeText(context,R.string.project_already, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton(R.string.cancel) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setCancelable(false)
                                .create()
                                .show()
                        true
                    }
                }
                navView.menu.add(R.string.archives).apply {
                    setOnMenuItemClickListener {
                        drawerLayout.closeDrawers()
                        context.openFragment(ArchiveFragment())
                        true
                    }
                }
            }
            "project" -> {
                navView.menu.add(actualProject!!.name).apply {
                    setOnMenuItemClickListener {
                        drawerLayout.closeDrawers()
                        context.openFragment(ProjectFragment())
                        true
                    }
                }
                navView.menu.add(R.string.edit_notes).apply{
                    setOnMenuItemClickListener {
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
                                .setCancelable(false)
                                .create()
                                .show()
                        true
                    }
                }
                if(context.actualProject!!.myCounters.size > 0){
                    navView.menu.add(R.string.open_a_counter).apply{
                        setOnMenuItemClickListener {
                            val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_spinner_input, context.navView as ViewGroup, false)
                            val the_spinner = viewInflated.findViewById<Spinner>(R.id.input_spinner)
                            val arr = ArrayList<String>(context.actualProject!!.getCounters().size)
                            context.actualProject!!.getCounters().forEach {arr.add(it.name)}
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
                            editCounter.setView(viewInflated)
                                    .setTitle(R.string.project_name_id)
                                    .setPositiveButton(R.string.ok) { dialog, _ ->
                                        actualCounter = actualProject!!.myCounters.find { it.name == selectedItem }
                                        context.openFragment(CounterFragment())
                                        drawerLayout.closeDrawers()
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .setCancelable(false)
                                    .create()
                                    .show()
                            true
                        }
                    }
                }
                navView.menu.add(R.string.my_rules).apply{
                    setOnMenuItemClickListener {
                        drawerLayout.closeDrawers()
                        context.seeWhat = "Rules"
                        context.openFragment(SeeFragment())
                        true
                    }
                }
                navView.menu.add(R.string.my_comments).apply{
                    setOnMenuItemClickListener {
                        drawerLayout.closeDrawers()
                        context.seeWhat = "Comments"
                        context.openFragment(SeeFragment())
                        true
                    }
                }
                navView.menu.add(R.string.clone_proj).apply{
                    setOnMenuItemClickListener {
                        val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_and_box_input, context.navView as ViewGroup, false)
                        viewInflated.tv.text = getString(R.string.with_data)
                        viewInflated.input_text.hint = getString(R.string.project_name)
                        val addCounter = AlertDialog.Builder(context)
                        addCounter.setView(viewInflated)
                                .setTitle(R.string.clone_proj)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    val projectName = viewInflated.input_text.text.toString()
                                    val data: Boolean = viewInflated.input_check_box.isChecked
                                    if(context.projectsList.find { it.name == projectName } == null){
                                        context.actualProject!!.clone(projectName, data)
                                        dialog.dismiss()
                                        context.openFragment(HomeFragment())
                                        drawerLayout.closeDrawers()
                                    }else {
                                        Toast.makeText(context,R.string.project_already, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton(R.string.cancel) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setCancelable(false)
                                .create()
                                .show()
                        true
                    }
                }
            }
        }

        navView.menu.add(R.string.parameters).apply{
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(ParametersFragment())
                true
            }
        }
        navView.menu.add(R.string.HelpTitle).apply{
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(HelpFragment())
                true
            }
        }
        navView.menu.add(R.string.AboutTitle).apply{
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(AboutFragment())
                true
            }
        }
        /*
        navView.menu.add("Test find file").apply{
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
                }
                startActivityForResult(intent, READ_REQUEST_CODE)
                true
            }
        }
        */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            resultData?.data?.also { uri ->
                Log.i(TAG, "Uri: $uri")
                /*val pfd = this.contentResolver.openFileDescriptor(uri, "r")
                val renderer = PdfRenderer(pfd)
                var i = 0
                while (i < renderer.pageCount){
                    val page = renderer.openPage(i)
                    page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    // mBitmap is the bitmap destination !! suivre le github :
                    // https://github.com/googlesamples/android-PdfRendererBasic/blob/master/kotlinApp/Application/src/main/java/com/example/android/pdfrendererbasic/PdfRendererBasicFragment.kt
                    page.close()
                    i++
                }
                renderer.close()*/
            }

        }
    }


    /***********************************************************************************************
     *  Fucntions to manage a project and his features
     */
    fun createProject(projectName: String): Project{
        val p = Project(this, projectName)
        db.addProjectDB(projectName, " ")
        projectsList.add(p)
        return p
    }

    fun deleteProject(proj: Project){
        db.deleteProjectDB(proj.toString())
        projectsList.remove(proj)
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

    fun addCommentToProject(c: Comment){
        actualProject!!.addComment(c)
        db.addCommentDB(actualProject!!.toString(), c)
    }

    fun updateComment(c: Comment, new_c: Comment){
        db.deleteCommentDB(actualProject!!.toString(), c)
        actualProject!!.deleteComment(c)
        db.addCommentDB(actualProject!!.toString(), new_c)
        actualProject!!.addComment(new_c)
    }

    fun deleteCommentOfProject(c: Comment){
        actualProject!!.deleteComment(c)
        db.deleteCommentDB(actualProject!!.toString(), c)
    }

    fun deleteStepOfRule(r: Rule, s: Step){
        db.deleteStepDB(actualProject!!.toString(), r, s)
        actualProject!!.deleteStepOfRule(r, s)
    }

    fun openFragment(frag: MyFragment, pop: Boolean = false){
        if(!pop && (frags.empty() || frag::class != this.frags.peek()::class)){
            frags.push(frag)
        }
        supportFragmentManager.beginTransaction().replace(R.id.frame, frag, frag.TAG()).commit()
    }

    /***********************************************************************************************
     * override of the activity functions
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (frags.size >= 2) {
            frags.pop()
            openFragment(frags.peek(), true)
        } else {
            saveState()
            super.onBackPressed()
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

    /***********************************************************************************************
     * Some more functions
     */
    private fun saveState(){
        if(!projectsList.isEmpty()){
            projectsList.forEach{ thisit ->
                val proj = thisit
                db.updateProjectDB(proj.toString(), proj.etat, proj.archived, proj.notes)
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
        s += if(r.counter == "") getString(R.string.on_main) else getString(R.string.on_counter) + " " + r.counter
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

    fun createTextFromComment(c: Comment): String{
        var s = getString(R.string.comment)
        s += ":\n"
        s += if(c.counter == "") getString(R.string.on_main) else getString(R.string.on_counter) + " " + c.counter
        s += "\n" + getString(R.string.from_row) + " " + c.start.toString() + " "
        s += getString(R.string.to_row) + " " + c.end.toString() + "\n"
        s += c.comment
        return s
    }
}