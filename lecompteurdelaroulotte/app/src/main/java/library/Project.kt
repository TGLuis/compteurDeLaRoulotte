package library

class Project {
    var etat: Int = 0
    var bindCounters: ArrayList<Counter>? = null
    var notes: String = " "
    private var name: String = ""
    private lateinit var myCounters: ArrayList<Counter>
    private lateinit var myRules: ArrayList<Rule>

    constructor(name: String){
        this.name = name
        this.notes = " "
        myCounters = ArrayList<Counter>()
        myRules = ArrayList<Rule>()
    }

    fun getCounters(): ArrayList<Counter>{return myCounters}

    fun addCounter(c: Counter){myCounters.add(c)}

    fun deleteCounter(c: Counter){myCounters.remove(c)}

    fun addRule(r: Rule){myRules.add(r)}

    fun deleteRule(r: Rule){myRules.remove(r)}//je pense qu'il y a un probleme vu qu'on sais pas identifier les rules...

    fun update(b: Boolean){
        if(b){etat++}else{etat--}
        notify(b)
    }

    fun attach(c: Counter){
        if (bindCounters == null){
            bindCounters = ArrayList<Counter>()
        }
        bindCounters!!.plus(c)
    }
    fun detach(c: Counter){
        if (bindCounters != null)
            bindCounters!!.minus(c)
    }

    fun notify(b: Boolean){
        if(bindCounters != null){
            bindCounters!!.forEach{
                it.update(b)
            }
        }
    }

    override fun toString(): String {return this.name}
}