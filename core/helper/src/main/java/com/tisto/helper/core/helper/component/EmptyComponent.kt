package com.tisto.helper.core.helper.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tisto.helper.core.helper.R
import com.tisto.helper.core.helper.ui.theme.ComposeHelperTheme
import com.tisto.helper.core.helper.ui.theme.TextAppearance

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    search: String = "",
    actionText: String? = null,
    onAction: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {

    val displaySearch = if (search.length > 10) {
        search.take(10) + "..."
    } else {
        search
    }
    val textSubtitle = if (search.isNotEmpty()) {
        stringResource(
            R.string.hasil_pencarian_tidak_ditemukan,
            displaySearch
        )
    } else subtitle

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_asset_list),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(5.dp))
        Text(title, style = TextAppearance.title2(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(5.dp))
        Text(textSubtitle, style = TextAppearance.body2(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        if (actionText != null) ButtonNormal(text = actionText) { onAction() }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    ComposeHelperTheme {
        EmptyState(
            title = "No Data",
            subtitle = "There is no data available at the moment.",
            actionText = "Refresh",
            onAction = {}
        )
    }
}