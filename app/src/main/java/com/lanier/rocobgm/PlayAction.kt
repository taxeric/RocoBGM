package com.lanier.rocobgm

import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Created by Eric
 * on 2023/6/2
 */
sealed interface PlayAction {

    object Idle: PlayAction
    object Pause: PlayAction
    object Resume: PlayAction
    object Stop: PlayAction
    object Release: PlayAction
}

val playActionFlow = MutableSharedFlow<PlayAction>(extraBufferCapacity = 1)