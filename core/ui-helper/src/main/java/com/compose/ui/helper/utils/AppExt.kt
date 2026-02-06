package com.compose.ui.helper.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import com.compose.ui.helper.R

@Preview(showBackground = true, device = PHONE)
annotation class MobilePreview


@Preview(showBackground = true, device = TABLET)
annotation class TabletPreview

@Composable
fun String.title(isDataAvailable: Boolean): String {
    val title = "${
        if (isDataAvailable) "${stringResource(R.string.detail)} "
        else "${stringResource(R.string.tambah)} "
    }$this"
    return title
}

fun isMobilePhone(): Boolean = false // for real device android