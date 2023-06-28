package com.lanier.rocobgm

import android.content.Context

/**
 * Created by Eric
 * on 2023/6/2
 */
interface IPlayEvent {

    /**
     * 初始化
     */
    fun init()

    /**
     * 播放
     */
    fun play(data: SceneData)

    /**
     * 跳转
     * @param duration 进度
     * @param autoIncrement 是否是自动增长,如果为否需要exoplayer调用seekTo方法
     */
    fun snapTo(duration: Long, autoIncrement: Boolean = true)

    /**
     * 暂停
     */
    fun pause()

    /**
     * 继续
     */
    fun resume()

    /**
     * 停止
     */
    fun stop()

    /**
     * 资源释放
     */
    fun release()
}