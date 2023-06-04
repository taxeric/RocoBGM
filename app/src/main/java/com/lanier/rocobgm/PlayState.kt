package com.lanier.rocobgm

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Eric
 * on 2023/6/2
 */
data class PlaySongState(
    val playList: List<SceneData> = listOf(),
    val curPlaySong: SceneData = SceneData.default,
    val isPlaying: Boolean = false,
    val curDuration: Long = 0L,
) {
    companion object {
        val default = PlaySongState()
    }
}

val playStateFlow = MutableStateFlow(PlaySongState.default)
