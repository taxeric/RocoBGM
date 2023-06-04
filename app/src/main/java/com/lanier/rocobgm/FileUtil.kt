package com.lanier.rocobgm

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Created by Eric
 * on 2023/6/4
 */
fun getInternalCacheFile(context: Context): File {
    return context.externalCacheDir!!
}

fun obtainDownloadFilePath(context: Context, childPathName: String): File {
    val parentFilePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val childFilePath = File(parentFilePath, childPathName)
    if (!childFilePath.exists()) {
        childFilePath.mkdir()
    }
    return childFilePath
}

fun obtainDownloadFile(context: Context, childPathName: String, childFilename: String): File {
    val realPath = obtainDownloadFilePath(context, childPathName)
    return File(realPath, childFilename)
}
