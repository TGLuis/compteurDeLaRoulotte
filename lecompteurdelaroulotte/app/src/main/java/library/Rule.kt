package library

class Rule {
    var augmentation: Boolean = true
    var elements: Array<Array<Integer>>? = null

    constructor(augm: Boolean, elem: Array<Array<Integer>>){
        this.augmentation = augm
        this.elements = elem
    }

    override fun toString(): String {
        var s:String = ""
        if (augmentation) s=s+"Augmentation:\n"
        else s=s+"Diminution:\n"
        elements!!.forEach{
            s=s+"tous les $it[0] rangs $it[1] x $it[2] mailles\n"
        }
        return s
    }
}