package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
// ... 기타 필요한 androidx import 문들 ...
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// ▼▼▼▼▼ 수정된 모델 import 경로 ▼▼▼▼▼
import com.example.daytogether.data.model.WeeklyCalendarDay // << 수정된 경로!
// 만약 CalendarEvent도 이 파일에서 직접 참조한다면, 경로 수정 필요
// import com.example.daytogether.data.model.CalendarEvent
// ▲▲▲▲▲ 수정된 모델 import 경로 ▲▲▲▲▲
import com.example.daytogether.ui.theme.TextPrimary
// DayCellNew import 문 (경로 확인)
// import com.example.daytogether.ui.home.composables.DayCellNew

// 테두리 색상 정의 (사용자님이 알려주신 값)
val WeeklyCalendarBorderColor = Color(0xFF533A28)

@Composable
fun WeeklyCalendarView(
    weeklyCalendarData: List<WeeklyCalendarDay>,
    modifier: Modifier = Modifier
) {
    val weeklyCalendarShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .border( // 테두리 적용
                width = 1.dp,
                color = WeeklyCalendarBorderColor,
                shape = weeklyCalendarShape
            )
        // 배경색 없음, 내부 패딩 없음 (날짜 셀이 테두리에 바로 붙도록)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            weeklyCalendarData.forEachIndexed { index, dayData ->
                DayCellNew(
                    dateData = dayData,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                if (index < weeklyCalendarData.size - 1) {
                    VerticalDivider(
                        color = TextPrimary.copy(alpha = 0.15f),
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}