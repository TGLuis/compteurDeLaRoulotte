package library

class Counter {
    var etat: Int = 0
    var name: String = ""
    var tours: Int = 0
    var max: Int = 0
    var attachedMain: Boolean = false
    var counterAttached: Counter? = null

    constructor(name: String, max: Int, attachedMain: Boolean, counterAttached: Counter?){
        this.name = name
        this.max = max
        this.attachedMain = attachedMain
        this.counterAttached = counterAttached
    }

    fun update(b: Boolean){
        if(b){
            if(max != -1 && max == etat){
                tours++
                if(counterAttached != null)
                    counterAttached!!.update(true)
                etat=1
            }else{
                etat++
            }
        }else{
            if(max != -1 && tours > 0 && etat == 0){
                tours--
                if(counterAttached != null)
                    counterAttached!!.update(false)
                etat=max
            }else if(etat != 0){
                etat--
            }
        }
    }

    fun attach(c: Counter){this.counterAttached=c}
    fun detach(){this.counterAttached=null}


}