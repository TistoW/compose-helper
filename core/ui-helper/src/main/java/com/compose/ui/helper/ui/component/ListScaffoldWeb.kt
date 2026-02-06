package com.compose.ui.helper.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.ui.helper.base.BaseUiState
import com.compose.ui.helper.model.FilterGroup
import com.compose.ui.helper.ui.theme.Colors
import com.compose.ui.helper.ui.theme.Radius
import com.compose.ui.helper.ui.theme.Spacing
import com.compose.ui.helper.R
import com.compose.ui.helper.ui.theme.ComposeHelperTheme
import com.compose.ui.helper.ui.theme.TextAppearance
import com.compose.ui.helper.utils.MobilePreview
import com.compose.ui.helper.utils.TabletPreview
import com.compose.ui.helper.utils.ext.ScreenConfig
import com.compose.ui.helper.utils.isMobilePhone
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <STATE, ITEMS> ListScaffoldWeb(
    modifier: Modifier = Modifier,
    title: String = "Title",
    screenConfig: ScreenConfig = ScreenConfig(),
    uiState: BaseUiState<STATE>,
    items: List<ITEMS> = listOf(),
    horizontalPadding: Float? = null,
    contentModifier: Modifier = Modifier
        .fillMaxWidth(
            screenConfig.getHorizontalPaddingWeight(horizontalPadding)
        )   // ✅ 80% width
        .padding(top = if (screenConfig.isMobile) Spacing.normal else Spacing.extraLarge),
    onUpdateUiState: (BaseUiState<STATE>.() -> BaseUiState<STATE>) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    onAddClick: (() -> Unit)? = null,

    addText: String = "Tambah",
    saveText: String = "Simpan",
    onSave: (() -> Unit)? = null,

    filterOptions: List<FilterGroup> = emptyList(),
    showToolbar: Boolean = true,
    showSearch: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {

    val listState = uiState.listScrollState
    val isLoading = uiState.isLoading
    val isRefreshing = uiState.isRefreshing

    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    /* =============================
     * SIDE EFFECTS
     * ============================= */

    // scroll to top setelah refresh selesai
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && listState.firstVisibleItemIndex > 0) {
            listState.animateScrollToItem(0)
        }
    }

    // load more ketika scroll mentok
    LaunchedEffect(listState.canScrollForward, listState.isScrollInProgress) {
        if (!listState.canScrollForward && !listState.isScrollInProgress && uiState.hasMore && !isLoading) {
//            onLoadMore()
        }
    }

    /* =============================
     * UI
     * ============================= */

    Box(
        modifier = modifier
            .background(Colors.White)
            .fillMaxSize()
    ) {
        Column(
            modifier = contentModifier.align(Alignment.Center)
        ) {

            if (showToolbar) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.normal)
                ) {
                    Text(
                        title,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    ButtonNormal(
                        horizontalContentPadding = Spacing.normal,
                        modifier = Modifier.align(Alignment.CenterEnd),
                        text = addText,
                        imageVector = Icons.Default.Add,
                        onClick = {

                        })
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = Spacing.normal)
                    .padding(bottom = Spacing.normal)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(Radius.medium),
                        ambientColor = Color.Black.copy(alpha = 0.10f),
                        clip = false
                    ), shape = RoundedCornerShape(Radius.medium), color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    /* =============================
                     * SEARCH + FILTER
                     * ============================= */

                    if (showSearch || filterOptions.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.normal),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (showSearch) {
                                CustomTextField(
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                        onSearch(it)
                                    },
                                    hint = "Cari...",
                                    style = TextFieldStyle.OUTLINED,
                                    strokeWidth = 0.5.dp,
                                    strokeColor = Colors.Gray3,
                                    floatingLabel = false,
                                    cornerRadius = Radius.normal,
                                    leadingIcon = painterResource(R.drawable.ic_search),
                                    endIcon = if (searchQuery.isNotEmpty()) painterResource(
                                        R.drawable.ic_asset_close
                                    )
                                    else null,
                                    endIconOnClick = {
                                        searchQuery = ""
                                        onSearch("")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (!screenConfig.isMobile) {
                                Box(modifier = Modifier.weight(1.5f))
                            }

                            if (filterOptions.isNotEmpty()) {
                                Row(
                                    modifier = Modifier,
                                ) {
                                    if (!isMobilePhone()) {
                                        RefreshButton(onClick = onRefresh)
                                        Spacer(Modifier.width(Spacing.small))
                                    }
                                    FilterButton(
                                        count = uiState.filters.size,
                                        onClick = { showFilterSheet = true })
                                }
                            }
                        }
                    }


                    if (header != null) {
                        header()
                    }

                    RefreshContainer(
                        isRefreshing = isRefreshing || (isLoading && uiState.isSearching),
                        onRefresh = onRefresh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {

                            LazyColumn(state = listState) {
                                content()
                            }

                            DraggableScrollbar(
                                listState = listState,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp)
                            )

                            // loading indicator
                            if (isLoading && !isRefreshing && !uiState.isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(
                                            if (uiState.isEmpty) Alignment.Center
                                            else Alignment.BottomCenter
                                        )
                                        .padding(16.dp), color = Colors.ColorPrimary
                                )
                            }

                            // empty state
                            if (items.isEmpty() && !isLoading) {
                                EmptyState(
                                    title = "Data Kosong",
                                    subtitle = "Belum ada data tersedia",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                            // FAB add
                            onAddClick?.let {
                                FloatingActionButton(
                                    onClick = it,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(Spacing.normal),
                                    containerColor = Colors.ColorPrimary
                                ) {
                                    Icon(Icons.Default.Add, null, tint = Colors.White)
                                }
                            }

                            // filter bottom sheet
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
                                        })
                                }
                            }
                        }
                    }

                    TablePaginationFooter(
                        rowsPerPage = uiState.perPage,
                        totalItems = uiState.totalSize,
                        currentPage = uiState.page,
                    )

                }
            }
        }
    }

}

@Composable
fun TablePaginationFooter(
    rowsPerPage: Int,
    totalItems: Int,
    currentPage: Int,
    onRowsPerPageChange: (Int) -> Unit = {},
    onPrevPage: () -> Unit = {},
    onNextPage: () -> Unit = {},
) {
    val start = currentPage * rowsPerPage + 1
    val end = min((currentPage + 1) * rowsPerPage, totalItems)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.Gray5)
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Rows per page
        Text(
            text = "Rows per page:",
            style = TextAppearance.body1(),
            color = Color.Gray
        )

        Spacer(Modifier.width(8.dp))

        RowsPerPageDropdown(
            value = rowsPerPage,
            onValueChange = onRowsPerPageChange
        )

        Spacer(Modifier.width(24.dp))

        // Range text
        Text(
            text = "$start–$end of $totalItems",
            style = TextAppearance.body1(),
            color = Color.Gray
        )

        Spacer(Modifier.width(16.dp))

        // Prev
        IconButton(
            onClick = onPrevPage,
            enabled = currentPage > 0
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Previous",
                tint = if (currentPage > 0) Color.DarkGray else Color.LightGray
            )
        }

        // Next
        IconButton(
            onClick = onNextPage,
            enabled = end < totalItems
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = if (end < totalItems) Color.DarkGray else Color.LightGray
            )
        }
    }
}

@Composable
fun RowsPerPageDropdown(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(5, 10, 20, 50)

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { expanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value.toString(),
                fontSize = 13.sp
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it.toString()) },
                    onClick = {
                        expanded = false
                        onValueChange(it)
                    }
                )
            }
        }
    }
}


@Composable
fun DraggableScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    width: Dp = 6.dp,
    minThumbHeight: Dp = 32.dp
) {
    val coroutineScope = rememberCoroutineScope()

    val totalItems = listState.layoutInfo.totalItemsCount
    val visibleItems = listState.layoutInfo.visibleItemsInfo.size

    if (totalItems == 0 || visibleItems == 0) return

    val proportion = visibleItems.toFloat() / totalItems.toFloat()
    val thumbHeightFraction = proportion.coerceIn(0.1f, 1f)

    BoxWithConstraints(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
    ) {
        val density = LocalDensity.current

        val containerHeightPx = constraints.maxHeight.toFloat()

        val thumbHeightPx = with(density) {
            max(
                minThumbHeight.toPx(),
                containerHeightPx * thumbHeightFraction
            )
        }

        val maxOffsetPx = containerHeightPx - thumbHeightPx

        var thumbOffsetPx by remember { mutableStateOf(0f) }

        LaunchedEffect(listState.firstVisibleItemIndex) {
            thumbOffsetPx =
                (listState.firstVisibleItemIndex.toFloat() / totalItems) * maxOffsetPx
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(0, thumbOffsetPx.roundToInt()) }
                .width(width)
                .height(with(LocalDensity.current) { thumbHeightPx.toDp() })
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.25f))
                .pointerInput(totalItems) {
                    detectVerticalDragGestures { _, dragAmount ->
                        thumbOffsetPx =
                            (thumbOffsetPx + dragAmount).coerceIn(0f, maxOffsetPx)

                        val scrollPercent = thumbOffsetPx / maxOffsetPx
                        val targetIndex =
                            (scrollPercent * totalItems).roundToInt()
                                .coerceIn(0, totalItems - 1)

                        coroutineScope.launch {
                            listState.scrollToItem(targetIndex)
                        }
                    }
                }
        )
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
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.Gray5)
            .padding(vertical = Spacing.normal, horizontal = Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderText(text = "Nama", modifier = Modifier.weight(2f))
        HeaderText(text = "Daerah", modifier = Modifier.weight(2f))
        HeaderText(text = "Update", modifier = Modifier.weight(1f))
        Spacer(Modifier.width(80.dp))
    }
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
fun TableRow(user: Example) {
    Column {
        HorizontalDivider(modifier = Modifier, thickness = 0.5.dp, color = Colors.Gray4)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.box),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RowText(modifier = Modifier.weight(2f), text = user.name, secondary = user.code)
        RowText(modifier = Modifier.weight(2f), text = user.daerah)
        RowText(modifier = Modifier.weight(1f), text = user.createdAt)

        Box(
            modifier = Modifier.width(80.dp)
        ) {
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Spacer(Modifier.width(Spacing.box))
                Icon(Icons.Default.Edit, null, tint = Color.Gray)
                Spacer(Modifier.width(Spacing.box))
                Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
            }
        }
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
            style = TextAppearance.body1()
        )
        if (secondary != null) {
            Text(
                text = secondary,
                modifier = Modifier,
                style = TextAppearance.body2(),
                color = Colors.Gray2
            )
        }
    }
}


@Composable
fun ScreenContentWebPreview(
    screenConfig: ScreenConfig = ScreenConfig(),
) {
    ListScaffoldWeb(
        uiState = BaseUiState(
            data = Example()
        ),
        items = list,
        screenConfig = screenConfig,
        filterOptions = defaultFilter(),
        header = { TableHeader() },
        content = {
            items(list, key = { it.id }) { item ->
                TableRow(user = item)
            }
        })
}


@TabletPreview
@Composable
fun TabletNewPreview() {
    ScreenContentWebPreview(ScreenConfig(700.dp))
}


@TabletPreview
@Composable
fun TabletWebPreview() {

}


@MobilePreview
@Composable
fun MobileWebPreview() {
    ComposeHelperTheme {

    }
    ScreenContentWebPreview(ScreenConfig(500.dp))
}

