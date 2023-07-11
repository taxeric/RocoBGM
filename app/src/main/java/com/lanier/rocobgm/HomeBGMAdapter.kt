package com.lanier.rocobgm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by 幻弦让叶
 * on 2023/7/6
 */
class HomeBGMAdapter constructor(
    private val rv: RecyclerView
): RecyclerView.Adapter<HomeBGMAdapter.MainVH>(), View.OnClickListener {

    var onItemClickEventListener: OnItemClickEventListener<SceneData>? = null
    var onFavouriteListener: OnItemFavouriteListener? = null

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
        when (v.id) {
            R.id.singleLayout -> {
                val position = rv.getChildAdapterPosition(v)
                onItemClickEventListener?.onItemClick(_data[position], position)
            }
        }
    }

    inner class MainVH(
        view: View
    ): RecyclerView.ViewHolder(view) {

        val singleLayout: RelativeLayout = view.findViewById(R.id.singleLayout)
        private val title = view.findViewById<TextView>(R.id.tvSceneTitle)
        private val playState = view.findViewById<TextView>(R.id.tvPlayState)
        private val favourite: ImageButton = view.findViewById(R.id.btnFavourite)

        private lateinit var _data: SceneData
        private val modelPosition get() = layoutPosition

        init {
            favourite.setOnClickListener {
                onFavouriteListener?.onFavourite(_data, modelPosition)
            }
        }

        fun bind(data: SceneData) {
            _data = data
            title.text = data.sceneName
            playState.text = "${data.playState}"
            favourite.setImageResource(
                if (data.favourite) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )
        }
    }

    interface OnItemFavouriteListener {
        fun onFavourite(data: SceneData, position: Int)
    }
}