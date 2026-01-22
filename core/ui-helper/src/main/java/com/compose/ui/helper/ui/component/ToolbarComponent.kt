package com.compose.ui.helper.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose.ui.helper.R
import com.compose.ui.helper.ui.theme.ComposeHelperTheme
import com.compose.ui.helper.ui.theme.Heights
import com.compose.ui.helper.ui.theme.Padding
import com.compose.ui.helper.ui.theme.TextAppearance

@Composable
fun Toolbars(
    title: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    iconBack: Painter? = painterResource(R.drawable.ic_arrow_back_black_24dp),
    iconMore: Painter = painterResource(R.drawable.ic_baseline_more_vert_24),
    saveText: String? = null,
    deleteText: String? = null,
    onBack: (() -> Unit)? = null,
    onMore: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = Padding.small, end = Padding.box),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = iconBack ?: painterResource(R.drawable.ic_arrow_back_black_24dp),
                        contentDescription = "Back"
                    )
                }
            }
            Text(
                text = title ?: stringResource(id = R.string.toolbar),
                style = TextAppearance.body1Bold(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Padding.small)
                    .align(Alignment.CenterVertically)
            )

            if (onDelete != null) {
                ButtonOutlinePrimary(
                    text = deleteText ?: stringResource(id = R.string.hapus),
                    onClick = onDelete,
                    modifier = Modifier
                        .height(Heights.normal)
                        .padding(end = Padding.box)
                )
            }

            if (onSave != null) {
                ButtonNormal(
                    text = saveText ?: stringResource(id = R.string.simpan),
                    onClick = onSave,
                    modifier = Modifier.height(Heights.normal)
                )
            }

            if (onMore != null) {
                IconButton(onClick = onMore) {
                    Icon(
                        painter = iconMore, contentDescription = "Add"
                    )
                }
            }

        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(0.5.dp),
            color = Color.LightGray
        )
    }
}


@Preview(showBackground = true)
@Preview(name = "Tablet", device = TABLET)
@Composable
fun ToolbarPreview() {
    ComposeHelperTheme {
        Toolbars()
    }
}

@Composable
fun Modifier.insertTopBarPadding() = this.windowInsetsPadding(
    WindowInsets.statusBars.only(WindowInsetsSides.Top)
)