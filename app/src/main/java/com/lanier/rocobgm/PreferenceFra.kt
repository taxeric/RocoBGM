package com.lanier.rocobgm

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created by Eric
 * on 2023/6/7
 */
class PreferenceFra(
    override val layoutId: Int = R.layout.fra_preference
) : BaseFra() {

    private lateinit var composePreference: ComposeView

    override fun initView(view: View) {
        composePreference = view.findViewById(R.id.composePreference)
        composePreference.setContent {  }
    }
}

@Composable
private fun MainView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        CachePath {
        }
    }
}

@Composable
private fun CachePath(
    onClick: () -> Unit
) {
    PreferenceItemTitle(title = "缓存路径")
    SingleKV(key = "设定缓存路径", value = "aaa", onClick)
}

@Composable
private fun PreferenceItemTitle(
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Composable
private fun SingleKV(
    key: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = rememberRipple()
            ) {
                onClick.invoke()
            }
    ) {
        Text(
            text = key,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Text(
            text = value,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}