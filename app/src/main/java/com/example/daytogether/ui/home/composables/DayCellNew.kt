package com.example.daytogether.ui.home.composables

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
import androidx.compose.runtime.Composable
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
import com.example.daytogether.ui.theme.TextPrimary // TextPrimary 테마 색상 import

@Composable
fun DayCellNew(
    dateData: WeeklyCalendarDay,
    modifier: Modifier = Modifier
    // 만약 날짜 셀 클릭 이벤트를 추가하고 싶다면 여기에 onClick: () -> Unit 파라미터를 추가할 수 있습니다.
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight() // 부모 Row의 높이를 따름
            .padding(horizontal = 4.dp, vertical = 6.dp), // 셀 내부 상하 패딩 조정
        verticalArrangement = Arrangement.Top
    ) {
        // 상단: "숫자(요일)" 헤더
        val annotatedDateString = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)) {
                append(dateData.date)
            }
            withStyle(style = SpanStyle(fontSize = 9.sp, color = TextPrimary.copy(alpha = 0.7f))) {
                append("(${dateData.dayOfWeek})")
            }
        }
        Text(
            text = annotatedDateString,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp) // 헤더와 구분선 사이 간격
        )

        // 가로 구분선
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp), // 구분선 위아래 간격 조정
            thickness = 1.dp,
            color = TextPrimary.copy(alpha = 0.2f)
        )

        // 하단: 이벤트 목록 (최대 3개)
        Column(
            modifier = Modifier
                .weight(1f) // 남은 공간을 모두 차지하도록
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp) // 이벤트 간 수직 간격
        ) {
            dateData.events.take(3).forEach { event -> // 최대 3개의 이벤트 표시
                Text(
                    text = event.description,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp) // 이벤트 내부 패딩
                )
            }
        }
    }
}