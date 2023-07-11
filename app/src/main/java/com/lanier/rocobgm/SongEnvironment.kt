package com.lanier.rocobgm

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lanier.rocobgm.datastore.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * Created by Eric
 * on 2023/6/2
 */
@DelicateCoroutinesApi
class SongEnvironment @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
): IPlayEvent {

    private lateinit var exoPlayer : ExoPlayer
    private val _playSceneData = MutableStateFlow(SceneData.default)
    val playSceneData: StateFlow<SceneData> = _playSceneData.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _contentDuration = MutableStateFlow(0L)
    val contentDuration: StateFlow<Long> = _contentDuration.asStateFlow()

    private val _playDuration = MutableStateFlow(0L)
    val playDuration: StateFlow<Long> = _playDuration.asStateFlow()

    private val _playing = MutableStateFlow(false)
    val playing: StateFlow<Boolean> = _playing.asStateFlow()

    private val _stopped = MutableStateFlow(false)
    private val stopped: StateFlow<Boolean> = _stopped.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState: StateFlow<Int> = _playbackState.asStateFlow()

    private var durationRunnable = Runnable {  }
    private val songHandler = Handler(Looper.getMainLooper())

    private val _playOriginalType = MutableStateFlow(0)
    private val playOriginalType: StateFlow<Int> = _playOriginalType.asStateFlow()

    private val _playbackMode = MutableStateFlow(0)
    private val playbackMode: StateFlow<Int> = _playbackMode.asStateFlow()

    init {
        GlobalScope.launch {
            launch {
                appPreferences.playOriginalFlow.collect {
                    _playOriginalType.tryEmit(it)
                }
            }
            launch {
                appPreferences.playbackModeFlow.collect {
                    _playbackMode.tryEmit(it)
                }
            }
        }
    }

    override fun init() {
        exoPlayer = ExoPlayer
            .Builder(context)
            .build()
            .apply {
                addListener(listener)
            }
    }

    private val listener = object : Player.Listener{
        override fun onIsLoadingChanged(isLoading: Boolean) {
            _loading.tryEmit(isLoading)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playing.tryEmit(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                exoPlayer.seekTo(0)
                if (playbackMode.value == 1) {
                    exoPlayer.pause()
                }
            }
            _playbackState.tryEmit(playbackState)
        }
    }

    override fun play(data: SceneData) {
        if (data.path.isEmpty() && data.bgmUrl.isEmpty()) {
            return
        }
        val uri = File(data.path).toUri()
        if (playOriginalType.value == 0) {
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        } else {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(data.bgmUrl)))
        }
//        exoPlayer.setMediaItem(MediaItem.fromUri(data.bgmUrl))
        exoPlayer.prepare()
        exoPlayer.play()

        _stopped.tryEmit(false)
        _playSceneData.tryEmit(data)

        durationRunnable = Runnable {
            snapTo(
                duration = if (exoPlayer.duration != -1L) exoPlayer.currentPosition else 0L
            )
            if (exoPlayer.contentDuration > 0L) {
                _contentDuration.tryEmit(exoPlayer.contentDuration)
            }
            songHandler.postDelayed(durationRunnable, 1000)
        }
        songHandler.post(durationRunnable)
    }

    override fun snapTo(duration: Long, autoIncrement: Boolean) {
        _playDuration.tryEmit(duration)
        if (!autoIncrement) {
            exoPlayer.seekTo(duration)
        }
    }

    override fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    override fun resume() {
        if (stopped.value && playSceneData.value.path.isNotEmpty()) {
            play(playSceneData.value)
        } else {
            exoPlayer.play()
        }
    }

    override fun stop() {
        exoPlayer.stop()
        _stopped.tryEmit(true)
    }

    override fun release() {
        exoPlayer.removeListener(listener)
        exoPlayer.release()
    }
}