package com.tisto.helper.core.helper.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tisto.helper.core.helper.utils.ext.shorten
import com.tisto.helper.core.helper.utils.ext.ScreenConfig
import com.tisto.helper.core.helper.ui.theme.Spacing
import com.tisto.helper.core.helper.ui.theme.Colors
import com.tisto.helper.core.helper.ui.theme.ComposeHelperTheme
import com.tisto.helper.core.helper.utils.MobilePreview
import com.tisto.helper.core.helper.utils.TabletPreview
import com.tisto.helper.core.helper.utils.title

@Composable
fun <ITEM> FormContainer(
    modifier: Modifier = Modifier,
    title: String = "Title",
    forceTitle: String? = null,
    selectedItemName: String? = "item ini",
    screenConfig: ScreenConfig = ScreenConfig(),
    isFormValid: Boolean = true,
    isLoadingProcess: Boolean = true,
    horizontalPadding: Float? = null,
    item: ITEM? = null,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable () -> Unit
) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    val isMobile = screenConfig.isMobile

    fun onDeleteClick() {
        showDeleteDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.White)
    ) {

        Toolbars(
            title = if (!forceTitle.isNullOrEmpty()) forceTitle else title.title(item != null),
            onBack = onBack,
            onSave = if (isMobile) null else onSave,
            isLoadingSave = isLoadingProcess,
            isLoadingDelete = isLoadingProcess,
            onDelete = if (!isMobile && item != null) ::onDeleteClick else null,
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .fillMaxWidth(screenConfig.getHorizontalPaddingWeight(horizontalPadding))   // ✅ 80% width
                    .padding(horizontal = if (isMobile) Spacing.normal else 0.dp),
                verticalArrangement = Arrangement.Top
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    content()
                }

                if (isMobile) {

                    Spacer(modifier = Modifier.height(Spacing.tiny))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.small),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        if (item != null) {
                            SimpleButton(
                                text = "Hapus",
                                onClick = ::onDeleteClick,
                                isLoading = isLoadingProcess,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = Spacing.tiny),
                                strokeWidth = 0.5.dp,
                                textColor = Colors.ColorPrimary
                            )
                        }

                        SimpleButton(
                            text = "Simpan",
                            onClick = onSave,
                            isLoading = isLoadingProcess,
                            enabled = !isLoadingProcess && isFormValid,
                            modifier = Modifier.weight(1f)
                        )
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

}

@Composable
fun FromScreenContentPreview(
    screenConfig: ScreenConfig = ScreenConfig(),
) {
    FormContainer(
        screenConfig = screenConfig,
        item = Example("Example 1"),
        content = {

            Column {

                Spacer(modifier = Modifier.height(Spacing.normal))

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
        FromScreenContentPreview()
    }

}

@MobilePreview
@Composable
fun MobilePreviewsForm() {
    ComposeHelperTheme {
        FromScreenContentPreview(ScreenConfig(500.dp))
    }
}
