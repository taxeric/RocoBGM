package com.lanier.rocobgm

/**
 * Created by Eric
 * on 2023/6/2
 */
interface OnItemClickEventListener<T> {

    fun onItemClick(data: T, position: Int) {}
    fun onItemLongClick(data: T, position: Int) {}
}