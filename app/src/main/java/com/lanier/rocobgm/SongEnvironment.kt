package com.lanier.rocobgm

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

/**
 * Created by Eric
 * on 2023/6/2
 */
class SongEnvironment @Inject constructor(): IPlayEvent {

    private lateinit var exoPlayer : ExoPlayer

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
            println(">>>> loading $isLoading")
            if (!isLoading) {
                println(">>>> ${exoPlayer.duration}")
                println(">>>> ${exoPlayer.contentDuration}")
                println(">>>> ${exoPlayer.totalBufferedDuration}")
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
        }
    }

    override fun prepare(data: SceneData) {
        exoPlayer.setMediaItem(MediaItem.fromUri(data.uri))
        exoPlayer.prepare()
    }

    override fun play(data: SceneData) {
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun resume() {
        exoPlayer.play()
    }

    override fun stop() {
        exoPlayer.stop()
    }
}