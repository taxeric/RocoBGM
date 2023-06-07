package com.lanier.rocobgm

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

/**
 * Created by Eric
 * on 2023/6/2
 */
class SongEnvironment @Inject constructor(): IPlayEvent {

    private lateinit var exoPlayer : ExoPlayer
    val playSceneData = MutableStateFlow(SceneData.default)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _contentDuration = MutableStateFlow(0L)
    val contentDuration: StateFlow<Long> = _contentDuration.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _playing = MutableStateFlow(false)
    val playing: StateFlow<Boolean> = _playing.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState: StateFlow<Int> = _playbackState.asStateFlow()

    override fun init(context: Context) {
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
            if (!isLoading) {
//                _contentDuration.tryEmit(exoPlayer.contentDuration)
//                if (playSceneData.value.duration <= 0L) {
//                    playSceneData.tryEmit(
//                        playSceneData.value.copy(duration = exoPlayer.contentDuration)
//                    )
//                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playing.tryEmit(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                exoPlayer.seekTo(0)
                exoPlayer.pause()
            }
            _playbackState.tryEmit(playbackState)
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
//            val percent = newPosition.positionMs / playSceneData.value.duration.toFloat()
//            _duration.tryEmit((percent * 100).toInt())
        }

        override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
            super.onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs)
        }
    }

    @Deprecated("..")
    override fun prepare(data: SceneData) {
        exoPlayer.setMediaItem(MediaItem.fromUri(data.uri))
        exoPlayer.prepare()
    }

    override fun play(data: SceneData) {
        if (data.path.isEmpty() && data.uri.isEmpty()) {
            return
        }
        if (data.sceneId == playSceneData.value.sceneId) {
            return
        }
        playSceneData.tryEmit(data)
        val uri = File(data.path).toUri()
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
//        exoPlayer.setMediaItem(MediaItem.fromUri(data.bgmUrl))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    override fun resume() {
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun release() {
        exoPlayer.removeListener(listener)
        exoPlayer.release()
    }
}