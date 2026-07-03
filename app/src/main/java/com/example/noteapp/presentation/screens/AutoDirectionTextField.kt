package com.example.noteapp.presentation.components

import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AutoDirectionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    textSize: Float = 14f,
    isBold: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    AndroidView(
        factory = { context ->
            EditText(context).apply {
                setText(value)
                setHint(hint)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                setSingleLine(singleLine)
                setMaxLines(maxLines)

                setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG)
                setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE)

                setTypeface(typeface, if (isBold) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)

                setTextColor(colors.onSurface.toArgb())
                setHintTextColor(colors.onSurface.copy(alpha = 0.6f).toArgb())
                setLinkTextColor(colors.primary.toArgb())
                setBackgroundColor(Color.Transparent.toArgb())

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    textCursorDrawable?.setTint(colors.primary.toArgb())
                }

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        val newText = s?.toString() ?: ""
                        onValueChange(newText)
                    }
                })
            }
        },
        update = { editText ->
            if (editText.text.toString() != value) {
                val selectionStart = editText.selectionStart
                val selectionEnd = editText.selectionEnd

                editText.setText(value)
                if (selectionStart <= value.length) {
                    editText.setSelection(selectionStart, selectionEnd.coerceAtMost(value.length))
                }
            }
            editText.setTextColor(colors.onSurface.toArgb())
            editText.setHintTextColor(colors.onSurface.copy(alpha = 0.6f).toArgb())
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                editText.textCursorDrawable?.setTint(colors.primary.toArgb())
            }
        },
        modifier = modifier
    )
}