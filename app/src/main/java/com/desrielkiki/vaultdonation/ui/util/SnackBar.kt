package com.desrielkiki.vaultdonation.ui.util
import android.view.View
import com.desrielkiki.vaultdonation.ui.util.ConstantValue.SNACKBAR_DURATION
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(msg: String, viewId: Int) {
    val snackbar = Snackbar.make(this, msg, SNACKBAR_DURATION)
    snackbar.setAnchorView(viewId)
    snackbar.show()
}