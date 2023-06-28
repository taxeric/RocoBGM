package com.lanier.rocobgm.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Created by 幻弦让叶
 * on 2023/6/12
 */
@Composable
fun CommonListDialog(
    desc: String,
    list: List<String>,
    defaultSelectedIndex: Int,
    onDismissRequest: (Int) -> Unit
){
    var choice by remember {
        mutableStateOf(defaultSelectedIndex)
    }
    Dialog(onDismissRequest = { onDismissRequest.invoke(-1) }) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 24.dp)
        ) {
            list.forEachIndexed { index, value ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = index == defaultSelectedIndex,
                        onClick = { choice = index }
                    )
                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                    )
                    Text(
                        text = value,
                        color = ExtendTheme.colors.commonColor
                    )
                }
                if (index != list.size - 1) {
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .height(18.dp)
            )
            Row(
                modifier = Modifier
                    .align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onDismissRequest.invoke(choice)
                    }
                ) {
                    Text(
                        text = "确定",
                        color = ExtendTheme.colors.commonColor
                    )
                }
            }
        }
    }
}
