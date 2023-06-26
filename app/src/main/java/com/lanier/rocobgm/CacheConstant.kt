package com.lanier.rocobgm

/**
 * Created by 幻弦让叶
 * on 2023/6/26
 */
data class CacheConstantEntity(
    val originalSongPlayMode: Int,
    val playMode: Int,
    val cacheFilePath: Int,
    val cacheFilename: Int,
)

object CacheConstant {

    var originalSongPlayMode = 0
    var playMode = 0
    var cacheFilePath = 0
    var cacheFilename = 0

    fun bind(entity: CacheConstantEntity) {
        originalSongPlayMode = entity.originalSongPlayMode
        playMode = entity.playMode
        cacheFilePath = entity.cacheFilePath
        cacheFilename = entity.cacheFilename
    }
}
