package fragments

import android.os.Bundle
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import android.widget.*
import kotlinx.android.synthetic.main.text_input_project.view.*
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import library.MyDatabase
import library.Project

class HomeFragment: Fragment() {
    private val TAG = "===== MAINFRAGMENT ====="
    private lateinit var context: MainActivity

    private lateinit var listViewProj: ListView
    private lateinit var addProj: AlertDialog.Builder
    private lateinit var bouton: Button
    private lateinit var projects: ArrayList<Project>

    inner class ProjectAdapter(context: Context, list: ArrayList<Project>) : ArrayAdapter<Project>(context, 0, list) {
        private inner class ProjectViewHolder(var msg: TextView?= null)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val proj = super.getItem(position)
            val projectView: View
            val viewHolder: ProjectViewHolder
            if (convertView == null){
                viewHolder = ProjectViewHolder()
                projectView = LayoutInflater.from(context).inflate(R.layout.list_project_item, parent, false)
                viewHolder.msg = projectView.findViewById(R.id.project_text)
                projectView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ProjectViewHolder
                projectView = convertView
            }
            viewHolder.msg!!.text = proj.toString()
            return projectView
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity

        return inflater.inflate(R.layout.fragment_acceuil, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        projects = context.projectsList
        addProj = AlertDialog.Builder(context)

        listViewProj = context.findViewById(R.id.listProject)
        val adapteur = this.ProjectAdapter(context, projects)
        listViewProj.adapter = adapteur
        listViewProj.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            context.actualProject = projects[position]
            context.openFragment(ProjectFragment() as Fragment)
        }

        bouton = context.findViewById(R.id.button_add_project)
        bouton.setOnClickListener({
            val viewInflated = LayoutInflater.from(context).inflate(R.layout.text_input_project, view as ViewGroup, false)
            addProj.setView(viewInflated)
                    .setTitle(R.string.project_name_id)
                    .setPositiveButton(R.string.ok, { dialog, _ ->
                        val projectName = viewInflated.input.text.toString()
                        //Toast.makeText(context,"something", Toast.LENGTH_SHORT).show()
                        context.createProject(projectName)
                        adapteur.notifyDataSetChanged()
                        dialog.dismiss()
                    })
                    .setNegativeButton(R.string.cancel, { dialog, _ ->
                        dialog.dismiss()
                    })
                    .create()
                    .show()
        })
    }
}