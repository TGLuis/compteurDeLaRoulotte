package library

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import tgl.lecompteurdelaroulotte.R

object Listeners {
    fun dropArrowButtonListener(context: Context, b: Boolean, tv: TextView, ib: ImageButton, text: Int, height: Int): Boolean {
        if (b) {
            dropUp(tv, ib)
        } else {
            dropDown(context, tv, ib, text, height)
        }
        return !b
    }

    private fun dropUp(tv: TextView, ib: ImageButton) {
        tv.height = 0
        tv.text = ""
        ib.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
    }

    private fun dropDown(context: Context, tv: TextView, ib: ImageButton, text: Int, height: Int) {
        tv.height = height
        tv.text = context.getString(text)
        ib.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
    }


}
