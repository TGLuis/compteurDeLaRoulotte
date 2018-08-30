package library

class Rule {
    var num: Int = 0 // identifiant
    var start: Int = 0 //depart
    var steps: ArrayList<Step>

    constructor(start: Int, identifiant: Int){
        this.start = start
        this.num = identifiant
        this.steps = ArrayList<Step>()
    }
}