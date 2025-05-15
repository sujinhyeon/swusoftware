package com.example.daytogether.ui.home.composables

import androidx.compose.ui.tooling.preview.Preview // Preview 사용시
import androidx.compose.runtime.Composable
import com.example.daytogether.ui.theme.DaytogetherTheme // 사용자 정의 테마
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.data.model.CalendarEvent // CalendarEvent 모델 클래스 import
import com.example.daytogether.data.model.WeeklyCalendarDay // WeeklyCalendarDay 모델 클래스 import
import com.example.daytogether.ui.theme.AnniversaryBoardBackground // <<< 수정된 배경색 import (이미 같은 패키지라 자동 인식될 수 있음)
import com.example.daytogether.ui.theme.TextPrimary // TextPrimary 테마 색상 import
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

@Composable
fun DayCellNew(
    dateData: WeeklyCalendarDay,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight()
            // horizontal 패딩을 늘려서 셀 내부의 좌우 여백을 더 줍니다. (예: 4.dp -> 6.dp 또는 8.dp)
            .padding(horizontal = 4.dp, vertical = 4.dp), // vertical 패딩도 유지하거나 조절
        verticalArrangement = Arrangement.Top // 최상단 정렬은 유지
    ) {
        // 날짜(요일) 텍스트와 셀 상단 사이에 약간의 여백을 추가하여 아래로 내립니다.
        Spacer(modifier = Modifier.height(5.dp)) // << 추가: 상단 여백 (원하는 만큼 조절)

        // 상단: "숫자(요일)" 헤더
        val annotatedDateString = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)) {
                append(dateData.date)
            }
            withStyle(style = SpanStyle(fontSize = 10.sp, color = TextPrimary.copy(alpha = 0.7f))) {
                append("(${dateData.dayOfWeek})")
            }
        }
        Text(
            text = annotatedDateString,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp)
        )

        // 가로 구분선
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            thickness = 1.dp,
            color = TextPrimary.copy(alpha = 0.2f)
        )

        // 하단: 이벤트 목록 (최대 4개)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            dateData.events.take(4).forEach { event ->
                Text(
                    text = event.description,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AnniversaryBoardBackground, RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}



