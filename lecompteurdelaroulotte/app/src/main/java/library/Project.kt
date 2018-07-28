package library

class Project {
    private var blocnote: String = ""
    private var name: String = ""
    private lateinit var myCounter: MCounter
    private lateinit var myCounters: ArrayList<Counter>
    private lateinit var myRules: ArrayList<Rule>

    constructor(name: String){
        this.name = name
        this.blocnote = ""
        this.myCounter = MCounter()
        myCounters = ArrayList<Counter>()
        myRules = ArrayList<Rule>()
    }

    fun getMCounter(): MCounter{return myCounter}

    fun getCounters(): ArrayList<Counter>{return myCounters}

    fun addCounter(c: Counter){myCounters.add(c)}

    fun deleteCounter(c: Counter){myCounters.remove(c)}

    fun getNotes(): String{return blocnote}

    fun addRule(r: Rule){myRules.add(r)}

    fun deleteRule(r: Rule){myRules.remove(r)}//je pense qu'il y a un probleme vu qu'on sais pas identifier les rules...

    fun setNotes(s: String){this.blocnote=s}

    override fun toString(): String {return this.name}
}