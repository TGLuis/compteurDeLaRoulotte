package library

import android.content.Context
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tgl.lecompteurdelaroulotte.R

object Dialogs {

    fun simpleDialog(context: Context): MaterialAlertDialogBuilder {
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

    fun infoDialog(context: Context): MaterialAlertDialogBuilder{
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

    fun warningDialog(context: Context): MaterialAlertDialogBuilder {
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

    fun confirmationDialog(context: Context): MaterialAlertDialogBuilder {
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

    fun displayCustomDialog(context: Context, viewInflated: View, title: Int, function: () -> Boolean) {
        MaterialAlertDialogBuilder(context)
            .setView(viewInflated)
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
