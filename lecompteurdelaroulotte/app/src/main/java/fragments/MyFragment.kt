package fragments

import android.support.v4.app.Fragment

open class MyFragment: Fragment() {
    open fun TAG(): String {
        return "== MYFRAGMENT =="
    }
}