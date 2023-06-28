package com.lanier.rocobgm.datastore

/**
 * Created by Eric
 * on 2023/6/12
 */
enum class PlayOriginal(val value: Int, val desc: String) {
    PlayAfterDownload(0, "下载后播放"),
    PlayDirect(1, "直接播放"),
}

enum class PlaybackMode(val value: Int, val desc: String) {
    ResetDuration(0, "重置进度"),
    ResetDurationAndPause(1, "重置进度并暂停"),
}

enum class CacheFilePath(val value: Int, val desc: String) {
    InternalPath(0, "/Android/data/com.lanier.rocobgm/files/Download/bgm/ (拒绝外部访问)"),
    ExternalPath(1, "/Music/roco/bgm/")
}

enum class CacheFilenameType(val value: Int, val desc: String) {
    AccordingToId(0, "文件编号 (不同场景编号可能一致)"),
    AccordingToFilename(1, "文件名 (即使编号一致也缓存)")
}
