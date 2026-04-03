package com.tisto.helper.core.helper.utils

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.MutableSharedFlow

object MenuEventBus {
    val trigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    // add new one for vouchers
    val fragmentEvents = MutableSharedFlow<Fragment>(extraBufferCapacity = 1)
}