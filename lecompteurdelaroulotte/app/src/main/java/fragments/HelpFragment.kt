package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import library.Listeners
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
        var generalDrop = false
        var counterDrop = false
        var commentDrop = false
        var ruleDrop = false
        var heightGeneral = 0
        var heightCounter = 0
        var heightComment = 0
        var heightRule = 0

        setAllView()

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
                        heightGeneral = textView_general.height
                        heightCounter = textView_counter.height
                        heightComment = textView_comment.height
                        heightRule = textView_rule.height
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


        imageButton_general.setOnClickListener {
            generalDrop = Listeners.dropArrowButtonListener(context, generalDrop, textView_general, imageButton_general, R.string.help_general, heightGeneral)
        }

        imageButton_counter.setOnClickListener {
            counterDrop = Listeners.dropArrowButtonListener(context, counterDrop, textView_counter, imageButton_counter, R.string.help_counter, heightCounter)
        }

        imageButton_comment.setOnClickListener {
            commentDrop = Listeners.dropArrowButtonListener(context, commentDrop, textView_comment, imageButton_comment, R.string.help_comment, heightComment)
        }

        imageButton_rule.setOnClickListener {
            ruleDrop = Listeners.dropArrowButtonListener(context, ruleDrop, textView_rule, imageButton_rule, R.string.help_rule, heightRule)
        }

        context.title = context.getString(R.string.HelpTitle)
    }

    private fun setAllView() {
        textView_general = context.findViewById(R.id.help_general_text)
        textView_counter = context.findViewById(R.id.help_counter_text)
        textView_comment = context.findViewById(R.id.help_comment_text)
        textView_rule = context.findViewById(R.id.help_rule_text)
        imageButton_general = context.findViewById(R.id.help_general_drop)
        imageButton_counter = context.findViewById(R.id.help_counter_drop)
        imageButton_comment = context.findViewById(R.id.help_comment_drop)
        imageButton_rule = context.findViewById(R.id.help_rule_drop)
    }
}