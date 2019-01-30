package library

class Rule {
    var num: Int = 0 // identifiant
    var start: Int = 0 //depart
    var steps: java.util.ArrayList<Step>

    constructor(start: Int, identifiant: Int){
        this.start = start
        this.num = identifiant
        this.steps = java.util.ArrayList<Step>()
    }

    fun clone(): Rule{
        val r = Rule(num, start)
        r.steps = steps.clone() as java.util.ArrayList<Step>
        return r
    }
}