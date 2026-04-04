package com.tisto.helper.core.helper.utils.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tisto.helper.core.helper.R
import java.util.UUID.randomUUID

@Composable
fun String.title(isDataAvailable: Boolean): String {
    val title = "${
        if (isDataAvailable) "${stringResource(R.string.detail)} "
        else "${stringResource(R.string.tambah)} "
    }$this"
    return title
}

fun isMobilePhone(): Boolean = false // for real device android

fun generateUUID(): String {
    return randomUUID().toString()
}