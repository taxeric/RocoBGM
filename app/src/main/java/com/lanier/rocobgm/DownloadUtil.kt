package com.lanier.rocobgm

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.File

/**
 * Created by Eric
 * on 2023/6/1
 */
sealed interface DownloadStatus{
    object Idle: DownloadStatus
    data class Progress(val percent: Int): DownloadStatus
    data class Complete(val success: Boolean, val file: File?): DownloadStatus
}

/**
 * 下载到指定目录,不会检查路径是否存在
 */
fun ResponseBody.downloadFileWithProgress(
    outputFile: File,
    onFailure: (Throwable) -> Unit = {}
): Flow<DownloadStatus> = flow {
    emit(DownloadStatus.Progress(0))
    var success: Boolean
    kotlin.runCatching {
        byteStream().use { inputStream ->
            outputFile.outputStream().use { outputStream ->
                val totalBytes = contentLength()
                val buffer = ByteArray(4 * 1024)
                var progressBytes = 0L
                while (true) {
                    val byteCount = inputStream.read(buffer)
                    if (byteCount == -1) break
                    outputStream.channel
                    outputStream.write(buffer, 0, byteCount)
                    progressBytes += byteCount
                    val percent = ((progressBytes * 100) / totalBytes).toInt()
                    emit(DownloadStatus.Progress(percent))
                }
                when {
                    progressBytes < totalBytes -> {
                        success = false
                        onFailure(Throwable("download failed -> missing bytes"))
                    }
                    progressBytes > totalBytes -> {
                        success = false
                        onFailure(Throwable("download failed -> too many bytes"))
                    }
                    else -> success = true
                }
            }
            emit(DownloadStatus.Complete(success, outputFile))
        }
    }.onFailure {
        onFailure(it)
    }
}.flowOn(Dispatchers.IO).distinctUntilChanged()

/**
 * 通过uri下载到公共目录
 */
fun ResponseBody.downloadFileWithProgress2(
    context: Context,
    uri: Uri,
    onFailure: (Throwable) -> Unit = {}
): Flow<DownloadStatus> = flow {
    emit(DownloadStatus.Progress(0))
    var success: Boolean
    kotlin.runCatching {
        context.contentResolver
            .openOutputStream(uri)
            .use { outputStream ->
                byteStream().use { inputStream ->
                    val totalBytes = contentLength()
                    val buffer = ByteArray(4 * 1024)
                    var progressBytes = 0L
                    while (true) {
                        val byteCount = inputStream.read(buffer)
                        if (byteCount == -1) break
                        outputStream?.write(buffer, 0, byteCount)
                        progressBytes += byteCount
                        val percent = ((progressBytes * 100) / totalBytes).toInt()
                        emit(DownloadStatus.Progress(percent))
                    }
                    when {
                        progressBytes < totalBytes -> {
                            success = false
                            onFailure(Throwable("download failed -> missing bytes"))
                        }
                        progressBytes > totalBytes -> {
                            success = false
                            onFailure(Throwable("download failed -> too many bytes"))
                        }
                        else -> success = true
                    }
                }
            }
        emit(DownloadStatus.Complete(success, null))
    }.onFailure {
        onFailure(it)
    }
}.flowOn(Dispatchers.IO).distinctUntilChanged()