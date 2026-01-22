package com.compose.ui.helper.utils

import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, device = PHONE)
annotation class MobilePreview


@Preview(showBackground = true, device = TABLET)
annotation class TabletPreview

