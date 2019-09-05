package pixycheck.minutecodes.com.pixycheck.wrapper

import android.app.AlertDialog
import android.content.Context
import pixycheck.minutecodes.com.pixycheck.R
import pixycheck.minutecodes.com.pixycheck.java_files.DialogDismissListener
import pixycheck.minutecodes.com.pixycheck.java_files.NegativeButtonNotifier
import pixycheck.minutecodes.com.pixycheck.java_files.NeutralButtonNotifier
import pixycheck.minutecodes.com.pixycheck.java_files.PositiveButtonNotifier

open class AlertDialogWrapper(context: Context) {
    private val alertDialog = AlertDialog.Builder(context)
    var isDialogActive = false

    fun buildDialog() = alertDialog.apply {
        setCancelable(false)
        setTitle(context.getString(R.string.dialog_title))
        setMessage(context.getString(R.string.dialog_message))
    }

    fun showDialog() {
        alertDialog.show()
        isDialogActive = true
    }

    fun dismissListener(dismissListener: DialogDismissListener): AlertDialogWrapper {
        alertDialog.setOnDismissListener {
            isDialogActive = false
            dismissListener.onDialogDismissed()
        }
        return this
    }

    open fun setupPositiveButton(text: String, buttonNotifier: PositiveButtonNotifier) = alertDialog.apply {
        setPositiveButton(text) { _, _ -> buttonNotifier.onPositiveButtonClicked() }
    }

    open fun setupNegativeButton(text: String, buttonNotifier: NegativeButtonNotifier) = alertDialog.apply {
        setNegativeButton(text) { _, _ -> buttonNotifier.onNegativeButtonClicked() }
    }

    open fun setupNeutralButton(text: String, buttonNotifier: NeutralButtonNotifier) = alertDialog.apply {
        setNeutralButton(text) { _, _ -> buttonNotifier.onNeutralButtonClicked() }
    }
}