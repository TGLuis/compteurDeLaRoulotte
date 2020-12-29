package fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import library.Dialogs
import library.Project
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

class HomeFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== MAINFRAGMENT ====="

    private lateinit var listView_projects: ListView
    private lateinit var button_addProject: Button
    private lateinit var editText_projectName: EditText
    private lateinit var projects: ArrayList<Project>
    private lateinit var that: HomeFragment
    private lateinit var clickedProject: Project

    inner class ProjectAdapter(context: Context, list: ArrayList<Project>) : ArrayAdapter<Project>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView? = null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val proj = super.getItem(position)!!
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null) {
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_project_item, parent, false)
                viewHolder.msg = projectView.findViewById(R.id.project_text)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }

            val imageButton_cancel = projectView.findViewById<ImageButton>(R.id.archive_image)
            val constraintLayout = projectView.findViewById<ConstraintLayout>(R.id.content)
            val textView_projectName = projectView.findViewById<TextView>(R.id.project_text)

            constraintLayout.setOnClickListener { openProj(proj) }
            textView_projectName.setOnClickListener { openProj(proj) }
            imageButton_cancel.setOnClickListener {
                clickedProject = proj
                Dialogs.displaySimpleDialog(context, R.string.archive_project, ::archiveProject)
            }

            viewHolder.msg!!.text = proj.toString()
            return projectView
        }

        private fun openProj(proj: Project) {
            (context as MainActivity).currentProject = proj
            (context as MainActivity).openFragment(ProjectFragment())
        }

        private fun archiveProject() {
            clickedProject.archived = true
            projects = ArrayList((context as MainActivity).projectsList.filter { proj -> !proj.archived })
            listView_projects.adapter = that.ProjectAdapter(context, projects)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        inflater.inflate(R.layout.simple_text_input, container, false)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        that = this

        projects = ArrayList(context.projectsList.filter { proj -> !proj.archived })

        listView_projects = context.findViewById(R.id.listProject)
        listView_projects.adapter = that.ProjectAdapter(context, projects)

        button_addProject = context.findViewById(R.id.button_add_project)
        button_addProject.setOnClickListener {
            val view: View = this.layoutInflater.inflate(R.layout.simple_text_input, null)
            editText_projectName = view.findViewById(R.id.input_text)
            editText_projectName.hint = context.getString(R.string.project_name)
            Dialogs.displayCustomDialog(context, view, R.string.project_name_id, ::registerProjectName)
        }

        context.setMenu("home")
        context.title = context.getString(R.string.app_name)
    }

    private fun registerProjectName(): Boolean {
        val projectName = editText_projectName.text.toString()
        if (context.projectsList.find { it.name == projectName } == null) {
            context.createProject(projectName)
            projects = ArrayList(context.projectsList.filter { proj -> !proj.archived })
            listView_projects.adapter = this.ProjectAdapter(context, projects)
            return true
        }
        Toast.makeText(context, R.string.project_already, Toast.LENGTH_SHORT).show()
        return false
    }
}