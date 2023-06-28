package com.lanier.rocobgm

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Eric
 * on 2023/6/2
 */

sealed interface PlayDataState {
    object Idle: PlayDataState
    data class PlayData(val data: SceneData): PlayDataState
    data class PlayState(val playing: Boolean): PlayDataState
    data class LoadingState(val loading: Boolean): PlayDataState
    data class PlayContentDuration(val contentDuration: Long): PlayDataState
    data class PlayDuration(val duration: Long): PlayDataState
}

val playStateFlow = MutableStateFlow<PlayDataState>(PlayDataState.Idle)
