package library

class Comment(identifiant: Int,//depart
              var start: Int, var end: Int) {
    var num: Int = identifiant // identifiant
    var comment: String = ""
    var counter: String = ""

    fun clone(): Comment{
        val c = Comment(num, start, end)
        c.counter = counter
        c.comment = comment
        return c
    }
}