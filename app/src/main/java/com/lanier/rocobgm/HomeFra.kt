package com.lanier.rocobgm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
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
    private lateinit var mAdapter: MainAdapter

    override fun initView(view: View) {
        rv = view.findViewById(R.id.recyclerView)
        mAdapter = MainAdapter(rv).apply {
            listener = object : OnItemClickEventListener<SceneData> {
                override fun onItemClick(data: SceneData, position: Int) {
                    println(">> $position ${data.sceneName}")
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

class MainAdapter(
    private val rv: RecyclerView
): RecyclerView.Adapter<MainVH>(), View.OnClickListener {

    var listener: OnItemClickEventListener<SceneData>? = null

    private val _data = mutableListOf<SceneData>()
    var data: List<SceneData>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    fun notifyItem(data: SceneData, position: Int) {
        _data[position] = data
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainVH {
        return MainVH(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_rv_song, parent, false)
        )
    }

    override fun getItemCount() = _data.size

    override fun onBindViewHolder(holder: MainVH, position: Int) {
        holder.bind(_data[position])
        holder.singleLayout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val position = rv.getChildAdapterPosition(v)
        listener?.onItemClick(_data[position], position)
    }
}

class MainVH(
    view: View
): RecyclerView.ViewHolder(view) {

    val singleLayout: RelativeLayout = view.findViewById(R.id.singleLayout)
    private val title = view.findViewById<TextView>(R.id.tvSceneTitle)
    private val playState = view.findViewById<TextView>(R.id.tvPlayState)
    private val downloadState = view.findViewById<TextView>(R.id.tvDownloadState)

    fun bind(data: SceneData) {
        title.text = data.sceneName
        downloadState.text = if (data.downloaded) {"ok"} else "${data.downloadState}"
        playState.text = "${data.playState}"
    }
}