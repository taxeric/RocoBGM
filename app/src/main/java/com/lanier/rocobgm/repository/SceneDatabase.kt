package com.lanier.rocobgm.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lanier.rocobgm.SceneData

/**
 * Created by Eric
 * on 2023/6/1
 */
@Database(
    entities = [
        SceneData::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class SceneDatabase: RoomDatabase() {

    abstract fun sceneDao(): SceneDao

    companion object {

        private const val DB_NAME = "roco_bgm.db"

        @Volatile
        lateinit var db: SceneDatabase

        fun init(context: Context) {
            if (::db.isInitialized) {
                return
            }
            synchronized(this) {
                db = Room
                    .databaseBuilder(context, SceneDatabase::class.java, DB_NAME)
                    .build()
            }
        }
    }
}