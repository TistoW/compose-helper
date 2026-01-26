package com.compose.ui.helper.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.compose.data.helper.utils.ext.reformatDate
import com.compose.ui.helper.base.BaseUiState
import com.compose.ui.helper.model.FilterGroup
import com.compose.ui.helper.ui.theme.Colors
import com.compose.ui.helper.ui.theme.Radius
import com.compose.ui.helper.ui.theme.Spacing
import com.compose.ui.helper.R
import com.compose.ui.helper.ui.theme.TextAppearance
import com.compose.ui.helper.utils.MobilePreview
import com.compose.ui.helper.utils.TabletPreview
import com.compose.ui.helper.utils.ext.ScreenConfig
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <STATE> ListContainer(
    modifier: Modifier = Modifier,
    title: String = "Title",
    screenConfig: ScreenConfig = ScreenConfig(),
    uiState: BaseUiState<STATE>,
    updateState: (BaseUiState<STATE>.() -> BaseUiState<STATE>) -> Unit = {},
    onSearch: (String) -> Unit = {},
    filterOptions: List<FilterGroup> = listOf(),
    horizontalPadding: Float? = null,
    showSearch: Boolean = true,
    showForm: (() -> Unit)? = null,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {

    val isMobile = screenConfig.isMobile
    val listState = uiState.listScrollState
    var searchQuery by remember { mutableStateOf("") }
    val isLoading = uiState.isLoading
    val isRefreshing = uiState.isRefreshing
    var showSheetFilter by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 🔑 SCROLL TO TOP SETELAH REFRESH SELESAI
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            listState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(listState.canScrollForward, listState.isScrollInProgress) {
        if (!listState.canScrollForward && !listState.isScrollInProgress && uiState.hasMore && !isLoading) {
            onLoadMore()
        }
    }
    Column(
        modifier = Modifier.background(color = Colors.White)
    ) {

        Toolbars(
            title = title,
            onBack = onBack
        )

        RefreshContainer(
            isRefreshing = isRefreshing || (isLoading && uiState.isSearching),
            onRefresh = {
                onRefresh()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(screenConfig.getHorizontalPaddingWeight(horizontalPadding))   // ✅ 80% width
                    .padding(horizontal = if (isMobile) Spacing.normal else 0.dp)
                    .padding(top = if (isMobile) Spacing.normal else Spacing.huge)
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.box),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (showSearch) {
                        CustomTextField(
                            value = searchQuery,
                            onValueChange = {
                                onSearch(it)
                                searchQuery = it
                            },
                            hint = "Cari...",
                            style = TextFieldStyle.OUTLINED,
                            strokeWidth = 0.5.dp,
                            strokeColor = Colors.Gray3,
                            floatingLabel = false,
                            cornerRadius = Radius.normal,
                            leadingIcon = painterResource(R.drawable.ic_search),
                            endIcon = if (searchQuery.isNotEmpty()) painterResource(R.drawable.ic_asset_close) else null,
                            endIconOnClick = {
                                searchQuery = ""
                                onSearch("")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }


                    if (!isMobile) {
                        Box(modifier = Modifier.weight(1.5f))
                    }

                    Box(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        if (filterOptions.isNotEmpty()) {
                            FilterButton(
                                count = uiState.filters.size,
                                onClick = { showSheetFilter = true }
                            )
                        }
                    }
                }

                Box(
                    modifier = modifier.fillMaxSize()
                ) {

                    LazyColumn(
                        state = uiState.listScrollState,
                        modifier = Modifier
                    ) {
                        content()
                    }

                    if (isLoading && !isRefreshing && !uiState.isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(if (uiState.isEmpty) Alignment.Center else Alignment.BottomCenter),
                            color = Colors.ColorPrimary
                        )
                    }

                    if (uiState.isEmpty && !isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                title = stringResource(
                                    R.string.s_kosong, title
                                ),
                                subtitle = "Belum ada data tersedia"
                            )
                        }
                    }

                    if (showForm != null) {
                        FloatingActionButton(
                            onClick = {
                                showForm()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(Spacing.normal),
                            containerColor = Colors.ColorPrimary
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Category",
                                tint = Colors.White
                            )
                        }
                    }

                    if (showSheetFilter) {
                        ModalBottomSheet(
                            onDismissRequest = { showSheetFilter = false },
                            sheetState = sheetState,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        ) {
                            GeneralFilterBottomSheet(
                                onClose = { showSheetFilter = false },
                                options = filterOptions,
                                preselected = uiState.filters,
                                onApply = { selectedItems ->
                                    updateState { copy(filters = selectedItems) }
                                    onRefresh()
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}

@Serializable
data class Example(
    val id: String = "",
    val name: String = "",
    val createdAt: String = "",
)


private val list = List(10) {
    Example(
        id = it.toString(),
        name = "Category $it",
        createdAt = "12 Des 2025"
    )
}

@Composable
private fun ListItem(
    item: Example,
    onClick: () -> Unit = {},
) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(Spacing.box)
        ) {

            Text(
                text = item.name,
                style = TextAppearance.body1Bold(),
                color = Colors.Dark,
                maxLines = 1,                     // ✅ limit to one line
                overflow = TextOverflow.Ellipsis, // ✅ show "..."
                modifier = Modifier
            )

            Spacer(Modifier.height(Spacing.tiny))

            Text(
                text = item.createdAt.reformatDate(),
                style = TextAppearance.body2(),
                color = Colors.Gray2,
                modifier = Modifier
            )
        }

        HorizontalDivider(thickness = 0.5.dp)
    }

}

@Composable
fun ScreenContentPreview(
    screenConfig: ScreenConfig = ScreenConfig(),
) {
    ListContainer(
        uiState = BaseUiState(
            data = Example()
        ),
        screenConfig = screenConfig
    ) {
        items(list, key = { it.id }) { item ->
            ListItem(item = item)
        }
    }
}

@MobilePreview
@Composable
fun MobilePreview() {
    ScreenContentPreview(ScreenConfig(500.dp))
}

@TabletPreview
@Composable
fun TabletPreview() {
    ScreenContentPreview()
}

