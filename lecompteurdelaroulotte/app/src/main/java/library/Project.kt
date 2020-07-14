package library

import android.net.Uri
import lufra.lecompteurdelaroulotte.MainActivity
import lufra.lecompteurdelaroulotte.R
import java.util.*

class Project(var context: MainActivity, var name: String) {
    val TAG = "=== PROJECT ==="
    var etat: Int = 0
    private var bindCounters: ArrayList<Counter>? = null
    var archived: Boolean = false
    var notes: String = " "
    var myRules: ArrayList<Rule>
    var myCounters: ArrayList<Counter>
    var myComments: ArrayList<Comment>
    var pdf: Uri? = null
    private var lesRappels: ArrayList<Rappel>

    /**
     * Class to create a message when a rule is applied
     */
    inner class Rappel(n: Int, message: String, count: Counter?) {
        private var c: Counter? = count
        private var x: Int = n
        var s: String = message

        fun isOkMain(): Boolean {
            return c == null && x == etat
        }

        fun isOkCounter(c: Counter): Boolean {
            if (c != this.c)
                return false
            return c.realState() == x
        }

        override fun toString(): String {
            var s = ""
            if (c == null)
                s += " " + context.getString(R.string.on_main) + " "
            else
                s += " " + context.getString(R.string.on_counter) + " = " + c!!.name
            s += " " + context.getString(R.string.row_number) + "  " + x.toString() + " " + context.getString(R.string.message) + " " + this.s
            return s
        }
    }

    init {
        notes = " "
        myCounters = ArrayList()
        myRules = ArrayList()
        myComments = ArrayList()
        lesRappels = ArrayList()
        pdf = null
    }

    fun setPdfFromString(s: String) {
        pdf = Uri.parse(s)
    }

    /***********************************************************************************************
     * Functions to manage the counters
     */
    fun hasCounter(s: String): Boolean {
        myCounters.forEach {
            if (s == it.name)
                return true
        }
        return false
    }

    fun getCounters(): ArrayList<Counter> {
        return myCounters
    }

    fun getCounter(s: String): Counter? {
        myCounters.forEach {
            if (it.name == s)
                return it
        }
        return null
    }

    fun addCounter(c: Counter) {
        if (c !in myCounters)
            myCounters.add(c)
    }

    fun deleteCounter(c: Counter) {
        myCounters.remove(c)
        myCounters.forEach {
            if (it.counterAttached != null && it.counterAttached!!.name == c.name)
                it.detach()
        }
        if (bindCounters != null && c in bindCounters!!)
            bindCounters!!.remove(c)
    }

    /***********************************************************************************************
     * Functions to manage the rules
     */
    fun addRule(r: Rule) {
        if (r !in myRules) {
            myRules.add(r)
            addRuleInRappel(r)
        }
    }

    fun deleteRule(r: Rule) {
        if (r in myRules) {
            myRules.remove(r)
            constructRappel()
        }
    }

    fun deleteStepOfRule(r: Rule, s: Step) {
        if (r in myRules) {
            r.steps.remove(s)
            constructRappel()
        }
    }

    /***********************************************************************************************
     * Functions to manage the comments
     */
    fun addComment(c: Comment) {
        if (c !in myComments) {
            myComments.add(c)
            addCommentInRappel(c)
        }
    }

    fun deleteComment(c: Comment) {
        if (c in myComments) {
            myComments.remove(c)
            constructRappel()
        }
    }

    /***********************************************************************************************
     * Functions to update manage the project
     */
    fun update(b: Boolean) {
        if (b) {
            etat++
        } else {
            etat--
        }
        notify(b)
    }

    fun attach(c: Counter) {
        if (bindCounters == null)
            bindCounters = ArrayList()
        if (c !in bindCounters!!)
            bindCounters!!.add(c)
    }

    fun detach(c: Counter) {
        if (bindCounters != null && c in bindCounters!!)
            bindCounters!!.remove(c)
    }

    private fun notify(b: Boolean) {
        if (bindCounters != null) {
            bindCounters!!.forEach {
                it.update(b)
            }
        }
    }

    /***********************************************************************************************
     * Functions to manage the Rappel, the messages which appears when a rule aply
     */
    fun constructRappel() {
        lesRappels = ArrayList()
        for (r in myRules)
            addRuleInRappel(r)
        for (c in myComments)
            addCommentInRappel(c)
    }

    private fun addCommentInRappel(c: Comment) {
        val theCounter = myCounters.find { it.name == c.counter }
        for (i in c.start..c.end) {
            lesRappels.add(Rappel(i, c.comment, theCounter))
        }
    }

    private fun addRuleInRappel(r: Rule) {
        var x = r.start - r.steps[0].two
        val theCounter = myCounters.find { it.name == r.counter }
        for (elem in r.steps) {
            val aug = if (elem.augm) R.string.augmentation else R.string.diminution
            for (i in 1..elem.one) {
                x += elem.two
                var theMessage = context.getString(aug) + " " + context.getString(R.string.of) + " " + elem.three + " " + context.getString(R.string.stitches)
                if (r.comment != "") {
                    theMessage = r.comment + ": " + theMessage
                }
                lesRappels.add(Rappel(x, theMessage, theCounter))
            }
        }
    }

    fun getMessageAll(): String? {
        var s = ""
        val mainText = getMessageForMain()
        if (mainText != null)
            s += mainText
        myCounters.forEach {
            val counterText = getMessageForCounter(it, true)
            if (counterText != null)
                s += counterText
        }
        if (s == "")
            return null
        return s
    }

    fun getMessageForMain(): String? {
        var s = ""
        lesRappels.forEach {
            if (it.isOkMain())
                s += it.s + "\n"
        }
        if (s == "")
            return null
        return s
    }

    fun getMessageForCounter(c: Counter, withName: Boolean): String? {
        var s = ""
        lesRappels.forEach {
            if (it.isOkCounter(c)) {
                if (withName)
                    s += c.name + ": "
                s += it.s + "\n"
            }
        }
        if (s == "")
            return null
        return s
    }

    /***********************************************************************************************
     * Other functions
     */
    fun clone(newName: String, data: Boolean) {
        val p = context.createProject(newName)
        p.notes = this.notes
        for (c in this.myCounters) {
            val newC = c.clone(data)
            p.addCounter(newC)
            if (this.bindCounters != null && c in this.bindCounters!!)
                p.attach(newC)
        }
        for (c in this.myComments)
            p.addComment(c.clone())
        for (r in this.myRules)
            p.addRule(r.clone())
        if (data) {
            p.etat = this.etat
        }
    }

    override fun toString(): String {
        return this.name
    }
}