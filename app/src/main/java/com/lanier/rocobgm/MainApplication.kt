package com.lanier.rocobgm

import android.app.Application
import com.lanier.rocobgm.repository.SceneDatabase
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Eric
 * on 2023/6/1
 */
@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initClient()
        SceneDatabase.init(this)
    }
}