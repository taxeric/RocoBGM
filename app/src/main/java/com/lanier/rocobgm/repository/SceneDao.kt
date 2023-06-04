package com.lanier.rocobgm.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lanier.rocobgm.SceneData

/**
 * Created by Eric
 * on 2023/6/1
 */
@Dao
interface SceneDao {

    @Query("select * from SceneData")
    suspend fun queryAllSceneSongs(): List<SceneData>

    @Insert
    suspend fun insertSceneSongs(vararg sceneData: SceneData)

    @Update
    suspend fun updateSceneSong(sceneData: SceneData)
}