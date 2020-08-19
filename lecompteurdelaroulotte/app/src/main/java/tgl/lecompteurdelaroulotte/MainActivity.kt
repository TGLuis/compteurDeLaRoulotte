package tgl.lecompteurdelaroulotte

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.support.design.widget.NavigationView
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
import kotlinx.android.synthetic.main.simple_text_and_box_input.view.*
import kotlinx.android.synthetic.main.simple_text_input.view.input_text
import library.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "==== MAINACTIVITY ===="

    lateinit var projectsList: ArrayList<Project>
    var actualProject: Project? = null
    var actualCounter: Counter? = null
    var actualRule: Rule? = null
    var actualComment: Comment? = null
    var seeWhat: String = "Comments"
    var pdfIsOpen: Boolean = true

    var screenOn: Boolean = false
    var volumeOn: Boolean = true
    var language: String = "en"

    private lateinit var frags: Stack<MyFragment>
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db: MyDatabase
    private lateinit var editCounter: AlertDialog.Builder
    private var lastMenu: String? = null
    private val PDF_SELECTION_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Database
        db = MyDatabase(this)
        projectsList = db.getAllProjects(this)

        // properties
        Helper.init(this)
        screenOn = Helper.getConfigValue("screen_on") == true.toString()
        volumeOn = Helper.getConfigValue("volume_on") == true.toString()
        if (screenOn) {
            // maintain screen open during activity
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        language = Helper.getConfigValue("language").toString()
        Log.e(TAG, language)
        Log.e(TAG, this.resources.configuration.locales.toString())
        if (Build.VERSION.SDK_INT >= 24) {
            var restart = false
            if (language != this.resources.configuration.locales[0].language)
                restart = true
            if (language == "-" || language == null.toString()) {
                language = this.resources.configuration.locales[0].language
                if (! Helper.isLanguageAvailable(language)) {
                    language = "en"
                }
            }
            if (language != this.resources.configuration.locales[0].language) {
                LocaleHelper.setLocale(this, language)
                recreate()
            }
            /*Helper.setLocale()
            if (restart) {
                Log.e(TAG, "restarting")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }*/
        }


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
        setDrawer()

        // Fragments
        frags = Stack()
        openFragment(HomeFragment())
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
    fun setMenu(which: String, force: Boolean = false) {
        val context = this
        val myMenu = toolbar.menu
        if (!force && lastMenu != null && lastMenu == which)
            return
        if (force)
            myMenu.clear()
        lastMenu = which
        when (which) {
            "home" -> {
                myMenu.clear()
            }
            "project" -> {
                myMenu.add(R.string.edit_notes).apply {
                    icon = context.getDrawable(R.drawable.icon_notes)
                    setShowAsAction(1)
                    setOnMenuItemClickListener {
                        context.openFragment(NotesFragment())
                        true
                    }
                }
                myMenu.add(R.string.add_counter).apply {
                    setOnMenuItemClickListener {
                        val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, context.navView as ViewGroup, false)
                        val addCounter = AlertDialog.Builder(context)
                        addCounter.setView(viewInflated)
                                .setTitle(R.string.counter_name_id)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    val counterName = viewInflated.input_text.text.toString()
                                    if (actualProject!!.hasCounter(counterName)) {
                                        Toast.makeText(context, R.string.counter_already, Toast.LENGTH_SHORT).show()
                                    } else {
                                        context.createCounter(counterName)
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
                if (context.actualProject!!.myCounters.size > 0) {
                    myMenu.add(R.string.open_a_counter).apply {
                        setOnMenuItemClickListener {
                            val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_spinner_input, context.navView as ViewGroup, false)
                            val the_spinner = viewInflated.findViewById<Spinner>(R.id.input_spinner)
                            val arr = ArrayList<String>(context.actualProject!!.getCounters().size)
                            context.actualProject!!.getCounters().forEach { arr.add(it.name) }
                            val adapteur = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, arr.toArray()!!)
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
                myMenu.add(R.string.my_rules).apply {
                    setOnMenuItemClickListener {
                        context.seeWhat = "Rules"
                        context.openFragment(SeeFragment())
                        true
                    }
                }
                myMenu.add(R.string.my_comments).apply {
                    setOnMenuItemClickListener {
                        context.seeWhat = "Comments"
                        context.openFragment(SeeFragment())
                        true
                    }
                }
                myMenu.add(R.string.clone_proj).apply {
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
                                    if (context.projectsList.find { it.name == projectName } == null) {
                                        context.actualProject!!.clone(projectName, data)
                                        dialog.dismiss()
                                        context.openFragment(HomeFragment())
                                    } else {
                                        Toast.makeText(context, R.string.project_already, Toast.LENGTH_SHORT).show()
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
                if (actualProject!!.pdf != null) {
                    if (pdfIsOpen) {
                        myMenu.add(R.string.hide_pdf).apply {
                            setOnMenuItemClickListener {
                                pdfIsOpen = false
                                openFragment(frags.pop())
                                context.setMenu("project", true)
                                true
                            }
                        }
                    } else {
                        myMenu.add(R.string.show_pdf).apply {
                            setOnMenuItemClickListener {
                                pdfIsOpen = true
                                openFragment(frags.pop().javaClass.newInstance())
                                true
                            }
                        }

                    }
                }
                // open new pdf
                myMenu.add(R.string.open_new_pdf).apply {
                    setOnMenuItemClickListener {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/pdf"
                        }
                        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_SELECTION_CODE)
                        // The function "onActivityResult" will be called when the activity to select a pdf has finished
                        true
                    }
                }
            }
        }
    }

    private fun setDrawer() {
        val context = this
        navView.menu.clear()
        navView.menu.add(R.string.home).apply {
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(HomeFragment())
                true
            }
        }
        navView.menu.add(R.string.add_project).apply {
            setOnMenuItemClickListener {
                val addProj = AlertDialog.Builder(context)
                val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, navView as ViewGroup, false)
                viewInflated.input_text.hint = context.getString(R.string.project_name)
                addProj.setView(viewInflated)
                        .setTitle(R.string.project_name_id)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            val projectName = viewInflated.input_text.text.toString()
                            if (context.projectsList.find { it.name == projectName } == null) {
                                context.createProject(projectName)
                                drawerLayout.closeDrawers()
                                openFragment(HomeFragment())
                                dialog.dismiss()
                            } else {
                                Toast.makeText(context, R.string.project_already, Toast.LENGTH_SHORT).show()
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
        navView.menu.add(R.string.parameters).apply {
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(ParametersFragment())
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
        navView.menu.add(R.string.HelpTitle).apply {
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(HelpFragment())
                true
            }
        }
        navView.menu.add(R.string.AboutTitle).apply {
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(AboutFragment())
                true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdf = data.data
            pdfIsOpen = true
            actualProject!!.pdf = selectedPdf
            setMenu("project", true)
            openFragment(ProjectFragment())
        }
    }


    /***********************************************************************************************
     *  Fucntions to manage a project and his features
     */
    fun createProject(projectName: String): Project {
        val p = Project(this, projectName)
        db.addProjectDB(p)
        projectsList.add(p)
        return p
    }

    fun deleteProject(proj: Project) {
        db.deleteProjectDB(proj)
        projectsList.remove(proj)
    }

    private fun createCounter(counterName: String) {
        val order = actualProject!!.getCounters().size
        db.addCounterDB(actualProject!!, counterName, 0, 0, order, false, null)
        actualProject!!.addCounter(Counter(counterName, 0, order, false, null))

        if (actualProject!!.getCounters().size == 1)
            setMenu("project", true) // case where there was no counter then you add one => must make the menu again to add "opencounter"
    }

    fun deleteCounter(counter: Counter) {
        if (actualCounter == counter) {
            actualCounter = null
        }
        actualProject!!.myRules.forEach {
            if (it.counter == counter.name)
                deleteRuleOfProject(it)
        }
        db.deleteCounterDB(actualProject!!, counter.name)
        actualProject!!.deleteCounter(counter)

        if (actualProject!!.getCounters().size == 0)
            setMenu("project", true) // case where there was 1 counter then you remove it => must remove "opencounter" from the menu
    }

    fun updateCounterName(c: Counter, new_name: String) {
        db.deleteCounterDB(actualProject!!, c.name)
        db.addCounterDB(actualProject!!, new_name, c.etat, c.max, c.order, c.attachedMain, c.counterAttached)
    }

    fun addRuleToProject(r: Rule) {
        actualProject!!.addRule(r)
        db.addRuleDB(actualProject!!, r)
    }

    fun updateRule(r: Rule, new_r: Rule) {
        deleteRuleOfProject(r)
        addRuleToProject(new_r)
    }

    fun deleteRuleOfProject(r: Rule) {
        actualProject!!.deleteRule(r)
        db.deleteRuleDB(actualProject!!, r)
    }

    fun addCommentToProject(c: Comment) {
        actualProject!!.addComment(c)
        db.addCommentDB(actualProject!!, c)
    }

    fun updateComment(c: Comment, new_c: Comment) {
        deleteCommentOfProject(c)
        addCommentToProject(new_c)
    }

    fun deleteCommentOfProject(c: Comment) {
        actualProject!!.deleteComment(c)
        db.deleteCommentDB(actualProject!!, c)
    }

    fun deleteStepOfRule(r: Rule, s: Step) {
        db.deleteStepDB(actualProject!!, r, s)
        actualProject!!.deleteStepOfRule(r, s)
    }

    fun openFragment(frag: MyFragment, pop: Boolean = false) {
        if (!pop && (frags.empty() || frag::class != this.frags.peek()::class))
            frags.push(frag)
        supportFragmentManager.beginTransaction().replace(R.id.frame, frag, frag.TAG()).commit()
    }

    fun getNextCommentIdentifiant(): Int {
        val comments = actualProject!!.myComments
        if (comments.isEmpty()) {
            return 0
        }
        var max = 0
        comments.forEach { comment ->
            max = if (comment.num > max) comment.num else max
        }
        return max + 1
    }

    fun getNextRuleIdentifiant(): Int {
        val rules = actualProject!!.myRules
        if (rules.isEmpty()) {
            return 0
        }
        var max = 0
        rules.forEach { rule ->
            max = if (rule.num > max) rule.num else max
        }
        return max + 1
    }
    /***********************************************************************************************
     * override of the activity functions
     */
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            frags.size >= 2 -> {
                frags.pop()
                openFragment(frags.peek(), true)
            }
            else -> {
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

    /***********************************************************************************************
     * Some more functions
     */
    private fun saveState() {
        Helper.saveProperties()
        if (projectsList.isNotEmpty()) {
            projectsList.forEach { thisit ->
                db.updateProjectDB(thisit)
                val counters = thisit.getCounters()
                if (counters.isNotEmpty()) {
                    counters.forEach {
                        db.updateCounterDB(thisit, it.name, it.etat, it.max, it.order, it.attachedMain, it.counterAttached)
                    }
                }
                // fix disparition of some rules and comments by force save..
                val rules = thisit.myRules
                if (rules.isNotEmpty()) {
                    rules.forEach {
                        if (!db.isRuleInDB(thisit, it))
                            db.addRuleDB(thisit, it)
                    }
                }
                val comments = thisit.myComments
                if (comments.isNotEmpty()) {
                    comments.forEach {
                        if (!db.isCommentInDB(thisit, it))
                            db.addCommentDB(thisit, it)
                    }
                }
            }
        }
    }
}