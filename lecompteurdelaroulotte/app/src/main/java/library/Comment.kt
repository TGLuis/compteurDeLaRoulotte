package library

class Comment(identifiant: Int,//depart
              var start: Int, private var end: Int) {
    private var num: Int = identifiant // identifiant
    var comment: String = ""
    var counter: String? = null

    fun clone(): Comment{
        val c = Comment(num, start, end)
        c.counter = counter
        c.comment = comment
        return c
    }
}