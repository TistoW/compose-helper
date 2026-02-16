package com.tisto.helper.core.helper.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tisto.helper.core.helper.ui.theme.Colors
import com.tisto.helper.core.helper.ui.theme.ComposeHelperTheme
import com.tisto.helper.core.helper.ui.theme.Radius
import com.tisto.helper.core.helper.ui.theme.Spacing
import com.tisto.helper.core.helper.utils.ext.MobilePreview
import com.tisto.helper.core.helper.utils.ext.ScreenConfig
import com.tisto.helper.core.helper.utils.ext.TabletPreview
import com.tisto.helper.core.helper.utils.ext.shorten
import com.tisto.helper.core.helper.utils.ext.title

@Composable
fun <ITEM> FormContainer(
    title: String = "Title",
    forceTitle: String? = null,
    screenConfig: ScreenConfig = ScreenConfig(),
    isFormValid: Boolean = true,
    horizontalPadding: Float? = null,
    item: ITEM? = null,
    selectedItemName: String? = "item ini",
    isLoadingProcess: Boolean = false,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit, // 👈 important: allow ColumnScope
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isMobile = screenConfig.isMobile

    val scrollState = rememberScrollState()

    fun onDeleteClick() {
        showDeleteDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.White)
    ) {

        // ✅ Toolbar fixed (not inside scroll)
        Toolbars(
            title = if (!forceTitle.isNullOrEmpty()) forceTitle else title.title(item != null),
            onBack = if (!isMobile) null else onBack
        )

        // ✅ Scroll container that makes the Surface move
        Box(
            modifier = Modifier
                .weight(1f)                   // take remaining space
                .fillMaxWidth()
                .verticalScroll(scrollState)  // <— surface moves when scrolling
                .imePadding()                 // helps when keyboard appears
        ) {

            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(screenConfig.getHorizontalPaddingFormWeight(horizontalPadding))
                    .padding(bottom = Spacing.normal)
                    .then(
                        if (isMobile)
                            Modifier.padding(horizontal = Spacing.normal)
                        else
                            Modifier
                                .padding(top = Spacing.huge)
                                .shadow(
                                    elevation = 5.dp,
                                    shape = RoundedCornerShape(Radius.medium),
                                    ambientColor = Color.Black.copy(alpha = 0.10f),
                                    clip = false
                                )

                    ),
                shape = RoundedCornerShape(Radius.medium),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isMobile)
                                Modifier
                            else
                                Modifier.padding(horizontal = Spacing.medium)
                        ),
                    verticalArrangement = Arrangement.Top
                ) {
                    // ✅ all form fields
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isMobile)
                                    Modifier
                                else
                                    Modifier
                            ),
                        verticalArrangement = verticalArrangement,
                        horizontalAlignment = horizontalAlignment,
                        content = content
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isMobile)
                                    Modifier.padding(vertical = Spacing.normal)
                                else
                                    Modifier.padding(vertical = Spacing.medium)
                            ),
                    ) {

                        if (!isMobile) {
                            ButtonNormal(
                                text = "Kembali",
                                onClick = onBack,
                                isLoading = isLoadingProcess,
                                backgroundColor = Color.Black,
                                horizontalContentPadding = Spacing.normal
                            )
                        }

                        Row(
                            modifier = Modifier
                                .then(
                                    if (isMobile)
                                        Modifier.fillMaxWidth()
                                    else
                                        Modifier.align(Alignment.CenterEnd)
                                ),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (item != null) {
                                ButtonNormal(
                                    text = "Hapus",
                                    onClick = ::onDeleteClick,
                                    isLoading = isLoadingProcess,
                                    strokeWidth = 1.dp,
                                    strokeColor = Color.Black,
                                    textColor = Colors.Black,
                                    horizontalContentPadding = Spacing.normal,
                                    modifier = Modifier.then(
                                        if (isMobile)
                                            Modifier.weight(1f)
                                        else
                                            Modifier
                                    )
                                )
                            }

                            ButtonNormal(
                                text = "Simpan",
                                onClick = onSave,
                                backgroundColor = Color.Black,
                                horizontalContentPadding = Spacing.normal,
                                isLoading = isLoadingProcess,
                                enabled = isFormValid,
                                modifier = Modifier.then(
                                    if (isMobile)
                                        Modifier.weight(1f)
                                    else
                                        Modifier
                                )
                            )
                        }
                    }

                }
            }
        }

        DeleteConfirmationDialog(
            showDialog = showDeleteDialog,
            onDismiss = { showDeleteDialog = false },
            onConfirm = onDelete,
            itemName = selectedItemName.shorten()
        )
    }
}


@Composable
fun FromScreenContentPreview(
    screenConfig: ScreenConfig = ScreenConfig(),
) {
    FormContainer(
        screenConfig = screenConfig,
        item = Example(),
        content = {

            Column {

                Spacer(modifier = Modifier.height(Spacing.large))

                CustomTextField(
                    value = "",
                    onValueChange = { },
                    hint = "Nama",
                    style = TextFieldStyle.OUTLINED,
                    strokeWidth = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

            }

        }
    )
}

@TabletPreview
@Composable
fun TabletPreviewsForm() {
    ComposeHelperTheme {
        FromScreenContentPreview(ScreenConfig(700.dp))
    }

}

@MobilePreview
@Composable
fun MobilePreviewsForm() {
    ComposeHelperTheme {
        FromScreenContentPreview()
    }
}
