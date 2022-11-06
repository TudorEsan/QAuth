package net.theluckycoder.qr.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadonlyTextField(
    modifier: Modifier,
    fieldModifier: Modifier = Modifier,
    value: String,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) = Box(modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = fieldModifier,
        label = label,
        trailingIcon = trailingIcon,
        colors = colors,
    )
    Box(
        modifier = Modifier
            .matchParentSize()
            .alpha(0f)
            .clickable(onClick = onClick),
    )
}
