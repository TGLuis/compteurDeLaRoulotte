package library

import lufra.lecompteurdelaroulotte.R
import java.util.*

class Project {
    var etat: Int = 0
    var bindCounters: ArrayList<Counter>? = null
    var notes: String = " "
    var name: String = ""
    var myRules: ArrayList<Rule>
    var myCounters: ArrayList<Counter>

    private var lesNums: ArrayList<Rappel>

    /**
     * Class to create a message when a rule is applied
     */
    inner class Rappel{
        var c: Counter?
        var x: Int
        var s: String

        constructor(n: Int, message: String, count: Counter?){
            x = n
            s = message
            c = count
        }

        fun is_ok(): Boolean{
            return if(c == null) x == etat else x == c!!.etat
        }
    }

    constructor(name: String){
        this.name = name
        this.notes = " "
        myCounters = ArrayList<Counter>()
        myRules = ArrayList<Rule>()
        lesNums = ArrayList<Rappel>()
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
            if(it.name == s){
                return it
            }
        }
        return null
    }

    fun addCounter(c: Counter){
        if (c !in myCounters){
            myCounters.add(c)
        }
    }

    fun deleteCounter(c: Counter){
        myCounters.remove(c)
        myCounters.forEach{
            if (it.counterAttached != null && it.counterAttached!!.name == c.name){
                it.detach()
            }
        }
        if(bindCounters != null){
            bindCounters!!.remove(c)
        }
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
     * Functions to update manage the project
     */
    fun update(b: Boolean){
        if(b){etat++}else{etat--}
        notify(b)
    }

    fun attach(c: Counter){
        if (bindCounters == null){
            bindCounters = ArrayList<Counter>()
        }
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
     * Functions to manage the Rappel, the messages which appears when a rule aplly
     */
    fun constructRappel(){
        lesNums = ArrayList<Rappel>()
        for (r in myRules){
            addRuleInRappel(r)
        }
    }

    fun addRuleInRappel(r: Rule){
        var x = r.start-r.steps[0].two
        for (elem in r.steps){
            val aug = if(elem.augm) R.string.augmentation else R.string.diminution
            for (i in 1..elem.one){
                x += elem.two
                val the_counter = myCounters.find { it.name == r.counter }
                lesNums.add(Rappel(x,aug.toString() + " " + R.string.of + " " + elem.three + " " + R.string.stitches, the_counter))
            }
        }
    }

    fun getMessageRule(): String?{
        var s = ""
        lesNums.forEach {
            if( it.is_ok()){
                s += it.s + "\n"
            }
        }
        if(s == ""){
            return null
        }
        return s
    }

    /***********************************************************************************************
     * Other functions
     */
    override fun toString(): String {return this.name}
}