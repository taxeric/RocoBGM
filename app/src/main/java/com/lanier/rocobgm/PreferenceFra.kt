package com.lanier.rocobgm

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lanier.rocobgm.compose.CommonListDialog
import com.lanier.rocobgm.compose.ExtendTheme
import com.lanier.rocobgm.datastore.AppPreferences
import com.lanier.rocobgm.datastore.CacheFilePath
import com.lanier.rocobgm.datastore.CacheFilenameType
import com.lanier.rocobgm.datastore.PlaybackMode
import com.lanier.rocobgm.datastore.PlayOriginal
import kotlinx.coroutines.launch

/**
 * Created by Eric
 * on 2023/6/7
 */
class PreferenceFra(
    override val layoutId: Int = R.layout.fra_preference
) : BaseFra() {

    private lateinit var composePreference: ComposeView
    private lateinit var appPreferences: AppPreferences

    override fun initView(view: View) {
        appPreferences = AppPreferences(requireContext())
        composePreference = view.findViewById(R.id.composePreference)
        composePreference.setContent {
            ExtendTheme {
                MainView(appPreferences = appPreferences)
            }
        }
    }
}

@Composable
private fun MainView(
    modifier: Modifier = Modifier,
    appPreferences: AppPreferences,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        PlayPreferenceGroup(appPreferences)
        CacheGroup(appPreferences)
    }
}

@Composable
private fun PlayPreferenceGroup(
    appPreferences: AppPreferences
) {
    PreferenceGroup(groupTitle = "播放") {
        PlayOriginalItem(appPreferences)
        PlaybackSongsMode(appPreferences)
    }
}

@Composable
private fun CacheGroup(
    appPreferences: AppPreferences
) {
    PreferenceGroup(groupTitle = "缓存") {
        CacheSongsPath(appPreferences = appPreferences)
        CacheSongsFilename(appPreferences = appPreferences)
    }
}

/**
 * 源文件播放形式
 * 直接播放or缓存到本地后播放
 */
@Composable
private fun PlayOriginalItem(
    appPreferences: AppPreferences
) {
    val scope = rememberCoroutineScope()
    var modify by remember {
        mutableStateOf(false)
    }
    var updateKey by remember {
        mutableStateOf(0)
    }
    val playOriginal = produceState(
        initialValue = 0,
        producer = {
            value = appPreferences.getPlayOriginal()
        },
        key1 = updateKey
    ).value
    val tips = remember {
        when (PlayOriginal.values()[playOriginal]) {
            PlayOriginal.PlayAfterDownload -> PlayOriginal.PlayAfterDownload.desc
            PlayOriginal.PlayDirect -> PlayOriginal.PlayDirect.desc
        }
    }
    SingleKV(key = "源文件", value = tips) {
        modify = !modify
    }
    if (modify) {
        val list = mutableListOf<String>()
        PlayOriginal.values().forEach {
            list.add(it.desc)
        }
        CommonListDialog(
            desc = "选择",
            list = list,
            defaultSelectedIndex = playOriginal
        ) {
            if (it != -1 && it != playOriginal) {
                scope.launch {
                    appPreferences.updatePlayOriginal(it)
                    updateKey ++
                }
            }
            modify = !modify
        }
    }
}

/**
 * 播放模式
 * 重置进度or重置进度并暂停
 */
@Composable
private fun PlaybackSongsMode(
    appPreferences: AppPreferences
) {
    val scope = rememberCoroutineScope()
    var modify by remember {
        mutableStateOf(false)
    }
    var updateKey by remember {
        mutableStateOf(0)
    }
    val playbackOriginal = produceState(
        initialValue = 0,
        producer = {
            value = appPreferences.getPlaybackMode()
        },
        key1 = updateKey
    ).value
    val tips = remember {
        when (PlaybackMode.values()[playbackOriginal]) {
            PlaybackMode.ResetDuration -> PlaybackMode.ResetDuration.desc
            PlaybackMode.ResetDurationAndPause -> PlaybackMode.ResetDurationAndPause.desc
        }
    }
    SingleKV(key = "播放模式", value = tips) {
        modify = !modify
    }
    if (modify) {
        val list = mutableListOf<String>()
        PlaybackMode.values().forEach {
            list.add(it.desc)
        }
        CommonListDialog(
            desc = "选择",
            list = list,
            defaultSelectedIndex = playbackOriginal
        ) {
            if (it != -1 && it != playbackOriginal) {
                scope.launch {
                    appPreferences.updatePlayMode(it)
                    updateKey ++
                }
            }
            modify = !modify
        }
    }
}

/**
 * 缓存路径
 * 内部路径or外部公共目录
 */
@Composable
private fun CacheSongsPath(
    appPreferences: AppPreferences
) {
    val scope = rememberCoroutineScope()
    var modify by remember {
        mutableStateOf(false)
    }
    var updateKey by remember {
        mutableStateOf(0)
    }
    val mOriginal = produceState(
        initialValue = 0,
        producer = {
            value = appPreferences.getCachePath()
        },
        key1 = updateKey
    ).value
    val tips = remember {
        when (CacheFilePath.values()[mOriginal]) {
            CacheFilePath.InternalPath -> CacheFilePath.InternalPath.desc
            CacheFilePath.ExternalPath -> CacheFilePath.ExternalPath.desc
        }
    }
    SingleKV(key = "缓存路径", value = tips) {
        modify = !modify
    }
    if (modify) {
        val list = mutableListOf<String>()
        CacheFilePath.values().forEach {
            list.add(it.desc)
        }
        CommonListDialog(
            desc = "选择",
            list = list,
            defaultSelectedIndex = mOriginal
        ) {
            if (it != -1 && it != mOriginal) {
                scope.launch {
                    appPreferences.updateCachePath(it)
                    updateKey ++
                }
            }
            modify = !modify
        }
    }
}

/**
 * 缓存文件名
 * 依据编号or文件名
 */
@Composable
private fun CacheSongsFilename(
    appPreferences: AppPreferences
) {
    val scope = rememberCoroutineScope()
    var modify by remember {
        mutableStateOf(false)
    }
    var updateKey by remember {
        mutableStateOf(0)
    }
    val mOriginal = produceState(
        initialValue = 0,
        producer = {
            value = appPreferences.getCacheFilenameType()
        },
        key1 = updateKey
    ).value
    val tips = remember {
        when (CacheFilenameType.values()[mOriginal]) {
            CacheFilenameType.AccordingToId -> CacheFilenameType.AccordingToId.desc
            CacheFilenameType.AccordingToFilename -> CacheFilenameType.AccordingToFilename.desc
        }
    }
    SingleKV(key = "缓存文件名", value = tips) {
        modify = !modify
    }
    if (modify) {
        val list = mutableListOf<String>()
        CacheFilenameType.values().forEach {
            list.add(it.desc)
        }
        CommonListDialog(
            desc = "选择",
            list = list,
            defaultSelectedIndex = mOriginal
        ) {
            if (it != -1 && it != mOriginal) {
                scope.launch {
                    appPreferences.updateCacheFilename(it)
                    updateKey ++
                }
            }
            modify = !modify
        }
    }
}

@Composable
private fun PreferenceGroup(
    groupTitle: String,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    PreferenceItemTitle(title = groupTitle)
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        content.invoke(this)
    }
}

@Composable
private fun PreferenceItemTitle(
    title: String,
    titleColor: Color = Color(0xFFB7D1AA)
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            color = titleColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
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
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = key,
            color = ExtendTheme.colors.commonTextColor,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Text(
            text = value,
            color = ExtendTheme.colors.commonTextColor1,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}