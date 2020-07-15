package fragments

import android.os.Bundle
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.*
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import library.Project
import java.lang.Exception

class ArchiveFragment: MyFragment() {
    private lateinit var context: MainActivity
    private lateinit var adapteur:ProjectAdapter

    private lateinit var listViewProj: ListView
    private lateinit var addProj: AlertDialog.Builder
    private lateinit var archivedProjects: ArrayList<Project>
    private lateinit var that: ArchiveFragment

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
            deleteButton.setOnClickListener{
                val cancelDialog = AlertDialog.Builder(context)
                cancelDialog.setTitle(R.string.delete_project)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            (context as MainActivity).deleteProject(proj)
                            archivedProjects = ArrayList((context as MainActivity).projectsList.filter{ proj -> proj.archived })
                            listViewProj.adapter = that.ProjectAdapter(context, archivedProjects)
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                try{
                    cancelDialog.create()
                }catch (e: Exception){} finally { cancelDialog.show() }
            }

            val unarchiveButton = projectView.findViewById<ImageButton>(R.id.unarchive_image)
            unarchiveButton.setOnClickListener{
                val cancelDialog = AlertDialog.Builder(context)
                cancelDialog.setTitle(R.string.unarchive_project)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            proj.archived = false
                            archivedProjects = ArrayList((context as MainActivity).projectsList.filter{ proj -> proj.archived })
                            listViewProj.adapter = that.ProjectAdapter(context, archivedProjects)
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                try{
                    cancelDialog.create()
                }catch (e: Exception){} finally { cancelDialog.show() }
            }

            val constr = projectView.findViewById<ConstraintLayout>(R.id.content)
            constr.setOnClickListener {openProj(proj)}

            val enterText = projectView.findViewById<TextView>(R.id.project_text)
            enterText.setOnClickListener {openProj(proj)}

            viewHolder.msg!!.text = proj.toString()
            return projectView
        }

        private fun openProj(proj: Project){
            (context as MainActivity).actualProject = proj
            (context as MainActivity).openFragment(ProjectFragment())
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

        archivedProjects = ArrayList(context.projectsList.filter{ proj -> proj.archived })
        addProj = AlertDialog.Builder(context)

        listViewProj = context.findViewById(R.id.listProject)
        adapteur = that.ProjectAdapter(context, archivedProjects)
        listViewProj.adapter = adapteur

        context.setMenu("home")
        context.title = context.getString(R.string.archives)
    }

    override fun TAG(): String {
        return "===== ARCHIVEFRAGMENT ====="
    }
}