package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R


class HelpFragment : MyFragment() {
    private lateinit var context: MainActivity
    override var TAG: String = "==== HELPFRAGMENT ===="

    private lateinit var imageButton_general: ImageButton
    private lateinit var textView_general: TextView
    private lateinit var imageButton_counter: ImageButton
    private lateinit var textView_counter: TextView
    private lateinit var imageButton_comment: ImageButton
    private lateinit var textView_comment: TextView
    private lateinit var imageButton_rule: ImageButton
    private lateinit var textView_rule: TextView

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
                        textView_general.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        textView_counter.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        textView_comment.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        textView_rule.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        height_general = textView_general.height
                        height_counter = textView_counter.height
                        height_comment = textView_comment.height
                        height_rule = textView_rule.height
                        textView_general.height = 0
                        textView_counter.height = 0
                        textView_comment.height = 0
                        textView_rule.height = 0
                        textView_general.text = ""
                        textView_counter.text = ""
                        textView_comment.text = ""
                        textView_rule.text = ""
                    }
                })

        textView_general = context.findViewById(R.id.help_general_text)
        textView_counter = context.findViewById(R.id.help_counter_text)
        textView_comment = context.findViewById(R.id.help_comment_text)
        textView_rule = context.findViewById(R.id.help_rule_text)

        imageButton_general = context.findViewById(R.id.help_general_drop)
        imageButton_general.setOnClickListener {
            if (gen_drop) {
                textView_general.text = ""
                textView_general.height = 0
                imageButton_general.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                textView_general.text = context.getString(R.string.help_general)
                textView_general.height = height_general
                imageButton_general.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            }
            gen_drop = !gen_drop
        }

        imageButton_counter = context.findViewById(R.id.help_counter_drop)
        imageButton_counter.setOnClickListener {
            if (con_drop) {
                textView_counter.height = 0
                textView_counter.text = ""
                imageButton_counter.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                textView_counter.text = context.getString(R.string.help_counter)
                textView_counter.height = height_counter
                imageButton_counter.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            }
            con_drop = !con_drop
        }

        imageButton_comment = context.findViewById(R.id.help_comment_drop)
        imageButton_comment.setOnClickListener {
            if (com_drop) {
                textView_comment.height = 0
                textView_comment.text = ""
                imageButton_comment.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                textView_comment.text = context.getString(R.string.help_comment)
                textView_comment.height = height_comment
                imageButton_comment.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            }
            com_drop = !com_drop
        }

        imageButton_rule = context.findViewById(R.id.help_rule_drop)
        imageButton_rule.setOnClickListener {
            if (rul_drop) {
                textView_rule.height = 0
                textView_rule.text = ""
                imageButton_rule.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                textView_rule.text = context.getString(R.string.help_rule)
                textView_rule.height = height_rule
                imageButton_rule.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            }
            rul_drop = !rul_drop
        }

        context.title = context.getString(R.string.HelpTitle)
    }
}