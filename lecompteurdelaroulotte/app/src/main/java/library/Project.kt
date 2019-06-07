package library

import android.content.Context
import android.util.Log
import lufra.lecompteurdelaroulotte.R
import java.util.*

class Project(var context: Context, var name: String) {
    var etat: Int = 0
    private var bindCounters: ArrayList<Counter>? = null
    var notes: String = " "
    var myRules: ArrayList<Rule>
    var myCounters: ArrayList<Counter>
    var myComments: ArrayList<Comment>
    private var lesRappels: ArrayList<Rappel>

    /**
     * Class to create a message when a rule is applied
     */
    inner class Rappel(n: Int, message: String, count: Counter?) {
        private var c: Counter? = count
        private var x: Int = n
        var s: String = message

        fun is_ok_main(): Boolean{
            return c == null && x == etat
        }

        fun is_ok_counter(c: Counter): Boolean{
            return c == this.c && c.etat == x
        }

        override fun toString(): String{
            var s = ""
            if(c == null)
                s += " ON main counter "
            else
                s += " ON counter="+ c!!.name
            s += " ROW number " + x.toString() + " MESSAGE: " + this.s
            return s
        }
    }

    init {
        this.notes = " "
        myCounters = ArrayList()
        myRules = ArrayList()
        myComments = ArrayList()
        lesRappels = ArrayList()
    }

    /***********************************************************************************************
     * Functions to manage the counters
     */
    fun has_counter(s: String): Boolean{
        myCounters.forEach {
            if(s == it.name)
                return true
        }
        return false
    }

    fun getCounters(): ArrayList<Counter>{return myCounters}

    fun getCounter(s: String): Counter?{
        myCounters.forEach {
            if(it.name == s)
                return it
        }
        return null
    }

    fun addCounter(c: Counter){
        if (c !in myCounters)
            myCounters.add(c)
    }

    fun deleteCounter(c: Counter){
        myCounters.remove(c)
        myCounters.forEach{
            if (it.counterAttached != null && it.counterAttached!!.name == c.name)
                it.detach()
        }
        if(bindCounters != null)
            bindCounters!!.remove(c)
    }

    /***********************************************************************************************
     * Functions to manage the rules
     */
    fun addRule(r: Rule){
        myRules.add(r)
        addRuleInRappel(r)
    }

    fun deleteRule(r: Rule){
        myRules.remove(r)
        constructRappel()
    }

    fun deleteStepOfRule(r: Rule, s: Step){
        r.steps.remove(s)
        constructRappel()
    }

    /***********************************************************************************************
     * Functions to manage the comments
     */
    fun addComment(c: Comment){
        myComments.add(c)
        addCommentInRappel(c)
    }

    fun deleteComment(c: Comment){
        myComments.remove(c)
        constructRappel()
    }

    /***********************************************************************************************
     * Functions to update manage the project
     */
    fun update(b: Boolean){
        if(b){etat++}else{etat--}
        notify(b)
    }

    fun attach(c: Counter){
        if (bindCounters == null)
            bindCounters = ArrayList()
        bindCounters!!.add(c)
    }

    fun detach(c: Counter){
        if (bindCounters != null)
            bindCounters!!.remove(c)
    }

    private fun notify(b: Boolean){
        if(bindCounters != null){
            bindCounters!!.forEach{
                it.update(b)
            }
        }
    }

    /***********************************************************************************************
     * Functions to manage the Rappel, the messages which appears when a rule aply
     */
    fun constructRappel(){
        lesRappels = ArrayList()
        for (r in myRules)
            addRuleInRappel(r)
        for (c in myComments)
            addCommentInRappel(c)
    }

    private fun addCommentInRappel(c: Comment){
        val theCounter = myCounters.find { it.name == c.counter }
        for (i in c.start..c.end) {
            lesRappels.add(Rappel(i,c.comment,theCounter))
        }
    }

    private fun addRuleInRappel(r: Rule){
        var x = r.start-r.steps[0].two
        val theCounter = myCounters.find { it.name == r.counter }
        for (elem in r.steps){
            val aug = if(elem.augm) R.string.augmentation else R.string.diminution
            for (i in 1..elem.one){
                x += elem.two
                var theMessage = context.getString(aug) + " " + context.getString(R.string.of) + " " + elem.three + " " + context.getString(R.string.stitches)
                if(r.comment != ""){
                    theMessage = r.comment + ": " + theMessage
                }
                lesRappels.add(Rappel(x, theMessage, theCounter))
            }
        }
    }

    fun getMessageAll(): String?{
        var s = ""
        val mainText = getMessageForMain()
        if(mainText != null)
            s += mainText
        myCounters.forEach {
            val counterText = getMessageForCounter(it, true)
            if(counterText != null)
                s += counterText
        }
        if(s == "")
            return null
        return s
    }

    fun getMessageForMain(): String?{
        var s = ""
        lesRappels.forEach {
            if(it.is_ok_main())
                s += it.s + "\n"
        }
        if(s == "")
            return null
        return s
    }

    fun getMessageForCounter(c: Counter, withName: Boolean): String?{
        var s = ""
        lesRappels.forEach {
            if(it.is_ok_counter(c)) {
                if (withName)
                    s += c.name + ": "
                s += it.s + "\n"
            }
        }
        if(s == "")
            return null
        return s
    }

    /***********************************************************************************************
     * Other functions
     */
    override fun toString(): String {return this.name}
}