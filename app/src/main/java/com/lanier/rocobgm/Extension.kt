package com.lanier.rocobgm

import android.view.View

/**
 * Created by Eric
 * on 2023/6/4
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.enabled() {
    isEnabled = true
}

fun View.disabled() {
    isEnabled = false
}
