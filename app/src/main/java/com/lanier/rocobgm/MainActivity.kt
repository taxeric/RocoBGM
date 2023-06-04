package com.lanier.rocobgm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var environment: IPlayEvent

    private val ivAvatar by lazy {
        findViewById<ShapeableImageView>(R.id.ivAvatar)
    }
    private val tvTitle by lazy {
        findViewById<TextView>(R.id.tvTitle)
    }
    private val ivController by lazy {
        findViewById<ImageView>(R.id.ivController)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        environment.init(this)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frameLayout, HomeFra(), "home")
            .commit()

        ivController.setOnClickListener {
        }

        lifecycleScope.launch {
            playStateFlow.collect {
                tvTitle.text = it.curPlaySong.sceneName
            }
        }
    }
}