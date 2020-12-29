package library

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tgl.lecompteurdelaroulotte.R

object Dialogs {

    private fun simpleDialog(context: Context): MaterialAlertDialogBuilder {
        /* still to call:
            .setPositiveButton() {}
            .setTitle()
            .create()
            .show()
         */
        return MaterialAlertDialogBuilder(context)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
    }

    fun displaySimpleDialog(context: Context, title: Int, functionOK: () -> Unit) {
        simpleDialog(context)
            .setTitle(title)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                functionOK()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun infoDialog(context: Context): MaterialAlertDialogBuilder{
        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.info)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
    }

    fun displayInfoDialog(context: Context, message: Int) {
        infoDialog(context)
            .setMessage(message)
            .create()
            .show()
    }

    private fun warningDialog(context: Context): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.warning)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
    }

    fun displayWarningDialog(context: Context, message: String) {
        warningDialog(context)
            .setMessage(message)
            .create()
            .show()
    }

    private fun confirmationDialog(context: Context): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.confirm)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
    }

    fun displayConfirmationDialog(context: Context, message: String, functionOK: () -> Unit) {
        confirmationDialog(context)
            .setPositiveButton(R.string.save) { dialog, _ ->
                functionOK()
                dialog.dismiss()
            }
            .setMessage(message)
            .create()
            .show()
    }

    fun displayCustomDialog(context: Context, layoutToInflate: View, title: Int, function: () -> Boolean) {
        MaterialAlertDialogBuilder(context)
            .setView(layoutToInflate)
            .setTitle(title)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                if (function()){
                    dialog.dismiss()
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}
