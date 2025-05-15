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
// import androidx.compose.ui.graphics.Color // 사용자 정의 테마 색상을 사용하므로 이 줄은 주석 처리하거나 삭제해도 됩니다.
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.daytogether.ui.theme.AnniversaryBoardBackground // 사용자의 테마 색상 import (좋습니다!)
import com.example.daytogether.ui.theme.TextPrimary             // 사용자의 테마 색상 import (좋습니다!)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnniversaryBoard(text: String, modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current // Preview 모드인지 확인

    // 텍스트가 짧더라도 스크롤되도록 내용을 길게 만듭니다.
    // 텍스트 반복 횟수와 구분자(공백)는 원하는 모양에 맞게 조절하세요.
    val repetitions = 3 // 텍스트가 스크롤 영역에 나타나는 횟수
    val separator = "     " // 텍스트 반복 사이의 공백 (예: 5칸)

    val wideDisplayText = remember(text, repetitions, separator) {
        if (text.isEmpty()) {
            " " // 원본 텍스트가 비어있으면, Marquee가 문제 일으키지 않도록 공백 하나를 줍니다.
        } else {
            // text 문자열을 separator를 중간에 두고 repetitions 횟수만큼 반복합니다.
            List(repetitions) { text }.joinToString(separator)
        }
    }

    Text(
        text = wideDisplayText, // <<-- 여기! 길게 만들어진 텍스트를 사용합니다.
        modifier = modifier
            .fillMaxWidth()
            .background(AnniversaryBoardBackground) // 사용자의 테마 색상 사용
            .padding(vertical = 10.dp)
            .basicMarquee(
                iterations = if (isPreview) 5 else Int.MAX_VALUE
                // 속도나 딜레이 조절이 필요하면 여기에 파라미터를 추가할 수 있습니다.
                // velocity = 30.dp,
                // initialDelayMillis = 0,
                // delayMillis = 0
            ),
        textAlign = TextAlign.Start, // <<-- 여기! 스크롤 텍스트에는 Start 정렬이 더 적합합니다.
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TextPrimary // 사용자의 테마 색상 사용
        ),
        maxLines = 1,       // <<-- 여기! Marquee를 위해 한 줄로 제한합니다.
        softWrap = false    // <<-- 여기! Marquee를 위해 줄바꿈을 비활성화합니다.
    )
}