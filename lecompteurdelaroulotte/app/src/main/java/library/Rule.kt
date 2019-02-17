package library

class Rule(//depart
        var start: Int, identifiant: Int) {
    var num: Int = identifiant // identifiant
    var steps: java.util.ArrayList<Step> = java.util.ArrayList()
    var comment: String = ""
    var counter: String = ""

    fun clone(): Rule{
        val r = Rule(num, start)
        r.counter = counter
        r.steps = steps.clone() as java.util.ArrayList<Step>
        return r
    }
}