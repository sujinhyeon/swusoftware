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
import androidx.compose.ui.unit.Dp
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

@OptIn(ExperimentalMaterial3Api::class) // MonthlyCalendarView에 필요할 수 있음
@Composable
fun MonthlyCalendarView(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate?) -> Unit, // HomeScreen에서 수정된 로직을 가진 콜백
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    selectedDateForDetails: LocalDate?, // 팝업을 위한 선택 날짜 (HomeScreen에서 관리)
    dateForBorderOnly: LocalDate?,      // '테두리만 표시'를 위한 날짜 (HomeScreen에서 관리, 새로 추가)
    modifier: Modifier = Modifier,
    onEditEventRequest: (LocalDate, CalendarEvent) -> Unit,
    onDeleteEventRequest: (LocalDate, CalendarEvent) -> Unit,
    onTitleClick: () -> Unit,
    onCalendarIconClick: () -> Unit,
    onTodayHeaderButtonClick: () -> Unit // HomeScreen에서 수정된 로직을 가진 콜백
) {
    val today = LocalDate.now()
    // currentMonth나 eventsByDate가 변경될 때만 daysInGrid를 다시 계산
    val daysInGrid: List<MonthlyCalendarCellData?> = remember(currentMonth, eventsByDate.toMap()) {
        println("Recalculating daysInGrid for $currentMonth")
        getDaysForMonthlyCalendarGrid(currentMonth, eventsByDate)
    }

    Column(
        modifier = modifier
    ) {
        MonthlyCalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = {
                val newMonth = currentMonth.minusMonths(1)
                onMonthChange(newMonth)
                // onDateClick(null) // 달 변경 시 선택 상태 초기화는 HomeScreen의 onMonthChange에서 처리
            },
            onNextMonth = {
                val newMonth = currentMonth.plusMonths(1)
                onMonthChange(newMonth)
                // onDateClick(null) // 달 변경 시 선택 상태 초기화는 HomeScreen의 onMonthChange에서 처리
            },
            onTitleClick = onTitleClick,
            onCalendarIconClick = onCalendarIconClick,
            onTodayHeaderButtonClick = onTodayHeaderButtonClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        DayOfWeekHeaderMonthly(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Absolute.spacedBy(0.dp), // 셀 사이 간격 없음
            verticalArrangement = Arrangement.spacedBy(4.dp) // 주(행) 사이 간격
        ) {
            items(daysInGrid.size) { index ->
                val dayInfo = daysInGrid[index]
                MonthlyDayCell(
                    date = dayInfo?.date,
                    isCurrentMonth = dayInfo?.isCurrentMonth ?: false,
                    isToday = dayInfo?.date == today,
                    // MonthlyDayCell에 전달할 Boolean 값들
                    showPopupHighlight = dayInfo?.date != null && dayInfo.date == selectedDateForDetails,
                    showBorderOnlyHighlight = dayInfo?.date != null && dayInfo.date == dateForBorderOnly,
                    events = dayInfo?.events ?: emptyList(),
                    onClick = {
                        // 현재 달의 날짜만 클릭 가능하도록 유지
                        if (dayInfo?.date != null && dayInfo.isCurrentMonth) {
                            onDateClick(dayInfo.date)
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
    showPopupHighlight: Boolean,      // 이름 변경: isSelected -> showPopupHighlight
    showBorderOnlyHighlight: Boolean, // 새로 추가된 파라미터
    events: List<CalendarEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // LazyVerticalGrid의 각 셀에 의해 채워짐
) {
    val cellHeight = 90.dp // 셀 높이
    val textColor = if (!isCurrentMonth) OtherMonthDayText else TextPrimary // 다른 달 날짜 색상

    // 상태에 따라 테두리와 배경색 결정 (이전 답변의 로직 사용)
    val actualBorderColor: Color
    val actualBorderWidth: Dp
    val actualBackgroundColor: Color

    if (showBorderOnlyHighlight) {
        // 경우 1: '테두리만 표시' 상태 (오늘 날짜 첫 클릭 또는 헤더 '오늘' 버튼 클릭 후)
        actualBorderColor = SelectedMonthlyBorder // 테두리 색 (Color.kt에 정의 가정)
        actualBorderWidth = 1.5.dp               // 테두리 두께
        actualBackgroundColor = if (isToday && isCurrentMonth) TodayMonthlyBackground else Color.Transparent
    } else if (showPopupHighlight) {
        // 경우 2: 팝업을 위해 날짜가 선택된 상태
        actualBorderColor = SelectedMonthlyBorder
        actualBorderWidth = 1.5.dp
        actualBackgroundColor = SelectedMonthlyBorder.copy(alpha = 0.2f) // 팝업 선택 시 배경색
    } else {
        // 경우 3: 아무것도 선택되지 않은 일반 날짜
        actualBorderColor = Color.Transparent
        actualBorderWidth = 0.dp
        actualBackgroundColor = if (isToday && isCurrentMonth) TodayMonthlyBackground else Color.Transparent
    }

    Box(
        modifier = modifier // LazyVerticalGrid에 의해 크기가 결정됨 (1/7 너비)
            .height(cellHeight)
            .clip(RoundedCornerShape(4.dp)) // 셀 모양
            .background(actualBackgroundColor) // 결정된 배경색 적용
            .border(BorderStroke(actualBorderWidth, actualBorderColor), RoundedCornerShape(4.dp)) // 결정된 테두리 적용
            .clickable(enabled = date != null && isCurrentMonth) { onClick() } // 현재 달만 클릭 가능
            .padding(horizontal = 3.dp, vertical = 4.dp), // 셀 내부 패딩
        contentAlignment = Alignment.TopCenter // 내용물 정렬
    ) {
        if (date != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight() // 내부 Column이 Box 높이 채우도록
            ) {
                // 날짜 숫자 표시
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically // 날짜와 +N 표시 수직 정렬
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isToday && isCurrentMonth) FontWeight.ExtraBold else FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                        color = if (isToday && isCurrentMonth) TextPrimary else textColor,
                        modifier = Modifier.weight(1f) // 날짜 텍스트가 공간 차지
                    )
                    // 이벤트가 3개 초과 시 +N 표시
                    if (isCurrentMonth && events.size > 3) {
                        Text(
                            text = "+${events.size - 3}",
                            color = MaterialTheme.colorScheme.primary, // 테마 색상 사용
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
                            textAlign = TextAlign.End // 오른쪽 정렬
                        )
                    }
                }

                // 이벤트 목록 표시 (최대 3개)
                if (isCurrentMonth) {
                    Column(
                        modifier = Modifier
                            .weight(1f) // 남은 공간 차지
                            .fillMaxWidth()
                            .padding(top = 2.dp), // 날짜 숫자와 이벤트 목록 사이 간격
                        horizontalAlignment = Alignment.Start, // 이벤트 텍스트 왼쪽 정렬
                        verticalArrangement = Arrangement.spacedBy(1.dp) // 이벤트 간 수직 간격
                    ) {
                        events.take(3).forEach { event ->
                            Text(
                                text = event.description,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), // 이벤트 텍스트 색상
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), // 이벤트 폰트 크기
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start, // 이벤트 텍스트 왼쪽 정렬
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(2.dp)) // 이벤트 배경
                                    .padding(horizontal = 2.dp, vertical = 0.5.dp) // 이벤트 내부 패딩
                            )
                        }
                    }
                } else {
                    // 현재 달이 아니면 이벤트 공간은 비워둠
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

        var currentMonthPreview by remember { mutableStateOf(YearMonth.now()) }
        // --- HomeScreen의 상태 로직을 흉내내기 위한 상태 변수들 ---
        var selectedDateForDetailsPreview by remember { mutableStateOf<LocalDate?>(null) }
        var dateForBorderOnlyPreview by remember { mutableStateOf<LocalDate?>(null) } // <<< 이 상태 추가
        // ---

        Column(Modifier.background(ScreenBackground)) { // ScreenBackground는 테마에 정의되어 있어야 함
            MonthlyCalendarView(
                currentMonth = currentMonthPreview,
                onMonthChange = { newMonth ->
                    currentMonthPreview = newMonth
                    // HomeScreen의 onMonthChange 처럼 상태 초기화
                    selectedDateForDetailsPreview = null
                    dateForBorderOnlyPreview = null
                    println("Month changed to: $newMonth")
                },
                onDateClick = { clickedDate -> // HomeScreen의 onDateClick 로직을 여기에 간략히 구현
                    if (clickedDate == null) return@MonthlyCalendarView // 이 부분은 MonthlyCalendarView 호출부에 맞게 조정
                    if (clickedDate == today) {
                        if (dateForBorderOnlyPreview == today) { // 이미 테두리만 있는 오늘을 클릭
                            selectedDateForDetailsPreview = today
                            dateForBorderOnlyPreview = null
                        } else { // 오늘 첫 클릭
                            selectedDateForDetailsPreview = null
                            dateForBorderOnlyPreview = today
                        }
                    } else { // 다른 날짜 클릭
                        selectedDateForDetailsPreview = clickedDate
                        dateForBorderOnlyPreview = null
                    }
                    println("Date clicked: $clickedDate, BorderFor: $dateForBorderOnlyPreview, PopupFor: $selectedDateForDetailsPreview")
                },
                eventsByDate = dummyEvents,
                selectedDateForDetails = selectedDateForDetailsPreview, // 프리뷰용 상태 전달
                dateForBorderOnly = dateForBorderOnlyPreview,      // <<< 누락된 파라미터 전달
                onEditEventRequest = { date: LocalDate, event: CalendarEvent ->
                    println("Edit event $event on $date requested")
                },
                onDeleteEventRequest = { date: LocalDate, event: CalendarEvent ->
                    println("Delete event $event on $date requested")
                },
                onTitleClick = { println("Month Title clicked (for view toggle)") },
                onCalendarIconClick = { println("Calendar Icon clicked (for time picker)") },
                onTodayHeaderButtonClick = { // HomeScreen의 onTodayHeaderButtonClick 로직 구현
                    currentMonthPreview = YearMonth.now()
                    selectedDateForDetailsPreview = null
                    dateForBorderOnlyPreview = LocalDate.now()
                    println("Today Header Button Clicked. BorderFor: $dateForBorderOnlyPreview, PopupFor: $selectedDateForDetailsPreview")
                }
            )
            // 선택 상태 확인용 텍스트 (디버깅용) - 필요하다면 추가
            Text("Selected for Details (Popup): ${selectedDateForDetailsPreview?.toString() ?: "None"}")
            Text("Border Only For (Today first click): ${dateForBorderOnlyPreview?.toString() ?: "None"}")
        }
    }
}
