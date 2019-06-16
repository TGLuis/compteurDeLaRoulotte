package fragments

import android.support.constraint.ConstraintLayout
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.TextView
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R



class HelpFragment: Fragment() {
    private val TAG = "==== HELPFRAGMENT ===="
    private lateinit var context: MainActivity

    private lateinit var IB_general: ImageButton
    private lateinit var TV_general: TextView
    private lateinit var IB_counter: ImageButton
    private lateinit var TV_counter: TextView
    private lateinit var IB_comment: ImageButton
    private lateinit var TV_comment: TextView
    private lateinit var IB_rule: ImageButton
    private lateinit var TV_rule: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var gen_drop = false
        var con_drop = false
        var com_drop = false
        var rul_drop = false
        var height_general = 0
        var height_counter = 0
        var height_comment = 0
        var height_rule = 0

        /**
         * We have to put a listerner on the layout to wait the rendering of the screen to get the
         * height of each element
         */
        context.findViewById<ConstraintLayout>(R.id.help_general)
                .viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                TV_general.viewTreeObserver.removeOnGlobalLayoutListener(this)
                TV_counter.viewTreeObserver.removeOnGlobalLayoutListener(this)
                TV_comment.viewTreeObserver.removeOnGlobalLayoutListener(this)
                TV_rule.viewTreeObserver.removeOnGlobalLayoutListener(this)
                height_general = TV_general.height
                height_counter = TV_counter.height
                height_comment = TV_comment.height
                height_rule = TV_rule.height
                TV_general.height = 0
                TV_counter.height = 0
                TV_comment.height = 0
                TV_rule.height = 0
                TV_general.text = ""
                TV_counter.text = ""
                TV_comment.text = ""
                TV_rule.text = ""
            }
        })

        TV_general = context.findViewById(R.id.help_general_text)
        TV_counter = context.findViewById(R.id.help_counter_text)
        TV_comment = context.findViewById(R.id.help_comment_text)
        TV_rule = context.findViewById(R.id.help_rule_text)

        IB_general = context.findViewById(R.id.help_general_drop)
        IB_general.setOnClickListener {
            Log.e(TAG, "\nlayout height = ${TV_general.layoutParams.height}")
            Log.e(TAG, "height = ${TV_general.height}")
            if(gen_drop){
                TV_general.text = ""
                TV_general.height = 0
                IB_general.setImageResource(R.drawable.drop_down)
            } else {
                TV_general.text = context.getString(R.string.help_general)
                TV_general.height = height_general
                IB_general.setImageResource(R.drawable.drop_up)
            }
            gen_drop = !gen_drop
        }

        IB_counter = context.findViewById(R.id.help_counter_drop)
        IB_counter.setOnClickListener {
            if(con_drop){
                TV_counter.height = 0
                TV_counter.text = ""
                IB_counter.setImageResource(R.drawable.drop_down)
            } else {
                TV_counter.text = context.getString(R.string.help_counter)
                TV_counter.height = height_counter
                IB_counter.setImageResource(R.drawable.drop_up)
            }
            con_drop = !con_drop
        }

        IB_comment = context.findViewById(R.id.help_comment_drop)
        IB_comment.setOnClickListener {
            if(com_drop){
                TV_comment.height = 0
                TV_comment.text = ""
                IB_comment.setImageResource(R.drawable.drop_down)
            } else {
                TV_comment.text = context.getString(R.string.help_comment)
                TV_comment.height = height_comment
                IB_comment.setImageResource(R.drawable.drop_up)
            }
            com_drop = !com_drop
        }

        IB_rule    = context.findViewById(R.id.help_rule_drop)
        IB_rule.setOnClickListener {
            if(rul_drop){
                TV_rule.height = 0
                TV_rule.text = ""
                IB_rule.setImageResource(R.drawable.drop_down)
            } else {
                TV_rule.text = context.getString(R.string.help_rule)
                TV_rule.height = height_rule
                IB_rule.setImageResource(R.drawable.drop_up)
            }
            rul_drop = !rul_drop
        }

        context.title = context.getString(R.string.HelpTitle)
    }
}