package com.lanier.rocobgm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Eric
 * on 2023/6/1
 */
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
                    val internalFile = obtainDownloadFile(
                        requireContext(),
                        "bgm",
                        data.bgmUrl.substring(
                            data.bgmUrl.lastIndexOf('/') + 1,
                            data.bgmUrl.length
                        )
                    )
                    if (!internalFile.exists()) {
                        refreshData(data, position, false)
                        lifecycleScope.launch {
                            val responseBody = withContext(Dispatchers.Default) {
                                getResponseBody(data.bgmUrl) {
                                    println(">>>> connect err ${it.message}")
                                }
                            }
                            responseBody?.downloadFileWithProgress(internalFile) {
                                println(">>>> error ${it.message}")
                            }?.collect {
                                if (it is DownloadStatus.Progress) {
                                    println(">>>> ${it.percent}")
                                }
                                if (it is DownloadStatus.Complete) {
                                    println(">>>> download complete")
                                    refreshData(data, position, filepath = internalFile.absolutePath)
                                }
                            }
                        }
                    } else {
                        if (!data.downloaded && data.path.isEmpty()) {
                            refreshData(data, position, filepath = internalFile.absolutePath)
                        }
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
    ) {
        val mData = data.copy(downloaded = downloaded, path = filepath, uri = uri)
        mAdapter.notifyItem(mData, position)
        vm.updateSceneData(mData)
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