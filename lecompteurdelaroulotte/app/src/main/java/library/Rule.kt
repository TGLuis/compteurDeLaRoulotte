package library

class Rule(var num: Int, var start: Int) {
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