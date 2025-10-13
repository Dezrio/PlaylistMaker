package com.example.playlistmaker.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R

@Composable
fun CommonTextField(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String = stringResource(R.string.empty_string),
    onTextChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit
) {
    BasicTextField(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .onFocusChanged {
                onFocusChanged(it.isFocused)
            },

        value = text,
        onValueChange = {
            onTextChanged(it)
        },
        singleLine = true,
        cursorBrush = SolidColor(colorResource(R.color.text_color_not_empty)),
        textStyle = MaterialTheme.typography.titleMedium,
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    innerTextField()
                }

                if (trailingIcon != null && text.isNotEmpty()) trailingIcon()
            }
        }
    )
}