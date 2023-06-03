package com.lanier.rocobgm

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.lanier.rocobgm.repository.RoomHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Eric
 * on 2023/6/1
 */
class MainVM: ViewModel() {

    private val _sceneFlow = MutableStateFlow(emptyList<SceneData>())
    val sceneFlow: StateFlow<List<SceneData>> = _sceneFlow.asStateFlow()

    fun obtainData(assets: AssetManager) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                RoomHelper.getAllSceneData()
            }
            if (data.isEmpty()) {
                val gson = Gson()
                val list = mutableListOf<SceneData>()
                withContext(Dispatchers.IO) {
                    assets.open("bgm.json").use { ism ->
                        val str = ism.readBytes().decodeToString()
                        val entity = gson.fromJson(str, SceneEntity::class.java)
                        entity.data.forEach { item ->
                            list.addAll(item.scene)
                        }
                    }
                }
                withContext(Dispatchers.IO) {
                    RoomHelper.insertSceneData(*list.toTypedArray())
                }
                _sceneFlow.tryEmit(list)
            } else {
                _sceneFlow.tryEmit(data)
            }
        }
    }
}