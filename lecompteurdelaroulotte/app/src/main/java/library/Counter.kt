package library

class Counter {
    var etat: Int = 0
    var name: String
    var max: Int
    var order: Int
    var attachedMain: Boolean
    var counterAttached: Counter?

    constructor(name: String, max: Int, order: Int, attachedMain: Boolean, counterAttached: Counter?){
        this.name = name
        this.max = max
        this.order = order
        this.attachedMain = attachedMain
        this.counterAttached = counterAttached
    }

    fun update(b: Boolean){
        if(b){
            if(max != 0 && etat%max == max-1 && counterAttached != null)
                counterAttached!!.update(true)
            etat++
        }else{
            if(max != 0 && etat%max == 0 && counterAttached != null)
                counterAttached!!.update(false)
            etat--
        }
    }

    fun attach(c: Counter){this.counterAttached=c}
    fun detach(){this.counterAttached=null}
}