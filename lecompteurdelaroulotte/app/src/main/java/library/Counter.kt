package library

class Counter(var name: String, var max: Int, var order: Int, var attachedMain: Boolean, var counterAttached: Counter?) {
    var etat: Int = 0

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

    fun realState(): Int {
        if (this.max == 0) return this.etat
        if (this.etat != 0 && this.etat % this.max == 0) return this.max
        return this.etat % this.max
    }

    fun toDisplay(): String {
        return this.realState().toString()
    }

    fun attach(c: Counter){this.counterAttached=c}
    fun detach(){this.counterAttached=null}

    fun clone(data: Boolean): Counter {
        val c = Counter(this.name, this.max, this.order, this.attachedMain, this.counterAttached)// todo: risque de problème avec counterAttached :'(
        if(data){
            c.etat = this.etat
        }
        return c
    }
}