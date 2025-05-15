package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme // Material 3 사용 기준
import androidx.compose.material3.Text          // Material 3 사용 기준
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.daytogether.ui.theme.AnniversaryBoardBackground
import com.example.daytogether.ui.theme.TextPrimary


@OptIn(ExperimentalFoundationApi::class)

@Composable

fun AnniversaryBoard(text: String, modifier: Modifier = Modifier) {

    val isPreview = LocalInspectionMode.current // Preview 모드인지 확인



    Text(

        text = text,

        modifier = modifier

            .fillMaxWidth()

            .background(AnniversaryBoardBackground)

            .padding(vertical = 10.dp)

            .basicMarquee(

// Preview 중일 때는 반복 횟수를 5로 제한, 실제 앱에서는 무한 반복

                iterations = if (isPreview) 5 else Int.MAX_VALUE

            ),

        textAlign = TextAlign.Center,

        style = MaterialTheme.typography.bodyMedium.copy(

            fontWeight = FontWeight.Bold,

            color = TextPrimary

        )

    )

}