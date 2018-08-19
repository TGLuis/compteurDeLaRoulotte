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

    fun addStep(times: Int, rangs: Int, mailles: Int){
        steps.add(Step(times, rangs, mailles))
    }

    override fun toString(): String {
        var s = ""
        if (augmentation) s=s+ "Augmentation" //TODO: get Ressource file here... difficult
        else s=s+ "Diminution"
        s+=": \n"
        for(i in 1..steps.size){
            if(i != 1){
                s += "puis "
            }
            s += "${steps[i].one} fois tous les ${steps[i].two} rangs ${steps[i].three} mailles\n"
        }

        return s
    }
}