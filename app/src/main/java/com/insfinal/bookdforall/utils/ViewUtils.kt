package com.insfinal.bookdforall.utils

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.textfield.TextInputLayout

fun tintStartIcon(textInputLayout: TextInputLayout, colorResId: Int, context: Context) {
    val startIcon = textInputLayout.findViewById<View>(com.google.android.material.R.id.text_input_start_icon)
    startIcon?.let {
        ViewCompat.setBackgroundTintList(
            it,
            ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
        )
    }
}
