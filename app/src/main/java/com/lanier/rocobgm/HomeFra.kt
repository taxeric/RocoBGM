package com.lanier.rocobgm

import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

/**
 * Created by Eric
 * on 2023/6/1
 */
@DelicateCoroutinesApi
class HomeFra(
    override val layoutId: Int = R.layout.fra_home
) : BaseFra() {

    private val vm by activityViewModels<MainVM>()

    private lateinit var rv: RecyclerView
    private lateinit var mAdapter: HomeBGMAdapter

    override fun initView(view: View) {
        rv = view.findViewById(R.id.recyclerView)
        mAdapter = HomeBGMAdapter(rv).apply {
            onItemClickEventListener = object : OnItemClickEventListener<SceneData> {
                override fun onItemClick(data: SceneData, position: Int) {
                    val filename = if (CacheConstant.cacheFilename == 0) {
                        val substring = data.bgmUrl.substring(
                            data.bgmUrl.lastIndexOf('/') + 1,
                            data.bgmUrl.length
                        )
                        "$substring.mp3"
                    } else {
                        "${data.sceneName}.mp3"
                    }
                    if (CacheConstant.originalSongPlayMode == 0) {
                        vm.downloadMp3(
                            context = requireContext(),
                            filename = filename,
                            fileUrl = data.bgmUrl,
                            downloadToInternalPath = CacheConstant.cacheFilePath == 0,
                            fileExist = { exist, filepath ->
                                if (exist) {
                                    val mData = if (!data.downloaded && data.path.isEmpty()) {
                                        refreshData(data, position, filepath = filepath)
                                    } else {
                                        data
                                    }
                                    play(mData)
                                } else {
                                    refreshData(data, position, false)
                                }
                            },
                            failure = {
                                Toast.makeText(
                                    requireContext(),
                                    "下载失败 ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            progress = {
                                val mData = data.copy().apply {
                                    downloadProgress = it
                                }
                                mAdapter.notifyItem(mData, position)
                            },
                            downloadComplete = { path ->
                                val mData = refreshData(data, position, filepath = path)
                                play(mData)
                            }
                        )
                    } else {
                        play(data)
                    }
                }
            }

            onFavouriteListener = object : HomeBGMAdapter.OnItemFavouriteListener {
                override fun onFavourite(data: SceneData, position: Int) {
                    if (data.favourite) {
                    } else {
                    }
                    val mData = data.copy(favourite = !data.favourite)
                    mAdapter.notifyItem(mData, position)
                }
            }
        }
        rv.adapter = mAdapter
    }

    private fun refreshData(
        data: SceneData,
        position: Int,
        downloaded: Boolean = true,
        filepath: String = "",
        uri: String = ""
    ): SceneData {
        val mData = data.copy(downloaded = downloaded, path = filepath, uri = uri)
        mAdapter.notifyItem(mData, position)
        vm.updateSceneData(mData)
        return mData
    }

    private fun play(sceneData: SceneData) {
        playStateFlow.tryEmit(
            PlayDataState.PlayData(sceneData)
        )
    }

    override fun initListener() {
        lifecycleScope.launch {
            vm.sceneFlow.collect {
                mAdapter.data = it
            }
        }

        vm.obtainData(requireContext().assets)
    }
}