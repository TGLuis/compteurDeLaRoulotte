package library

import tgl.lecompteurdelaroulotte.MainActivity
import tgl.lecompteurdelaroulotte.R

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

    fun getString(context: MainActivity): String {
        var s = context.getString(R.string.rule)
        s += ": $comment\n"
        s += if (counter == "") context.getString(R.string.on_main) else context.getString(R.string.on_counter) + " " + counter
        s += "\n" + context.getString(R.string.from_row) + " " + start.toString() + "\n"
        for (i in 0 until steps.size) {
            if (i != 0) {
                s += context.getString(R.string.andthen) + " "
            }
            s += if (steps[i].augm) context.getString(R.string.augmentation)
            else context.getString(R.string.diminution)
            s += " " + steps[i].one.toString() + " " + context.getString(R.string.rule_text1) + " "
            s += steps[i].two.toString() + " " + context.getString(R.string.rows) + " "
            s += steps[i].three.toString() + " " + context.getString(R.string.stitch) + "\n"
        }
        return s
    }
}