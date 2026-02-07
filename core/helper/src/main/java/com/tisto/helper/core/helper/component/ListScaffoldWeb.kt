package com.tisto.helper.core.helper.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tisto.helper.core.helper.R
import com.tisto.helper.core.helper.base.BaseUiState
import com.tisto.helper.core.helper.retrofit.model.FilterGroup
import com.tisto.helper.core.helper.ui.theme.Colors
import com.tisto.helper.core.helper.ui.theme.Radius
import com.tisto.helper.core.helper.ui.theme.Spacing
import com.tisto.helper.core.helper.ui.theme.TextAppearance
import com.tisto.helper.core.helper.utils.ext.MobilePreview
import com.tisto.helper.core.helper.utils.ext.TabletPreview
import com.tisto.helper.core.helper.utils.ext.ScreenConfig
import com.tisto.helper.core.helper.utils.ext.isMobilePhone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <STATE, ITEMS> ListScaffoldWeb(
    modifier: Modifier = Modifier,
    title: String = "Title",
    screenConfig: ScreenConfig = ScreenConfig(),
    uiState: BaseUiState<STATE>,
    items: List<ITEMS> = emptyList(),
    horizontalPadding: Float? = null,

    // Keep your default width behavior (80% on web), but we’ll wrap it with fillMaxSize() internally
    contentModifier: Modifier = Modifier
        .fillMaxWidth(screenConfig.getHorizontalPaddingListWeight(horizontalPadding))
        .then(
            if (screenConfig.isMobile)
                Modifier
                    .padding(horizontal = Spacing.normal)
            else
                Modifier
//                    .padding(top = Spacing.extraLarge)
        ),

    onUpdateUiState: (BaseUiState<STATE>.() -> BaseUiState<STATE>) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onRowsPerPageChange: (Int) -> Unit = {},
    onPrevPage: () -> Unit = {},
    onNextPage: () -> Unit = {},
    onAddClick: (() -> Unit)? = null,
    addText: String = "Tambah",

    filterOptions: List<FilterGroup> = emptyList(),
    showToolbar: Boolean = true,
    showSearch: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit,

    // ✅ New: minimum list height that follows perPage (estimation)
    // If your row/table height differs, just adjust this value.
    minListHeight: Dp = 350.dp,
    estimatedRowHeight: Dp = 56.dp,
) {
    val listState = uiState.listScrollState
    val isLoading = uiState.isLoading
    val isRefreshing = uiState.isRefreshing

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ✅ Scroll to top after refresh is done
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && listState.firstVisibleItemIndex > 0) {
            listState.animateScrollToItem(0)
        }
    }

    val isMobile = screenConfig.isMobile || isMobilePhone()

// ✅ static minimum list height logic
    val contentHeight = estimatedRowHeight * items.size
    val fillerHeight = (minListHeight - contentHeight)
        .coerceAtLeast(0.dp)

    Box(
        modifier = modifier
            .background(Colors.White)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        RefreshContainer(
            isRefreshing = isRefreshing || (isLoading && uiState.isSearching),
            onRefresh = onRefresh,
            modifier = contentModifier
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
            ) {

                item {
                    if (screenConfig.isMobile) {
                        Spacer(modifier = Modifier.height(Spacing.normal))
                    } else {
                        Spacer(modifier = Modifier.height(Spacing.extraLarge))
                    }
                }


                // =============================
                // TOOLBAR
                // =============================
                if (showToolbar) {
                    item(key = "toolbar") {
                        ToolbarRow(
                            title = title,
                            addText = addText,
                            onAddClick = onAddClick
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))
                    }
                }

                // =============================
                // SEARCH + FILTER
                // =============================
                if (showSearch || filterOptions.isNotEmpty()) {
                    item(key = "search-filter") {
                        SearchFilterRow(
                            isMobile = isMobile,
                            showSearch = showSearch,
                            filterOptions = filterOptions,
                            searchQuery = searchQuery,
                            onSearchQueryChange = {
                                searchQuery = it
                                onSearch(it)
                            },
                            onClearSearch = {
                                searchQuery = ""
                                onSearch("")
                            },
                            refreshCount = uiState.filters.size,
                            onRefresh = onRefresh,
                            onOpenFilter = { showFilterSheet = true }
                        )
                        Spacer(modifier = Modifier.height(Spacing.normal))
                    }
                }

                // =============================
                // HEADER (e.g., table header)
                // =============================
                header?.let {
                    item(key = "header") {
                        it()
                    }
                }

                // =============================
                // CONTENT ROWS
                // =============================
                if (items.isEmpty() && !isLoading) {
                    item(key = "empty") {
                        EmptyState(
                            title = "Data Kosong",
                            subtitle = "Belum ada data tersedia",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.extraLarge)
                        )
                    }
                } else {
                    content()
                }

//                // ✅ Filler spacer to keep minimum list height based on perPage
                if (!isLoading && items.isNotEmpty() && fillerHeight > 0.dp) {
                    item(key = "filler") {
                        Spacer(Modifier.height(fillerHeight))
                    }
                }

                // Loading indicator (shown at the end of the list)
                if (isLoading && !isRefreshing && !uiState.isSearching) {
                    item(key = "loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.large),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Colors.ColorPrimary)
                        }
                    }
                }

                // =============================
                // FOOTER PAGINATION
                // =============================
                if (items.isNotEmpty()) {
                    item(key = "pagination") {
                        TablePaginationFooter(
                            rowsPerPage = uiState.perPage,
                            totalItems = uiState.totalSize,
                            currentPage = uiState.page,
                            onNextPage = onNextPage,
                            onPrevPage = onPrevPage,
                            onRowsPerPageChange = onRowsPerPageChange
                        )
                    }
                }
            }
        }

        // =============================
        // FILTER SHEET (overlay)
        // =============================
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                GeneralFilterBottomSheet(
                    options = filterOptions,
                    preselected = uiState.filters,
                    onClose = { showFilterSheet = false },
                    onApply = { selected ->
                        onUpdateUiState { copy(filters = selected) }
                        showFilterSheet = false
                        onRefresh()
                    }
                )
            }
        }
    }
}

@Composable
private fun ToolbarRow(
    title: String,
    addText: String,
    onAddClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextAppearance.headline2Bold(),
            modifier = Modifier.weight(1f)
        )

        if (onAddClick != null) {
            ButtonNormal(
                backgroundColor = Colors.Black,
                horizontalContentPadding = Spacing.normal,
                text = addText,
                imageVector = Icons.Default.Add,
                onClick = onAddClick
            )
        }
    }
}

@Composable
private fun SearchFilterRow(
    isMobile: Boolean,
    showSearch: Boolean,
    filterOptions: List<FilterGroup>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    refreshCount: Int,
    onRefresh: () -> Unit,
    onOpenFilter: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.box),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showSearch) {
            CustomTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                hint = "Cari...",
                style = TextFieldStyle.OUTLINED,
                strokeWidth = 0.5.dp,
                strokeColor = Colors.Gray3,
                floatingLabel = false,
                cornerRadius = Radius.normal,
                leadingIcon = vectorResource(R.drawable.ic_search),
                endIcon = if (searchQuery.isNotEmpty()) vectorResource(R.drawable.ic_asset_close) else null,
                endIconOnClick = onClearSearch,
                modifier = Modifier.weight(1f)
            )
        }

        if (!isMobile) {
            Spacer(Modifier.weight(1.5f))
        }

        if (filterOptions.isNotEmpty()) {
            if (!isMobile) {
                RefreshButton(onClick = onRefresh)
//                Spacer(Modifier.width(Spacing.tiny))
            }
            FilterButton(count = refreshCount, onClick = onOpenFilter)
        }
    }
}


fun exampleTableSpec(): TableSpec<Example> {
    val columns = listOf(
        TableColumn<Example>(
            key = "name",
            title = "Nama",
            weight = 2f,
            cell = { row ->
                RowText(
                    modifier = Modifier.fillMaxWidth(),
                    text = row.name,
                    secondary = row.code
                )
            }
        ),
        TableColumn(
            key = "region",
            title = "Daerah",
            weight = 2f,
            cell = { row ->
                RowText(
                    modifier = Modifier.fillMaxWidth(),
                    text = row.daerah
                )
            }
        ),
        TableColumn(
            key = "updated",
            title = "Update",
            weight = 1f,
            cell = { row ->
                RowText(
                    modifier = Modifier.fillMaxWidth(),
                    text = row.createdAt
                )
            }
        )
    )

    return TableSpec(
        columns = columns,
        actionsWidth = 80.dp
    )
}

@Composable
fun HeaderText(
    modifier: Modifier = Modifier, text: String
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = TextAppearance.body1Bold(),
            color = Color.Gray
        )

        VerticalDivider(
            modifier = Modifier
                .height(18.dp)
                .padding(end = Spacing.small),
            thickness = 1.dp,
            color = Colors.Gray4
        )
    }
}

@Composable
fun RowText(
    modifier: Modifier = Modifier,
    text: String = "",
    secondary: String? = null,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier,
            style = TextAppearance.body1(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (secondary != null) {
            Text(
                text = secondary,
                modifier = Modifier,
                style = TextAppearance.body2(),
                color = Colors.Gray2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


private val list = List(10) {
    Example(
        id = it.toString(),
        name = "Desa $it",
        code = "CODE-${it}${it - 1}",
        daerah = "Kabupaten $it",
        createdAt = "12 Des 2025"
    )
}

@Composable
fun ScreenContentWebPreview(
    screenConfig: ScreenConfig = ScreenConfig(),
) {
    val spec = remember { exampleTableSpec() }

    ListScaffoldWeb(
        uiState = BaseUiState(
            data = Example()
        ),
        items = list,
        screenConfig = screenConfig,
        filterOptions = defaultFilter(),
        header = { TableHeader(spec) },
        onAddClick = {},
        content = {
            items(list, key = { it.id }) { item ->
                TableRow(item, spec, actions = {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(Spacing.box))
                    Icon(Icons.Default.MoreVert, null)
                })
            }
        })
}


@TabletPreview
@Composable
fun TabletNewPreview() {
    ScreenContentWebPreview(ScreenConfig(750.dp))
}


@TabletPreview
@Composable
fun TabletWebPreview() {

}


@MobilePreview
@Composable
fun MobileWebPreview() {
    ScreenContentWebPreview(ScreenConfig(500.dp))
}

