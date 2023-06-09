package com.lanier.rocobgm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Eric
 * on 2023/6/9
 */
class VPAdapter(
    act: FragmentActivity
): FragmentStateAdapter(act) {

    private val fragments = mutableListOf<Fragment>().apply {
        add(HomeFra())
        add(PreferenceFra())
    }

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}