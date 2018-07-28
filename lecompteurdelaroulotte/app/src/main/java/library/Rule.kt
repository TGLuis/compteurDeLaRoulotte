package library

class Rule {
    var augmentation: Boolean = true
    var first: Int = 0
    var second: Int = 0
    var third: Int = 0

    constructor(augm: Boolean, first: Int, second: Int, third: Int){
        this.augmentation = augm
        this.first = first
        this.second = second
        this.third = third
    }

    override fun toString(): String {
        var s = ""
        if (augmentation) s=s+"Augmentation: "
        else s=s+"Diminution: "
        s=s+"tous les $first rangs $second x $third mailles\n"
        return s
    }
}