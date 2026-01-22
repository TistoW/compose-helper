package com.compose.ui.helper.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.ui.helper.ui.theme.Colors
import com.compose.ui.helper.ui.theme.Radius
import com.compose.ui.helper.ui.theme.Spacing
import com.compose.ui.helper.ui.theme.TextAppearance

@Composable
fun ButtonOutlinePrimary(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = TextAppearance.body2Bold(),
    enabled: Boolean = true,
    horizontalContentPadding: Dp = Spacing.box,
    colorPrimary: Color = Colors.Green700,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,

        shape = RoundedCornerShape(Radius.normal),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colorPrimary
        ),
        border = BorderStroke(
            width = 1.dp, color = if (enabled) colorPrimary else Colors.Gray4
        ),
        contentPadding = PaddingValues(horizontal = horizontalContentPadding, vertical = 0.dp)
    ) {
        Text(
            text = text,
            style = textStyle,
            textAlign = TextAlign.Center,
            color = if (enabled) colorPrimary else Colors.Gray4
        )
    }
}

@Composable
fun ButtonNormal(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = TextAppearance.body2Bold(),
    enabled: Boolean = true,
    horizontalContentPadding: Dp = Spacing.box,
    colorPrimary: Color = Colors.Green700,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Radius.normal),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) colorPrimary else Colors.Gray4,
            contentColor = Color.White
        ),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = horizontalContentPadding, vertical = 0.dp)
    ) {
        Text(
            text = text,
            style = textStyle,
            textAlign = TextAlign.Center
        )
    }
}