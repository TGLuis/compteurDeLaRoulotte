package fragments

import android.os.Bundle
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import library.Dialogs
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R
import library.Project

class ArchiveFragment: MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "===== ARCHIVEFRAGMENT ====="
    private lateinit var adapteur:ProjectAdapter

    private lateinit var listView_projects: ListView
    private lateinit var archivedProjects: ArrayList<Project>
    private lateinit var that: ArchiveFragment
    private lateinit var clickedProject: Project

    inner class ProjectAdapter(context: Context, list: ArrayList<Project>) : ArrayAdapter<Project>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val proj = super.getItem(position)!!
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null){
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_project_archived_item, parent, false)
                viewHolder.msg = projectView.findViewById(R.id.project_text)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }

            val deleteButton = projectView.findViewById<ImageButton>(R.id.delete_image)
            val unarchiveButton = projectView.findViewById<ImageButton>(R.id.unarchive_image)
            val constraintLayout = projectView.findViewById<ConstraintLayout>(R.id.content)
            val enterText = projectView.findViewById<TextView>(R.id.project_text)

            deleteButton.setOnClickListener{
                clickedProject = proj
                Dialogs.displaySimpleDialog(context,R.string.delete_project, ::deleteProject)
            }

            unarchiveButton.setOnClickListener{
                clickedProject = proj
                Dialogs.displaySimpleDialog(context, R.string.unarchive_project, ::restoreProject)
            }

            constraintLayout.setOnClickListener {openProj(proj)}
            enterText.setOnClickListener {openProj(proj)}

            viewHolder.msg!!.text = proj.toString()
            return projectView
        }

        private fun openProj(proj: Project){
            (context as MainActivity).currentProject = proj
            (context as MainActivity).openFragment(ProjectFragment())
        }

        private fun deleteProject() {
            (context as MainActivity).deleteProject(clickedProject)
            archivedProjects = ArrayList((context as MainActivity).projectsList.filter{ proj -> proj.archived })
            listView_projects.adapter = that.ProjectAdapter(context, archivedProjects)
        }

        private fun restoreProject() {
            clickedProject.archived = false
            archivedProjects = ArrayList((context as MainActivity).projectsList.filter{ proj -> proj.archived })
            listView_projects.adapter = that.ProjectAdapter(context, archivedProjects)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_archive, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        that = this

        listView_projects = context.findViewById(R.id.listProject)

        archivedProjects = ArrayList(context.projectsList.filter{ proj -> proj.archived })
        adapteur = that.ProjectAdapter(context, archivedProjects)
        listView_projects.adapter = adapteur

        context.setMenu("home")
        context.title = context.getString(R.string.archives)
    }
}