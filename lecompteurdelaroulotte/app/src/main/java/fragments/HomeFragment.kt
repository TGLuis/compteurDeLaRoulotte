package fragments

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.simple_text_input.view.*
import library.Project
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

class HomeFragment : MyFragment() {
    private lateinit var context: MainActivity

    private lateinit var listViewProj: ListView
    private lateinit var addProj: AlertDialog.Builder
    private lateinit var bouton: Button
    private lateinit var projects: ArrayList<Project>
    private lateinit var that: HomeFragment

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

            val cancelButton = projectView.findViewById<ImageButton>(R.id.archive_image)
            cancelButton.setOnClickListener {
                val cancelDialog = AlertDialog.Builder(context)
                cancelDialog.setTitle(R.string.archive_project)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            proj.archived = true
                            projects = ArrayList((context as MainActivity).projectsList.filter { proj -> !proj.archived })
                            listViewProj.adapter = that.ProjectAdapter(context, projects)
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                try {
                    cancelDialog.create()
                } catch (e: Exception) {
                } finally {
                    cancelDialog.show()
                }

            }

            val constr = projectView.findViewById<ConstraintLayout>(R.id.content)
            constr.setOnClickListener { openProj(proj) }

            val enterText = projectView.findViewById<TextView>(R.id.project_text)
            enterText.setOnClickListener { openProj(proj) }

            viewHolder.msg!!.text = proj.toString()
            return projectView
        }

        private fun openProj(proj: Project) {
            (context as MainActivity).actualProject = proj
            (context as MainActivity).openFragment(ProjectFragment())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        projects = ArrayList(context.projectsList.filter { proj -> !proj.archived })
        addProj = AlertDialog.Builder(context)
        that = this

        listViewProj = context.findViewById(R.id.listProject)
        listViewProj.adapter = that.ProjectAdapter(context, projects)

        bouton = context.findViewById(R.id.button_add_project)
        bouton.setOnClickListener {
            val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, view as ViewGroup, false)
            viewInflated.input_text.hint = context.getString(R.string.project_name)
            addProj.setView(viewInflated)
                    .setTitle(R.string.project_name_id)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        val projectName = viewInflated.input_text.text.toString()
                        if (context.projectsList.find { it.name == projectName } == null) {
                            context.createProject(projectName)
                            projects = ArrayList(context.projectsList.filter { proj -> !proj.archived })
                            listViewProj.adapter = that.ProjectAdapter(context, projects)
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
        }

        context.setMenu("home")
        context.title = context.getString(R.string.app_name)
    }

    override fun TAG(): String {
        return "===== MAINFRAGMENT ====="
    }
}