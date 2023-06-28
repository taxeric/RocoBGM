package com.lanier.rocobgm

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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

fun obtainAudioMediaUri(
    context: Context,
    parentPathname: String = "bgm",
    filename: String,
): UriCase {
    val environmentType = Environment.DIRECTORY_MUSIC
    val parentPath = Environment.getExternalStoragePublicDirectory(environmentType).path +
            File.separator + parentPathname + File.separator
    val contentValues = ContentValues()
    contentValues.put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, filename)
    contentValues.put(MediaStore.Audio.AudioColumns.MIME_TYPE, "audio/mp3")
    contentValues.put(MediaStore.Audio.AudioColumns.DATE_ADDED, System.currentTimeMillis())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(
            MediaStore.Audio.AudioColumns.RELATIVE_PATH,
            environmentType + File.separator + parentPathname
        )
    } else {
        contentValues.put(MediaStore.Audio.AudioColumns.DATA, parentPath + filename)
    }
    val newUri = context.contentResolver
        .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
    return UriCase(newUri!!, File(parentPath, filename))
}

data class UriCase(
    val uri: Uri,
    val file: File,
)
