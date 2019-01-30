package library

class MCounter {
    var etat: Int = 0
    var rules: ArrayList<Rule>? = null
    var counters: ArrayList<Counter>? = null

    constructor()

    fun update(b: Boolean){
        if(b){etat++}else{etat--}
        notify(b)
    }

    fun attach(c: Counter){
        if (counters == null){
            counters = ArrayList<Counter>()
        }
        counters!!.plus(c)
    }
    fun detach(c: Counter){
        if (counters != null)
            counters!!.minus(c)
    }

    fun addRule(r: Rule){
        if (rules == null){
            rules = ArrayList<Rule>()
        }
        rules!!.plus(r)
    }
    fun deleteRule(r: Rule){
        if (rules != null)
            rules!!.minus(r)

    }

    fun notify(b: Boolean){
        if(counters != null){
            counters!!.forEach{
                it.update(b)
            }
        }
    }
}