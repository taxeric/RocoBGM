package com.lanier.rocobgm

import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.lanier.rocobgm.repository.RoomHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by Eric
 * on 2023/6/1
 */
@HiltViewModel
@DelicateCoroutinesApi
class MainVM @Inject constructor(
    private val environment: SongEnvironment
): ViewModel() {

    init {
        viewModelScope.launch {
            environment.contentDuration.collect {
                playStateFlow.tryEmit(
                    PlayDataState.PlayContentDuration(it)
                )
                if (environment.playSceneData.value.duration <= 0L) {
                    RoomHelper.updateSceneData(
                        environment.playSceneData.value.copy(
                            duration = it
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            environment.loading.collect {
                playStateFlow.tryEmit(
                    PlayDataState.LoadingState(it)
                )
            }
        }
        viewModelScope.launch {
            environment.playing.collect {
                playStateFlow.tryEmit(
                    PlayDataState.PlayState(it)
                )
            }
        }
        viewModelScope.launch {
            environment.playDuration.collect {
                playStateFlow.tryEmit(
                    PlayDataState.PlayDuration(it)
                )
            }
        }
        viewModelScope.launch {
            environment.playbackState.collect {
            }
        }
        viewModelScope.launch { 
            playActionFlow.collect {
                when (it) {
                    PlayAction.Idle -> {}
                    PlayAction.Pause -> {
                        environment.pause()
                    }
                    PlayAction.Resume -> {
                        environment.resume()
                    }
                    PlayAction.Stop -> {
                        environment.stop()
                    }
                    PlayAction.Release -> {
                        environment.release()
                    }
                }
            }
        }
    }

    private val _sceneFlow = MutableStateFlow(emptyList<SceneData>())
    val sceneFlow: StateFlow<List<SceneData>> = _sceneFlow.asStateFlow()

    fun lazyInit() {
        environment.init()
    }

    fun play(sceneData: SceneData) {
        viewModelScope.launch {
            environment.play(sceneData)
        }
    }

    fun obtainData(assets: AssetManager, syncFromAssets: Boolean = false) {
        viewModelScope.launch {
            if (syncFromAssets) {
                val list = obtainDataFromAssets(assets)
                withContext(Dispatchers.IO) {
                    RoomHelper.deleteAllSceneData()
                    RoomHelper.insertSceneData(*list.toTypedArray())
                }
                _sceneFlow.tryEmit(list)
            } else {
                val data = withContext(Dispatchers.IO) {
                    RoomHelper.getAllSceneData()
                }
                if (data.isEmpty()) {
                    val list = obtainDataFromAssets(assets)
                    withContext(Dispatchers.IO) {
                        RoomHelper.insertSceneData(*list.toTypedArray())
                    }
                    _sceneFlow.tryEmit(list)
                } else {
                    _sceneFlow.tryEmit(data)
                }
            }
        }
    }

    private suspend fun obtainDataFromAssets(assets: AssetManager): List<SceneData> {
        val gson = Gson()
        val list = mutableListOf<SceneData>()
        withContext(Dispatchers.IO) {
            assets.open("bgm.json").use { ism ->
                val str = ism.readBytes().decodeToString()
                val entity = gson.fromJson(str, SceneEntity::class.java)
                entity.data.forEach { item ->
                    list.addAll(item.scene)
                }
            }
        }
        return list
    }

    fun updateSceneData(sceneData: SceneData) {
        viewModelScope.launch {
            RoomHelper.updateSceneData(sceneData)
        }
    }

    fun downloadMp3(
        context: Context,
        filename: String,
        fileUrl: String,
        downloadToInternalPath: Boolean = true,
        progress: (Int) -> Unit = {},
        failure: (Throwable) -> Unit = {},
        fileExist: (Boolean, String) -> Unit,
        downloadComplete: (String) -> Unit,
    ) {
        if (downloadToInternalPath) {
            downloadToInternalPath(
                context, filename, fileUrl, fileExist, progress, failure, downloadComplete
            )
        } else {
            downloadToPublicPath(
                context, filename, fileUrl, fileExist, progress, failure, downloadComplete
            )
        }
    }

    private fun downloadToInternalPath(
        context: Context,
        filename: String,
        fileUrl: String,
        fileExist: (Boolean, String) -> Unit,
        progress: (Int) -> Unit = {},
        failure: (Throwable) -> Unit = {},
        downloadComplete: (String) -> Unit,
    ) {
        val internalFile = obtainDownloadFile(
            context,
            "bgm",
            filename
        )
        val exist = internalFile.exists()
        if (!exist) {
            fileExist.invoke(false, "")
            viewModelScope.launch {
                val responseBody = withContext(Dispatchers.Default) {
                    getResponseBody(fileUrl)
                }
                responseBody?.downloadFileWithProgress(internalFile) {
                    println(">>>> error ${it.message}")
                    failure.invoke(it)
                }?.collect {
                    if (it is DownloadStatus.Progress) {
                        println(">>>> ${it.percent}")
                        progress.invoke(it.percent)
                    }
                    if (it is DownloadStatus.Complete) {
                        println(">>>> download complete")
                        downloadComplete.invoke(internalFile.absolutePath)
                    }
                }?: failure.invoke(Throwable("Error"))
            }
        } else {
            fileExist.invoke(true, internalFile.absolutePath)
        }
    }

    private fun downloadToPublicPath(
        context: Context,
        filename: String,
        fileUrl: String,
        fileExist: (Boolean, String) -> Unit,
        progress: (Int) -> Unit = {},
        failure: (Throwable) -> Unit = {},
        downloadComplete: (String) -> Unit,
    ) {
        val publicFile = File(Environment.DIRECTORY_MUSIC, "/bgm/$filename")
        if (!publicFile.exists()) {
            fileExist.invoke(false, "")
            val uriCase = obtainAudioMediaUri(context = context, filename = filename)
            viewModelScope.launch {
                val responseBody = withContext(Dispatchers.Default) {
                    getResponseBody(fileUrl)
                }
                responseBody?.downloadFileWithProgress2(context, uriCase.uri) {
                    println(">>>> error ${it.message}")
                    failure.invoke(it)
                }?.collect {
                    if (it is DownloadStatus.Progress) {
                        println(">>>> ${it.percent}")
                        progress.invoke(it.percent)
                    }
                    if (it is DownloadStatus.Complete) {
                        println(">>>> download complete")
                        downloadComplete.invoke(publicFile.absolutePath)
                    }
                }?: failure.invoke(Throwable("Error"))
            }
        } else {
            fileExist.invoke(true, publicFile.absolutePath)
        }
    }
}