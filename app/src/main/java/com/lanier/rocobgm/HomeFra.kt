package com.lanier.rocobgm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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
                }
            }
        }
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
        holder.itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val position = rv.getChildAdapterPosition(v)
        listener?.onItemClick(_data[position], position)
    }
}

class MainVH(
    view: View
): RecyclerView.ViewHolder(view) {

    private val title = view.findViewById<TextView>(R.id.tvTitle)
    private val state = view.findViewById<TextView>(R.id.tvState)

    fun bind(data: SceneData) {
        title.text = data.sceneName
        state.text = "${data.playState}"
    }
}