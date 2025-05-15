package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday // 달력 아이콘은 Material Icon 유지
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.ui.theme.TextPrimary
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.res.painterResource
import com.example.daytogether.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items // LazyColumn 용 items (EventDetailsDialog 내부)
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.daytogether.data.model.CalendarEvent // CalendarEvent 모델 경로
import com.example.daytogether.ui.theme.* // 앱 테마 및 색상 경로
import java.time.LocalDate
import java.time.DayOfWeek as JavaDayOfWeek // DayOfWeek 이름 충돌 방지

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember



@Composable
internal fun MonthlyCalendarHeader( // 가시성을 internal 또는 public으로 (HomeScreen에서 직접 호출하지 않으므로 private 유지 가능)
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTitleClick: () -> Unit,           // 년월 텍스트 클릭 시 (뷰 토글)
    onCalendarIconClick: () -> Unit, // 왼쪽 달력 아이콘 클릭 시 (타임피커 열기)
    onTodayHeaderButtonClick: () -> Unit, // 새로 추가된 "오늘" 버튼 클릭 시
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 왼쪽 달력 아이콘: 클릭 시 onCalendarIconClick (타임피커) 호출
        IconButton(onClick = onCalendarIconClick, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Filled.CalendarToday, // Material Icon 사용
                contentDescription = "년/월 선택",
                tint = TextPrimary
            )
        }

        // 이전 달, 년월 텍스트, 다음 달 부분
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_left),
                    contentDescription = "이전 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = currentMonth.format(formatter),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),
                color = TextPrimary,
                modifier = Modifier
                    .clickable { onTitleClick() } // 년월 텍스트 클릭 시 뷰 토글
                    .padding(horizontal = 4.dp)
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_right),
                    contentDescription = "다음 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // "오늘" TextButton 추가
        TextButton(onClick = onTodayHeaderButtonClick, modifier = Modifier.height(36.dp)) {
            Text("오늘", color = TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}


// 월간 캘린더의 각 날짜 셀에 표시될 정보를 담는 데이터 클래스
data class MonthlyCalendarCellData(
    val date: LocalDate?,
    val isCurrentMonth: Boolean,
    var events: List<CalendarEvent> = emptyList()
)

// 주어진 년도와 월에 대한 캘린더 그리드 데이터를 생성하는 함수
private fun getDaysForMonthlyCalendarGrid(
    yearMonth: YearMonth,
    allEvents: Map<LocalDate, List<CalendarEvent>>
): List<MonthlyCalendarCellData?> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()

    // 주의 시작을 월요일로 가정 (JavaDayOfWeek.MONDAY.value == 1)
    // firstDayOfMonth.dayOfWeek.value는 월요일(1) ~ 일요일(7)을 반환
    // 그리드에서 월요일을 0번째 인덱스로 하기 위한 조정값
    val daysToPrepend = (firstDayOfMonth.dayOfWeek.value - JavaDayOfWeek.MONDAY.value).let { if (it < 0) it + 7 else it }

    val calendarDays = mutableListOf<MonthlyCalendarCellData?>()

    // 이전 달의 날짜들 추가
    val prevMonth = yearMonth.minusMonths(1)
    val daysInPrevMonth = prevMonth.lengthOfMonth()
    for (i in 0 until daysToPrepend) {
        val date = prevMonth.atDay(daysInPrevMonth - daysToPrepend + 1 + i)
        calendarDays.add(MonthlyCalendarCellData(date, false, allEvents[date] ?: emptyList()))
    }

    // 현재 달의 날짜들 추가
    for (day in 1..daysInMonth) {
        val date = yearMonth.atDay(day)
        calendarDays.add(MonthlyCalendarCellData(date, true, allEvents[date] ?: emptyList()))
    }

    // 다음 달의 날짜들 추가 (총 5주 = 35칸을 채우도록)
    val totalCellsToDisplay = 35
    val currentCellCount = calendarDays.size
    if (currentCellCount < totalCellsToDisplay) {
        val nextMonth = yearMonth.plusMonths(1)
        for (day in 1..(totalCellsToDisplay - currentCellCount)) {
            val date = nextMonth.atDay(day)
            calendarDays.add(MonthlyCalendarCellData(date, false, allEvents[date] ?: emptyList()))
        }
    }
    return calendarDays // 이미 35칸으로 채워졌거나, 그보다 적으면 해당 리스트 반환
}

// monthlycalendarview.kt - MonthlyCalendarView 함수
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCalendarView(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate?) -> Unit,
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    selectedDateForDetails: LocalDate?,
    modifier: Modifier = Modifier,
    onEditEventRequest: (LocalDate, CalendarEvent) -> Unit, // <<< 다시 추가
    onDeleteEventRequest: (LocalDate, CalendarEvent) -> Unit, // <<< 다시 추가
    onTitleClick: () -> Unit,
    onCalendarIconClick: () -> Unit,
    onTodayHeaderButtonClick: () -> Unit // <<< 새로 추가된 파라미터
) {
    val today = LocalDate.now()
    val daysInGrid: List<MonthlyCalendarCellData?> = remember(currentMonth, eventsByDate.entries.toList()) {
        getDaysForMonthlyCalendarGrid(currentMonth, eventsByDate)
    }

    Column(
        modifier = modifier
    ) {
        MonthlyCalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = {
                val newMonth = currentMonth.minusMonths(1); onMonthChange(newMonth); onDateClick(null)
            },
            onNextMonth = {
                val newMonth = currentMonth.plusMonths(1); onMonthChange(newMonth); onDateClick(null)
            },
            onTitleClick = onTitleClick,
            onCalendarIconClick = onCalendarIconClick,
            onTodayHeaderButtonClick = onTodayHeaderButtonClick, // 새로 추가된 콜백 전달
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        DayOfWeekHeaderMonthly(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Absolute.spacedBy(0.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(daysInGrid.size) { index ->
                val dayInfo = daysInGrid[index]
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
    }
}


@Composable
private fun MonthlyCalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTitleClick: () -> Unit,
    onCalendarIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onCalendarIconClick, modifier = Modifier.size(36.dp)) {
            // Unresolved reference 오류를 피하기 위해 Material Icon으로 변경
            // 사용자 정의 SVG 'ic_calendar_today'가 있다면 아래 painterResource 코드를 사용하세요.
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = "오늘 날짜 달로 이동",
                tint = TextPrimary
            )
            // 만약 'ic_calendar_today.xml' (Vector Drawable) 파일이 res/drawable 에 있다면:
            // Icon(
            //     painter = painterResource(id = R.drawable.ic_calendar_today),
            //     contentDescription = "오늘 날짜 달로 이동",
            //     tint = TextPrimary
            // )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_left), // 사용자 SVG
                    contentDescription = "이전 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = currentMonth.format(formatter),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),
                color = TextPrimary,
                modifier = Modifier
                    .clickable { onTitleClick() }
                    .padding(horizontal = 4.dp)
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_custom_arrow_right), // 사용자 SVG
                    contentDescription = "다음 달",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Box(modifier = Modifier.size(36.dp)) // "오늘" 버튼 자리 유지용 빈 박스
    }
}


@Composable
private fun DayOfWeekHeaderMonthly(modifier: Modifier = Modifier) { // modifier 파라미터 추가
    Row(
        modifier = modifier // 전달받은 modifier 사용 (fillMaxWidth)
            .padding(horizontal = 16.dp) // << LazyVerticalGrid와 동일한 수평 패딩 적용
            .padding(vertical = 4.dp),
        // horizontalArrangement = Arrangement.SpaceAround // weight(1f)를 사용하므로 불필요하거나 Start로 변경
        horizontalArrangement = Arrangement.Start // 또는 Arrangement.Center로 각 텍스트를 중앙 정렬
    ) {
        listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
            Text(
                text = day,
                textAlign = TextAlign.Start, // 각 Text 내부에서 중앙 정렬
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.weight(1f) // 각 요일이 동일한 너비를 차지
            )
        }
    }
}

@Composable
private fun MonthlyDayCell(
    date: LocalDate?,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    events: List<CalendarEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // 이 modifier는 LazyVerticalGrid의 각 셀에 의해 채워짐
) {
    val cellHeight = 90.dp
    // ... (나머지 스타일 로직은 이전과 동일하게 유지)
    val textColor = if (!isCurrentMonth) OtherMonthDayText else TextPrimary
    val finalBackgroundColor = when {
        isSelected -> SelectedMonthlyBorder.copy(alpha = 0.2f)
        isToday && isCurrentMonth -> TodayMonthlyBackground // 현재 달의 오늘만 특별 배경
        else -> Color.Transparent
    }
    val finalBorderColor = if (isSelected) SelectedMonthlyBorder else Color.Transparent
    val finalBorderWidth = if (isSelected) 1.5.dp else 0.dp

    Box(
        modifier = modifier // LazyVerticalGrid에 의해 크기가 결정됨 (1/7 너비)
            .height(cellHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(finalBackgroundColor)
            .border(BorderStroke(finalBorderWidth, finalBorderColor), RoundedCornerShape(4.dp))
            .clickable(enabled = date != null && isCurrentMonth) { onClick() }
            // 셀 내부 패딩으로 내용물 위치 조정
            .padding(horizontal = 3.dp, vertical = 4.dp), // 기존 내부 패딩 유지
        contentAlignment = Alignment.TopCenter
    ) {
        // ... (날짜 및 이벤트 표시 로직은 이전과 동일)
        if (date != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isToday && isCurrentMonth) FontWeight.ExtraBold else FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                        color = if (isToday && isCurrentMonth) TextPrimary else textColor,
                        modifier = Modifier.weight(1f)
                    )
                    if (isCurrentMonth && events.size > 3) {
                        Text(
                            text = "+${events.size - 3}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
                            textAlign = TextAlign.End
                        )
                    }
                }
                if(isCurrentMonth) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        events.take(3).forEach { event ->
                            Text(
                                text = event.description,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
                                    .padding(horizontal = 2.dp, vertical = 0.5.dp)
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// 상세 일정 목록 Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsDialog(
    date: LocalDate,
    events: List<CalendarEvent>,
    onDismiss: () -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                date.format(formatter),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            if (events.isEmpty()) {
                Text("등록된 일정이 없습니다.", modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) { // 일정이 많을 경우 스크롤
                    items(items = events, key = { event -> event.id }) { event ->
                        var showMenu by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* 각 이벤트 클릭 시 동작 정의 가능 */ }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(event.description, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Box {
                                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "더보기", tint = TextPrimary)
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("수정") },
                                        onClick = {
                                            onEditEvent(event)
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("삭제") },
                                        onClick = {
                                            onDeleteEvent(event)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = TextPrimary.copy(alpha = 0.1f))
                    }
                }
            }
        },
        confirmButton = { /* 비워둠 (타이틀과 내용만 표시) */ },
        dismissButton = { /* 비워둠 */ }
    )
}


@Preview(showBackground = true, name = "월간 캘린더 뷰 (전체)", widthDp = 390)
@Composable
fun MonthlyCalendarViewFullPreview() {
    DaytogetherTheme {
        val today = LocalDate.now()
        val dummyEvents = mapOf(
            today.plusDays(1) to listOf(
                CalendarEvent(description = "회의"),
                CalendarEvent(description = "점심 약속"),
                CalendarEvent(description = "저녁 식사"),
                CalendarEvent(description = "추가 일정")
            ),
            today.plusDays(2) to listOf(CalendarEvent(description = "발표 준비 데드라인")),
            today.plusDays(3) to listOf(
                CalendarEvent(description = "스터디"),
                CalendarEvent(description = "운동"),
                CalendarEvent(description = "장보기 목록 작성"),
                CalendarEvent(description = "친구와 저녁 약속"),
                CalendarEvent(description = "도서관 책 반납")
            ),
            today to listOf(
                CalendarEvent(description = "오늘의 할일 1"),
                CalendarEvent(description = "오늘의 할일 2"),
                CalendarEvent(description = "오늘의 할일 3"),
                CalendarEvent(description = "오늘의 할일 4 (길게 써보기 테스트으으으으으으으으으)"),
                CalendarEvent(description = "오늘의 할일 5")
            ),
            today.minusDays(2) to listOf(CalendarEvent(description = "지난 일정"))
        )
        var selectedDateForPreview by remember { mutableStateOf<LocalDate?>(null) }

        Column(Modifier.background(ScreenBackground)) { // ScreenBackground는 테마에 정의되어 있어야 함
            MonthlyCalendarView(
                currentMonth = YearMonth.now(),
                onMonthChange = { println("Month changed to: $it") },
                onDateClick = { date ->
                    selectedDateForPreview = date
                    println("Date clicked: $date")
                },
                eventsByDate = dummyEvents,
                selectedDateForDetails = selectedDateForPreview,
                onEditEventRequest = { date: LocalDate, event: CalendarEvent ->
                    println("Edit event $event on $date requested")
                },
                onDeleteEventRequest = { date: LocalDate, event: CalendarEvent ->
                    println("Delete event $event on $date requested")
                },
                onTitleClick = { println("Month Title clicked (for view toggle)") },
                onCalendarIconClick = { println("Calendar Icon clicked (for time picker)") },
                onTodayHeaderButtonClick = { println("Today Header Button Clicked in Preview") } // <<< 추가된 파라미터
            )
        }
    }
}
