package com.example.daytogether.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // items(List) 확장 함수 사용
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos // 수정된 아이콘
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos // 수정된 아이콘
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday // 수정된 아이콘
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.data.model.CalendarEvent // 가정: CalendarEvent 데이터 클래스
import com.example.daytogether.ui.theme.* // 사용자 정의 테마 임포트
import java.time.DayOfWeek as JavaDayOfWeek // 이름 충돌 방지용 별칭
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape


import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.clip

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// MonthlyCalendarCellData를 위한 LocalDate 임포트 누락 방지
import com.example.daytogether.ui.theme.*

import com.example.daytogether.ui.theme.AnniversaryBoardBackground // 오늘 날짜 배경색을 위해
import com.example.daytogether.ui.theme.TextPrimary
import com.example.daytogether.ui.theme.OtherMonthDayText
import com.example.daytogether.ui.theme.SelectedMonthlyBorder
import com.example.daytogether.ui.theme.TodayMonthlyBackground // 수정된 색상 임포트


// 월간 캘린더 전체 뷰
@Composable
fun MonthlyCalendarView(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate) -> Unit,
    onAddEventClick: () -> Unit,
    onShowYearMonthPicker: () -> Unit,
    eventsByDate: Map<LocalDate, List<CalendarEvent>> = emptyMap(),
    selectedDateForDetails: LocalDate?,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()

    val daysInGrid = remember(currentMonth, eventsByDate) { // eventsByDate도 키로 추가 (이벤트 변경 시 재계산)
        getDaysForMonthlyCalendarGrid(currentMonth, eventsByDate)
    }

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        MonthlyCalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { onMonthChange(currentMonth.minusMonths(1)) },
            onNextMonth = { onMonthChange(currentMonth.plusMonths(1)) },
            onTodayClick = {
                val nowMonth = YearMonth.now()
                if (currentMonth != nowMonth) {
                    onMonthChange(nowMonth)
                }
                onDateClick(today)
            },
            onAddEventClick = onAddEventClick,
            onTitleClick = onShowYearMonthPicker
        )
        Spacer(modifier = Modifier.height(10.dp))
        DayOfWeekHeaderMonthly() // 요일 헤더 색상 변경됨
        Spacer(modifier = Modifier.height(6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(daysInGrid) { dayInfo ->
                MonthlyDayCell(
                    date = dayInfo?.date,
                    isCurrentMonth = dayInfo?.isCurrentMonth ?: false,
                    isToday = dayInfo?.date == today,
                    isSelected = dayInfo?.date == selectedDateForDetails,
                    events = dayInfo?.events ?: emptyList(),
                    onClick = {
                        dayInfo?.date?.let { clickedDate ->
                            if (dayInfo.isCurrentMonth) {
                                onDateClick(clickedDate)
                            }
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddEventClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonActiveBackground)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "새 일정 만들기", tint = ButtonActiveText)
            Spacer(modifier = Modifier.width(4.dp))
            Text("새 일정 만들기", color = ButtonActiveText)
        }
    }
}

@Composable
private fun MonthlyCalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit,
    onAddEventClick: () -> Unit,
    onTitleClick: () -> Unit,
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onAddEventClick, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "새 일정 추가", tint = TextPrimary)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousMonth, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "이전 달", tint = TextPrimary)
            }
            Text(
                text = currentMonth.format(formatter),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.clickable { onTitleClick() }.padding(horizontal = 8.dp)
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "다음 달", tint = TextPrimary)
            }
        }
        TextButton(onClick = onTodayClick, modifier = Modifier.height(36.dp)) {
            Text("오늘", color = TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun DayOfWeekHeaderMonthly() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
            Text(
                text = day,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary, // 2. 요일 헤더 색상 #533A28 (TextPrimary)로 통일
                modifier = Modifier.weight(1f)
            )
        }
    }
}

data class MonthlyCalendarCellData(
    val date: LocalDate?,
    val isCurrentMonth: Boolean,
    var events: List<CalendarEvent> = emptyList()
)

@Composable
private fun MonthlyDayCell(
    date: LocalDate?,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    events: List<CalendarEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellHeight = 85.dp
    val textColor = if (isCurrentMonth) TextPrimary else OtherMonthDayText

    // 1. 오늘 날짜 배경색 연하게 (Color.kt에 정의된 TodayMonthlyBackground 사용)
    val backgroundColor = when {
        isToday -> TodayMonthlyBackground // 연하게 조정된 색상
        else -> Color.Transparent
    }
    val borderColor = when {
        isSelected && !isToday -> SelectedMonthlyBorder
        isToday -> SelectedMonthlyBorder // 오늘 날짜에도 테두리 유지 (선택 시와 동일)
        else -> Color.Transparent
    }
    val borderWidth = if (isSelected || isToday) 1.5.dp else 0.dp

    Box(
        modifier = modifier
            .height(cellHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(4.dp))
            .clickable(enabled = date != null && isCurrentMonth) { onClick() }
            .padding(3.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfMonth.toString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal
                    ),
                    color = if (isToday) TextPrimary else textColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Column(
                    modifier = Modifier.weight(1f).padding(top = 1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    val maxTextEvents = 2
                    events.take(maxTextEvents).forEach { event ->
                        Text(
                            text = event.description,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp)
                        )
                    }
                    if (events.size > maxTextEvents) {
                        Text(
                            text = "+${events.size - maxTextEvents}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (events.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun getDaysForMonthlyCalendarGrid(
    yearMonth: YearMonth,
    allEvents: Map<LocalDate, List<CalendarEvent>> = emptyMap()
): List<MonthlyCalendarCellData?> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeekValue = (firstDayOfMonth.dayOfWeek.value % 7)

    val calendarDays = mutableListOf<MonthlyCalendarCellData?>()

    val prevMonth = yearMonth.minusMonths(1)
    val daysInPrevMonth = prevMonth.lengthOfMonth()
    for (i in 0 until firstDayOfWeekValue) {
        val date = prevMonth.atDay(daysInPrevMonth - firstDayOfWeekValue + 1 + i)
        calendarDays.add(MonthlyCalendarCellData(date, false, allEvents[date] ?: emptyList()))
    }

    for (day in 1..daysInMonth) {
        val date = yearMonth.atDay(day)
        calendarDays.add(MonthlyCalendarCellData(date, true, allEvents[date] ?: emptyList()))
    }

    val remainingCells = 42 - calendarDays.size
    val nextMonth = yearMonth.plusMonths(1)
    if (remainingCells > 0) { // remainingCells가 음수일 경우 루프 방지
        for (day in 1..remainingCells) {
            val date = nextMonth.atDay(day)
            calendarDays.add(MonthlyCalendarCellData(date, false, allEvents[date] ?: emptyList()))
        }
    }
    return calendarDays.take(42)
}


@Preview(showBackground = true, name = "월간 캘린더 뷰", widthDp = 390)
@Composable
fun MonthlyCalendarViewPreview() {
    DaytogetherTheme {
        val today = LocalDate.now()
        val events = mapOf(
            today.plusDays(2) to listOf(CalendarEvent("회의"), CalendarEvent("점심")),
            today.plusDays(5) to listOf(CalendarEvent("발표 준비"), CalendarEvent("스터디"), CalendarEvent("운동"))
        )
        MonthlyCalendarView(
            currentMonth = YearMonth.now(),
            onMonthChange = {},
            onDateClick = {},
            onAddEventClick = {},
            onShowYearMonthPicker = {},
            eventsByDate = events,
            selectedDateForDetails = today.plusDays(2)
        )
    }
}