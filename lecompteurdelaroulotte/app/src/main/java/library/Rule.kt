package library

class Rule {
    var num: Int // identifiant
    var start: Int //depart
    var steps: java.util.ArrayList<Step>
    var comment: String = ""
    var counter: String

    constructor(start: Int, identifiant: Int, counter: String){
        this.start = start
        this.num = identifiant
        this.counter = counter
        this.steps = java.util.ArrayList<Step>()
    }

    fun clone(): Rule{
        val r = Rule(num, start, counter)
        r.steps = steps.clone() as java.util.ArrayList<Step>
        return r
    }
}