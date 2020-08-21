package fragments

import android.content.res.Configuration
import androidx.fragment.app.Fragment

open class MyFragment: Fragment() {
    open fun TAG(): String {
        return "== MYFRAGMENT =="
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}