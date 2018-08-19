package library

class Project {
    var etat: Int = 0
    var bindCounters: ArrayList<Counter>? = null
    var notes: String = " "
    private var name: String = ""
    lateinit var myRules: ArrayList<Rule>
    private lateinit var myCounters: ArrayList<Counter>

    private lateinit var lesNums: ArrayList<Rappel>

    inner class Rappel{
        var x: Int
        var s: String

        constructor(n: Int, message: String){
            x = n
            s = message
        }
    }

    constructor(name: String){
        this.name = name
        this.notes = " "
        myCounters = ArrayList<Counter>()
        myRules = ArrayList<Rule>()
        lesNums = ArrayList<Rappel>()
    }

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

    fun addCounter(c: Counter){myCounters.add(c)}

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

    fun addRule(r: Rule){
        myRules.add(r)
        addRuleInRappel(r)
    }

    fun deleteRule(r: Rule){
        myRules.remove(r)
        constructRappel()
    }

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

    fun constructRappel(){
        for (r in myRules){
            addRuleInRappel(r)
        }
    }

    fun addRuleInRappel(r: Rule){
        var x = r.start
        val aug = if(r.augmentation) "Augmentation" else "Diminution" //TODO: utiliser une ressource string
        for (elem in r.steps){
            for (i in 1..elem.one){
                x += elem.two
                lesNums.add(Rappel(x,"%s de %d mailles".format(aug,elem.three)))//TODO: utiliser une ressource string à voir...
            }
        }
    }

    fun getMessageRule(): String?{
        var s = ""
        lesNums.forEach {
            if( it.x == etat){
                s += it.s + "\n"
            }
        }
        if(s == ""){
            return null
        }
        return s
    }

    override fun toString(): String {return this.name}
}