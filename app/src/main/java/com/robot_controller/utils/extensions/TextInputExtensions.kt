package com.robot_controller.utils.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.setupValidation(
    textInputLayout: TextInputLayout,
    errorMessage: String,
    validationRule: (String) -> Boolean
): TextWatcher {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            val text = s?.toString().orEmpty()
            textInputLayout.error = if (validationRule(text)) null else errorMessage
        }
    }
    addTextChangedListener(watcher)
    return watcher
}

fun List<EditText>.onAnyTextChanged(onChange: () -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) = onChange()
    }
    forEach { it.addTextChangedListener(watcher) }
    return watcher
}
