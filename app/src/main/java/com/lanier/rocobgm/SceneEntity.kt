package com.lanier.rocobgm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by Eric
 * on 2023/6/1
 */
data class SceneEntity(
    val `data`: List<SceneParentData> = listOf(),
    val totalItem: Int = 0
)

data class SceneParentData(
    val parentScene: String = "",
    val parentSceneId: Int = 0,
    val scene: List<SceneData> = listOf(),
    val totalScene: Int = 0
)

@Entity
data class SceneData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val bgmId: Int = 0,
    @ColumnInfo val bgmUrl: String = "",
    @ColumnInfo val sceneId: Int = 0,
    @ColumnInfo val sceneName: String = "",
    @ColumnInfo val duration: Long = 0L,
    @ColumnInfo val favourite: Boolean = false,
    @ColumnInfo val path: String = "",
    @ColumnInfo val uri: String = "",
    @ColumnInfo val downloaded: Boolean = false,
) {
    @Ignore var downloadState: Int = Download
    @Ignore var selected: Boolean = false
    @Ignore var playState: Int = NoPlay

    companion object {
        const val Download = 0
        const val Downloading = 1
        const val Downloaded = 2

        const val NoPlay = 0
        const val Playing = 1

        val default = SceneData(
            sceneName = "unknown"
        )
    }
}
