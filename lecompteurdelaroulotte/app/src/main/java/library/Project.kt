package library

import android.content.Context
import lufra.lecompteurdelaroulotte.R
import java.util.*

class Project(var context: Context, var name: String) {
    var etat: Int = 0
    var bindCounters: ArrayList<Counter>? = null
    var notes: String = " "
    var myRules: ArrayList<Rule>
    var myCounters: ArrayList<Counter>
    var myComments: ArrayList<Comment>

    private var lesNums: ArrayList<Rappel>

    /**
     * Class to create a message when a rule is applied
     */
    inner class Rappel(n: Int, message: String, count: Counter?) {
        var c: Counter? = count
        var x: Int = n
        var s: String = message

        fun is_ok_main(): Boolean{
            return c == null && x == etat
        }

        fun is_ok_counter(c: Counter): Boolean{
            return c == this.c && c.etat == x
        }

        fun is_ok(): Boolean{
            return if(c == null) x == etat else x == c!!.etat
        }
    }

    init {
        this.notes = " "
        myCounters = ArrayList()
        myRules = ArrayList()
        myComments = ArrayList()
        lesNums = ArrayList()
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
     * Functions to manage the rules
     */
    fun addComment(c: Comment){
        myComments.add(c)
        constructRappel()// TODO verify this
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

    fun notify(b: Boolean){
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
        lesNums = ArrayList()
        for (r in myRules)
            addRuleInRappel(r)
    }

    fun addRuleInRappel(r: Rule){
        var x = r.start-r.steps[0].two
        for (elem in r.steps){
            val aug = if(elem.augm) R.string.augmentation else R.string.diminution
            for (i in 1..elem.one){
                x += elem.two
                val theCounter = myCounters.find { it.name == r.counter }
                var theMessage = context.getString(aug) + " " + context.getString(R.string.of) + " " + elem.three + " " + context.getString(R.string.stitches)
                if(r.comment != "" && r.counter != ""){
                    theMessage = r.counter + "; " + r.comment + ": " + theMessage
                }else if(r.comment != ""){
                    theMessage = r.comment + ": " + theMessage
                }else if(r.counter != ""){
                    theMessage = r.counter + ": " + theMessage
                }
                lesNums.add(Rappel(x,theMessage , theCounter))
            }
        }
    }

    fun getMessageAll(): String?{
        var s = ""
        val mainText = getMessageForMain()
        if(mainText != null)
            s += mainText
        myCounters.forEach {
            val counterText = getMessageForCounter(it)
            if(counterText != null)
                s += counterText
        }
        if(s == "")
            return null
        return s
    }

    fun getMessageForMain(): String?{
        var s = ""
        lesNums.forEach {
            if(it.is_ok_main())
                s += it.s + "\n"
        }
        if(s == "")
            return null
        return s
    }

    fun getMessageForCounter(c: Counter): String?{
        var s = ""
        lesNums.forEach {
            if(it. is_ok_counter(c))
                s += it.s + "\n"
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