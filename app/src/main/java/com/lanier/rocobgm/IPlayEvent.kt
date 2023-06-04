package com.lanier.rocobgm

import android.content.Context

/**
 * Created by Eric
 * on 2023/6/2
 */
interface IPlayEvent {

    fun init(context: Context)
    fun prepare(data: SceneData)
    fun play(data: SceneData)
    fun pause()
    fun resume()
    fun stop()
}