package library

class Rule {
    var augmentation: Boolean = true
    var num: Int = 0 // identifiant
    var start: Int = 0 //depart
    var steps: ArrayList<Step>

    constructor(augm: Boolean, start: Int, identifiant: Int){
        this.augmentation = augm
        this.start = start
        this.num = identifiant
        this.steps = ArrayList<Step>()
    }
}