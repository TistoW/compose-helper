package com.compose.ui.helper.ui.component

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PopupMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    items: List<String>,
    onSelected: (String) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Color.White) // ⟵ paksa putih
    ) {
        items.forEach { label ->
            DropdownMenuItem(
                text = { Text(label) },
                onClick = {
                    onSelected(label)
                    onDismiss()
                }
            )
        }
    }
}
