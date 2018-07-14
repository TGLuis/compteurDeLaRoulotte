package library

class Project {
    private var blocnote: String = ""
    private var name: String = ""
    private lateinit var myCounter: MCounter
    private lateinit var myCounters: ArrayList<Counter>

    constructor(name: String){
        this.name = name
        this.blocnote = ""
        this.myCounter = MCounter()
        myCounters = ArrayList<Counter>()
    }

    fun getMCounter(): MCounter{return myCounter}

    fun getCounters(): List<Counter>{return myCounters}

    fun addCounter(c: Counter){myCounters.add(c)}

    fun deleteCounter(c: Counter){myCounters.remove(c)}

    fun getNotes(): String{return blocnote}

    fun setNotes(s: String){this.blocnote=s}

    override fun toString(): String {return this.name}
}