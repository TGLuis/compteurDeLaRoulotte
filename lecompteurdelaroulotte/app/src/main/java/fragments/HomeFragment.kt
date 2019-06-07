package fragments

import android.os.Bundle
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import android.widget.*
import kotlinx.android.synthetic.main.simple_text_input.view.*
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import library.Project
import java.lang.Exception

class HomeFragment: Fragment() {
    private val TAG = "===== MAINFRAGMENT ====="
    private lateinit var context: MainActivity
    lateinit var adapteur:ProjectAdapter

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

            val cancelButton = projectView.findViewById<ImageButton>(R.id.delete_image)
            cancelButton.setOnClickListener{
                val cancelDialog = AlertDialog.Builder(context)
                cancelDialog.setTitle(R.string.delete_project)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            (context as MainActivity).deleteProject(proj)
                            adapteur.notifyDataSetChanged()
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
            (context as MainActivity).frags.push(HomeFragment())
            (context as MainActivity).openFragment(ProjectFragment())
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
        adapteur = this.ProjectAdapter(context, projects)
        listViewProj.adapter = adapteur

        bouton = context.findViewById(R.id.button_add_project)
        bouton.setOnClickListener{
            val viewInflated = LayoutInflater.from(context).inflate(R.layout.simple_text_input, view as ViewGroup, false)
            viewInflated.input_text.hint = context.getString(R.string.project_name)
            addProj.setView(viewInflated)
                    .setTitle(R.string.project_name_id)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        val projectName = viewInflated.input_text.text.toString()
                        if(context.projectsList.find { it.name == projectName } == null){
                            context.createProject(projectName)
                            adapteur.notifyDataSetChanged()
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
        }

        context.actualFragment = HomeFragment()
        context.frags.clear()
        context.frags.push(context.actualFragment)
        context.setMenu("home")
        context.title = context.getString(R.string.app_name)
    }
}