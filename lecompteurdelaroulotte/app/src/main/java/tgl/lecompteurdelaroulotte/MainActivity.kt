package tgl.lecompteurdelaroulotte

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.ninenox.kotlinlocalemanager.AppCompatActivityBase
import fragments.*
import kotlinx.android.synthetic.main.simple_text_and_box_input.view.*
import kotlinx.android.synthetic.main.simple_text_input.view.input_text
import library.*
import java.util.*

class MainActivity : AppCompatActivityBase() {
    private val TAG = "==== MAINACTIVITY ===="

    lateinit var projectsList: ArrayList<Project>
    var currentProject: Project? = null
    var currentCounter: Counter? = null
    var currentRule: Rule? = null
    var currentComment: Comment? = null
    var seeWhat: String = "Comments"
    var pdfIsOpen: Boolean = true

    var screenOn: Boolean = false
    var volumeOn: Boolean = true
    lateinit var language: String

    private lateinit var fragmentStack: Stack<MyFragment>
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db: MyDatabase
    private var lastMenu: String? = null
    private val PDF_SELECTION_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setProperties()
        setLanguage()
        setToolbar()

        // Database
        db = MyDatabase(this)
        projectsList = db.getAllProjects(this)

        //NavigationView
        navView = this.findViewById(R.id.nav_view)
        setDrawer()

        // Fragments
        fragmentStack = Stack()
        openFragment(HomeFragment())
    }

    private fun setToolbar() {
        toolbar = this.findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = this.findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setProperties() {
        Helper.init(this)
        screenOn = Helper.getConfigValue("screen_on") == true.toString()
        volumeOn = Helper.getConfigValue("volume_on") == true.toString()
        if (screenOn) {
            // maintain screen open during activity
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun setLanguage() {
        language = Helper.getConfigValue("language").toString()
        if (language != "-" && Helper.getLanguage() != language) {
            // the following call restart the activity so it basically stops there
            setNewLocale(language)
        }
        if (Build.VERSION.SDK_INT >= 24) {
            val locs = Resources.getSystem().configuration.locales
            if (language == "-") {
                for (x in 0 until locs.size()) {
                    val thelangue = locs[x].language.toUpperCase()
                    if (Helper.isLanguageAvailable(thelangue)) {
                        if (Helper.getLanguage() != thelangue)
                            setNewLocale(thelangue)
                        break
                    }
                }
            }
        } else {
            val thelangue = Resources.getSystem().configuration.locale.language.toUpperCase()
            if (language == "-" && Helper.getLanguage() != thelangue) {
                if (Helper.isLanguageAvailable(thelangue))
                    setNewLocale(thelangue)
            }
        }
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
        if (!force && lastMenu != null && lastMenu == which) return
        if (force) myMenu.clear()
        lastMenu = which
        when (which) {
            "home" -> {
                myMenu.clear()
            }
            "project" -> {
                myMenu.add(R.string.edit_notes).apply {
                    icon = ContextCompat.getDrawable(context, R.drawable.icon_notes)
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                    setOnMenuItemClickListener {
                        context.openFragment(NotesFragment())
                        true
                    }
                }
                myMenu.add(R.string.add_counter).apply {
                    setOnMenuItemClickListener {
                        val viewInflated = LayoutInflater.from(context).inflate(
                            R.layout.simple_text_input,
                            context.navView as ViewGroup,
                            false
                        )
                        val addCounter = MaterialAlertDialogBuilder(
                            context,
                            R.style.AlertDialogPositiveBtnFilled
                        )
                        addCounter.setView(viewInflated)
                            .setTitle(R.string.counter_name_id)
                            .setPositiveButton(R.string.ok) { dialog, _ ->
                                val counterName = viewInflated.input_text.text.toString()
                                if (currentProject!!.hasCounter(counterName)) {
                                    Toast.makeText(
                                        context,
                                        R.string.counter_already,
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                if (context.currentProject!!.myCounters.size > 0) {
                    myMenu.add(R.string.open_a_counter).apply {
                        setOnMenuItemClickListener {
                            val viewInflated = LayoutInflater.from(context).inflate(
                                R.layout.simple_spinner_input,
                                context.navView as ViewGroup,
                                false
                            )
                            val the_spinner = viewInflated.findViewById<Spinner>(R.id.input_spinner)
                            val arr = ArrayList<String>(context.currentProject!!.getCounters().size)
                            context.currentProject!!.getCounters().forEach { arr.add(it.name) }
                            val adapteur = ArrayAdapter(
                                context,
                                R.layout.support_simple_spinner_dropdown_item,
                                arr.toArray()
                            )
                            var selectedItem = arr[0]
                            adapteur.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                            the_spinner.adapter = adapteur
                            the_spinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                    override fun onItemSelected(
                                        p0: AdapterView<*>?,
                                        p1: View?,
                                        p2: Int,
                                        p3: Long
                                    ) {
                                        selectedItem = arr[p2]
                                    }
                                }
                            val editCounter = MaterialAlertDialogBuilder(
                                context,
                                R.style.AlertDialogPositiveBtnFilled
                            )
                            editCounter.setView(viewInflated)
                                .setTitle(R.string.project_name_id)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    currentCounter =
                                        currentProject!!.myCounters.find { it.name == selectedItem }
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
                        context.openFragment(SeeRules())
                        true
                    }
                }
                myMenu.add(R.string.my_comments).apply {
                    setOnMenuItemClickListener {
                        context.seeWhat = "Comments"
                        context.openFragment(SeeComments())
                        true
                    }
                }
                myMenu.add(R.string.clone_proj).apply {
                    setOnMenuItemClickListener {
                        val viewInflated = LayoutInflater.from(context).inflate(
                            R.layout.simple_text_and_box_input,
                            context.navView as ViewGroup,
                            false
                        )
                        viewInflated.tv.text = getString(R.string.with_data)
                        viewInflated.input_text.hint = getString(R.string.project_name)
                        val addCounter = MaterialAlertDialogBuilder(
                            context,
                            R.style.AlertDialogPositiveBtnFilled
                        )
                        addCounter.setView(viewInflated)
                            .setTitle(R.string.clone_proj)
                            .setPositiveButton(R.string.ok) { dialog, _ ->
                                val projectName = viewInflated.input_text.text.toString()
                                val data: Boolean = viewInflated.input_check_box.isChecked
                                if (context.projectsList.find { it.name == projectName } == null) {
                                    context.currentProject!!.clone(projectName, data)
                                    dialog.dismiss()
                                    context.openFragment(HomeFragment())
                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.project_already,
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                if (currentProject!!.pdf != null) {
                    if (pdfIsOpen) {
                        myMenu.add(R.string.hide_pdf).apply {
                            icon = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_baseline_visibility_off_24
                            )
                            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                            setOnMenuItemClickListener {
                                pdfIsOpen = false
                                openFragment(fragmentStack.pop())
                                context.setMenu("project", true)
                                true
                            }
                        }
                    } else {
                        myMenu.add(R.string.show_pdf).apply {
                            icon = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_baseline_visibility_24
                            )
                            DrawableCompat.setTint(
                                icon, ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                            setOnMenuItemClickListener {
                                pdfIsOpen = true
                                openFragment(fragmentStack.pop().javaClass.newInstance())
                                context.setMenu("project", true)
                                true
                            }
                        }

                    }
                }
                // open new pdf
                myMenu.add(R.string.open_new_pdf).apply {
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_picture_as_pdf_24
                    )
                    DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.white))
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                    setOnMenuItemClickListener {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/pdf"
                        }
                        startActivityForResult(
                            Intent.createChooser(intent, "Select PDF"),
                            PDF_SELECTION_CODE
                        )
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
                val addProj = MaterialAlertDialogBuilder(
                    context,
                    R.style.AlertDialogPositiveBtnFilled
                )
                val viewInflated = LayoutInflater.from(context).inflate(
                    R.layout.simple_text_input,
                    navView as ViewGroup,
                    false
                )
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
                                Toast.makeText(
                                    context,
                                    R.string.project_already,
                                    Toast.LENGTH_SHORT
                                ).show()
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
        navView.menu.add(R.string.tools).apply {
            setOnMenuItemClickListener {
                drawerLayout.closeDrawers()
                context.openFragment(ToolsFragment())
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
            currentProject!!.pdf = selectedPdf
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
        val order = currentProject!!.getCounters().size
        db.addCounterDB(currentProject!!, counterName, 0, 0, order, false, null)
        currentProject!!.addCounter(Counter(counterName, 0, order, false, null))

        if (currentProject!!.getCounters().size == 1)
            setMenu("project", true) // case where there was no counter then you add one => must make the menu again to add "opencounter"
    }

    fun deleteCounter(counter: Counter) {
        if (currentCounter == counter) {
            currentCounter = null
        }
        currentProject!!.myRules.forEach {
            if (it.counter == counter.name)
                deleteRuleOfProject(it)
        }
        db.deleteCounterDB(currentProject!!, counter.name)
        currentProject!!.deleteCounter(counter)

        if (currentProject!!.getCounters().size == 0)
            setMenu("project", true) // case where there was 1 counter then you remove it => must remove "opencounter" from the menu
    }

    fun updateCounterName(c: Counter, new_name: String) {
        db.deleteCounterDB(currentProject!!, c.name)
        db.addCounterDB(
            currentProject!!,
            new_name,
            c.etat,
            c.max,
            c.order,
            c.attachedMain,
            c.counterAttached
        )
    }

    fun addRuleToProject(r: Rule) {
        currentProject!!.addRule(r)
        db.addRuleDB(currentProject!!, r)
    }

    fun updateRule(r: Rule, new_r: Rule) {
        deleteRuleOfProject(r)
        addRuleToProject(new_r)
    }

    fun deleteRuleOfProject(r: Rule) {
        currentProject!!.deleteRule(r)
        db.deleteRuleDB(currentProject!!, r)
    }

    fun addCommentToProject(c: Comment) {
        currentProject!!.addComment(c)
        db.addCommentDB(currentProject!!, c)
    }

    fun updateComment(c: Comment, new_c: Comment) {
        deleteCommentOfProject(c)
        addCommentToProject(new_c)
    }

    fun deleteCommentOfProject(c: Comment) {
        currentProject!!.deleteComment(c)
        db.deleteCommentDB(currentProject!!, c)
    }

    fun deleteStepOfRule(r: Rule, s: Step) {
        db.deleteStepDB(currentProject!!, r, s)
        currentProject!!.deleteStepOfRule(r, s)
    }

    fun getNextCommentIdentifiant(): Int {
        val comments = currentProject!!.myComments
        if (comments.isEmpty()) return 0
        var max = 0
        comments.forEach { comment ->
            max = if (comment.num > max) comment.num else max
        }
        return max + 1
    }

    fun getNextRuleIdentifiant(): Int {
        val rules = currentProject!!.myRules
        if (rules.isEmpty()) return 0
        var max = 0
        rules.forEach { rule ->
            max = if (rule.num > max) rule.num else max
        }
        return max + 1
    }

    fun openFragment(frag: MyFragment, pop: Boolean = false) {
        if (!pop && (fragmentStack.empty() || frag::class != this.fragmentStack.peek()::class))
            fragmentStack.push(frag)
        supportFragmentManager.beginTransaction().replace(R.id.frame, frag, frag.TAG).commit()
    }

    /***********************************************************************************************
     * override of the activity functions
     */
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            fragmentStack.size >= 2 -> {
                fragmentStack.pop()
                openFragment(fragmentStack.peek()::class.java.newInstance(), true)
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
                        db.updateCounterDB(
                            thisit,
                            it.name,
                            it.etat,
                            it.max,
                            it.order,
                            it.attachedMain,
                            it.counterAttached
                        )
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