package library

class Step {
    var augm: Boolean
    var one: Int
    var two: Int
    var three: Int

    constructor(augm: Boolean, times: Int, rangs: Int, mailles: Int){
        this.augm = augm
        one = times
        two = rangs
        three = mailles
    }
}