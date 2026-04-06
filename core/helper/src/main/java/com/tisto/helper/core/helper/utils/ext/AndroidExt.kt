package com.tisto.helper.core.helper.utils.ext

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresPermission
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import java.util.HashMap


fun <T> T.toMap(): Map<String, String> {
    val map: Map<String, String> = HashMap()
    return Gson().fromJson(this.toJson(), map.javaClass)
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isOffline(): Boolean {
    return !isOnline()
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Fragment.isOffline(): Boolean {
    return !requireActivity().isOnline()
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Fragment.isOnline(): Boolean {
    return requireActivity().isOnline()
}

fun String.toQRCode(dimensions: Int): Bitmap {
    val bitMatrix = QRCodeWriter().encode(this, BarcodeFormat.QR_CODE, dimensions, dimensions)
    val bitmap = createBitmap(dimensions, dimensions, Bitmap.Config.RGB_565)
    for (x in 0 until dimensions) {
        for (y in 0 until dimensions) {
            bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    return bitmap
}

fun Context.setToolbar(view: Toolbar, title: String) {
    (this as AppCompatActivity).setSupportActionBar(view)
    this.supportActionBar!!.title = title
    this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
}

fun Context.verticalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(this)
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    return layoutManager
}

fun Fragment.verticalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(requireActivity())
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    return layoutManager
}

fun Context.horizontalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(this)
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    return layoutManager
}

fun Fragment.horizontalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(requireActivity())
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    return layoutManager
}

fun Fragment.hideKeyboard() {
    val imm: InputMethodManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
}
