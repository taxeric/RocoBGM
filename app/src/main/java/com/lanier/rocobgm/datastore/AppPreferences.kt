package com.lanier.rocobgm.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lanier.rocobgm.CacheConstantEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Created by Eric
 * on 2023/6/12
 */
class AppPreferences @Inject constructor(
    private val context: Context
) {

    companion object{
        private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "local_preferences"
        )

        //源文件播放形式
        private const val P_K_PLAY_ORIGINAL = "p_k_play_original"
        //播放模式
        private const val P_K_PLAY_MODE = "p_k_play_mode"

        //缓存路径
        private const val P_K_CACHE_PATH = "p_k_cache_path"
        //缓存文件名类型
        private const val P_K_CACHE_FILENAME_TYPE = "p_k_cache_filename_type"

        private val KeyPlayOriginal = intPreferencesKey(P_K_PLAY_ORIGINAL)
        private val KeyPlaybackMode = intPreferencesKey(P_K_PLAY_MODE)
        private val KeyCachePath = intPreferencesKey(P_K_CACHE_PATH)
        private val KeyCacheFilenameType = intPreferencesKey(P_K_CACHE_FILENAME_TYPE)
    }

    val data = context.appDataStore
        .data

    val dataFlow = data
        .map {
            CacheConstantEntity(
                originalSongPlayMode = it[KeyPlayOriginal]?: 0,
                playMode = it[KeyPlaybackMode]?: 0,
                cacheFilePath = it[KeyCachePath]?: 0,
                cacheFilename = it[KeyCacheFilenameType]?: 0
            )
        }

    suspend fun getPlayOriginal(): Int {
        return data.first()[KeyPlayOriginal]?: 0
    }

    val playOriginalFlow = context.appDataStore.data.map {
        it[KeyPlayOriginal]?: 0
    }

    suspend fun updatePlayOriginal(value: Int) {
        context.appDataStore
            .edit {
                it[KeyPlayOriginal] = value
            }
    }

    suspend fun getPlaybackMode(): Int {
        return data.first()[KeyPlaybackMode]?: 0
    }

    val playbackModeFlow = context.appDataStore.data.map {
        it[KeyPlaybackMode]?: 0
    }

    suspend fun updatePlayMode(value: Int) {
        context.appDataStore
            .edit {
                it[KeyPlaybackMode] = value
            }
    }

    suspend fun getCachePath(): Int {
        return data.first()[KeyCachePath]?: 0
    }

    val cachePathFlow = context.appDataStore.data.map {
        it[KeyCachePath]?: 0
    }

    suspend fun updateCachePath(value: Int) {
        context.appDataStore
            .edit {
                it[KeyCachePath] = value
            }
    }

    suspend fun getCacheFilenameType(): Int {
        return data.first()[KeyCacheFilenameType]?: 0
    }

    val cacheFilenameTypeFlow = context.appDataStore.data.map {
        it[KeyCacheFilenameType]?: 0
    }

    suspend fun updateCacheFilename(value: Int) {
        context.appDataStore
            .edit {
                it[KeyCacheFilenameType] = value
            }
    }
}