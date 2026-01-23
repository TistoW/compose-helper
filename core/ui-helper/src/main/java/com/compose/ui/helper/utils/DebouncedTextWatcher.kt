package com.compose.ui.helper.utils

import android.os.Handler
import android.os.Looper

class DebouncedTextWatcher(
    private val delay: Long = 750L,
    private val onDebounce: (String) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var lastText = ""
    private var lastEditTime: Long = 0

    private val runnable = Runnable {
        if (System.currentTimeMillis() > lastEditTime + delay - 500) {
            onDebounce(lastText)
        }
    }

    fun onTextChanged(newText: String) {
        lastText = newText
        handler.removeCallbacks(runnable)
        if (newText.isNotEmpty()) {
            lastEditTime = System.currentTimeMillis()
            handler.postDelayed(runnable, delay)
        } else {
            onDebounce(newText)
        }
    }

    fun cancel() {
        handler.removeCallbacks(runnable)
    }
}