package com.robot_controller.utils.extensions

import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

fun Button.enableWhenAllValid(
    fields: List<EditText>,
    isValid: () -> Boolean
): TextWatcher {
    val watcher = fields.onAnyTextChanged { isEnabled = isValid() }
    isEnabled = isValid()
    return watcher
}
