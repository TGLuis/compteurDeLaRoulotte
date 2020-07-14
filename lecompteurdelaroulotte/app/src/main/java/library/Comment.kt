package library

import lufra.lecompteurdelaroulotte.R
import lufra.lecompteurdelaroulotte.MainActivity

class Comment(identifiant: Int, var start: Int, var end: Int) {
    var num: Int = identifiant
    var comment: String = ""
    var counter: String = ""

    fun clone(): Comment{
        val c = Comment(num, start, end)
        c.counter = counter
        c.comment = comment
        return c
    }

    fun getString(context: MainActivity): String{
        var s = context.getString(R.string.comment)
        s += ":\n"
        s += if(counter == "") context.getString(R.string.on_main) else context.getString(R.string.on_counter) + " " + counter
        s += "\n" + context.getString(R.string.from_row) + " " + start.toString() + " "
        s += context.getString(R.string.to_row) + " " + end.toString() + "\n"
        s += comment
        return s
    }
}