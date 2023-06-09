package com.lanier.rocobgm.repository

import com.lanier.rocobgm.SceneData

/**
 * Created by Eric
 * on 2023/6/1
 */
object RoomHelper {

    val dao = SceneDatabase.db.sceneDao()

    suspend fun getAllSceneData() = dao.queryAllSceneSongs()

    suspend fun insertSceneData(vararg data: SceneData) {
        dao.insertSceneSongs(*data)
    }

    suspend fun updateSceneData(sceneData: SceneData) {
        dao.updateSceneSong(sceneData)
    }

    suspend fun deleteAllSceneData() {
        dao.deleteAll()
    }
}