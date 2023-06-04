package com.lanier.rocobgm

/**
 * Created by Eric
 * on 2023/6/2
 */
sealed interface PlayAction {

    data class Play(val sceneData: SceneData): PlayAction
    object Pause: PlayAction
    object Resume: PlayAction
    object Stop: PlayAction
}