package com.lanier.rocobgm

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.rocobgm.compose.ComposeDuration
import com.lanier.rocobgm.datastore.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject

@DelicateCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var localPreferences: AppPreferences

    private val vm by viewModels<MainVM>()

    private val durationFlow = MutableStateFlow(0L)
    private val contentDurationFlow = MutableStateFlow(0L)

    private val ivAvatar by lazy {
        findViewById<ShapeableImageView>(R.id.ivAvatar)
    }
    private val tvTitle by lazy {
        findViewById<TextView>(R.id.tvTitle)
    }
    private val progressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }
    private val ivController by lazy {
        findViewById<ImageView>(R.id.ivController)
    }
    private val composeDuration by lazy {
        findViewById<ComposeView>(R.id.composeDurationBar)
    }
    private val toolbar by lazy {
        findViewById<MaterialToolbar>(R.id.toolbar)
    }
    private val viewPager by lazy {
        findViewById<ViewPager2>(R.id.viewPager)
    }

    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        lifecycleScope.launch {
            CacheConstant.bind(localPreferences.dataFlow.last())
        }

        ivController.setOnClickListener {
            if (isPlaying) {
                playActionFlow.tryEmit(PlayAction.Pause)
            } else {
                playActionFlow.tryEmit(PlayAction.Resume)
            }
        }
        composeDuration.setContent {
            ComposeDuration(
                playDurationFlow = durationFlow,
                maxDurationFlow = contentDurationFlow,
                progressBarStartColor = Color(0xFF97D782),
                progressBarEndColor = Color(0xFF326B24),
                roundBorder = true
            )
        }

        viewPager.adapter = VPAdapter(this)

        vm.lazyInit()

        lifecycleScope.launch {
            playStateFlow.collect {
                when (it) {
                    PlayDataState.Idle -> {}
                    is PlayDataState.LoadingState -> {
                        if (it.loading) {
                            progressBar.visible()
                            ivController.disabled()
                        } else {
                            progressBar.invisible()
                            ivController.enabled()
                        }
                    }
                    is PlayDataState.PlayData -> {
                        tvTitle.text = it.data.sceneName
                        vm.play(it.data)
                    }
                    is PlayDataState.PlayContentDuration -> {
                        contentDurationFlow.tryEmit(it.contentDuration)
                    }
                    is PlayDataState.PlayDuration -> {
                        durationFlow.tryEmit(it.duration)
                    }
                    is PlayDataState.PlayState -> {
                        isPlaying = it.playing
                        if (it.playing) {
                            ivController.setImageResource(R.drawable.baseline_pause_24)
                        } else {
                            ivController.setImageResource(R.drawable.baseline_play_arrow_24)
                        }
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sync) {
            vm.obtainData(assets, true)
        }
        if (item.itemId == R.id.menu_settings) {
            viewPager.currentItem = 1
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        playActionFlow.tryEmit(PlayAction.Release)
    }
}