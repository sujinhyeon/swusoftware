package com.example.daytogether.ui.home // 실제 패키지 경로


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // Preview 사용시
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.R
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
import com.example.daytogether.ui.home.composables.ActualHomeScreenContent // ActualHomeScreenContent 경로
import com.example.daytogether.ui.home.composables.EventListItem // EventListItem 경로
import com.example.daytogether.ui.home.composables.YearMonthPickerDialog
import com.example.daytogether.ui.home.composables.AddEventInputView
import com.example.daytogether.ui.theme.DaytogetherTheme // 사용자 정의 테마
import com.example.daytogether.ui.theme.TextPrimary // 사용자 정의 색상
import com.example.daytogether.ui.theme.ScreenBackground // 사용자 정의 색상
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random
import java.time.DayOfWeek as JavaDayOfWeek

// LocalDate.yearMonth 확장 프로퍼티 (필요하다면 유지)
val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

@Composable
fun DateEventsBottomSheet(
    visible: Boolean,
    targetDate: LocalDate,
    events: List<CalendarEvent>,
    onDismiss: () -> Unit,
    onAddNewEventClick: () -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN)

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(animationSpec = tween(durationMillis = 250), initialOffsetY = { it }),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 200), targetOffsetY = { it }),
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 200.dp),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row( // 헤더
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = targetDate.format(dateFormatter),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "닫기", tint = TextPrimary.copy(alpha = 0.7f))
                    }
                }
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.2f))

                if (events.isEmpty()) { // 이벤트 없을 때
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = onAddNewEventClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "새 일정 만들기 아이콘", tint = TextPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("새 일정 만들기", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else { // 이벤트 있을 때
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false).padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        items(items = events, key = { event -> event.id }) { event ->
                            EventListItem(
                                event = event,
                                onEditClick = { onEditEvent(event) },
                                onDeleteClick = { onDeleteEvent(event) }
                            )
                            HorizontalDivider(color = TextPrimary.copy(alpha = 0.1f))
                        }
                    }
                    Button( // 목록 하단 새 일정 만들기 버튼
                        onClick = onAddNewEventClick,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "새 일정 만들기 아이콘", tint = TextPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("새 일정 만들기", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var upcomingAnniversaryText by remember { mutableStateOf("D-3 가족 기념일! 오늘은 즐거운 하루 보내세요!") }
    var dDayTextState by remember { mutableStateOf("D-3") }
    var isAnniversaryAvailable by remember { mutableStateOf(true) }
    var dDayTitleState by remember { mutableStateOf(if (isAnniversaryAvailable) "엄마 생일" else "기념일 없음") }

    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val currentYearMonthFormatted = remember(currentYearMonth) {
        DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN).format(currentYearMonth)
    }

    val today = LocalDate.now()
    var isMonthlyView by remember { mutableStateOf(false) }
    var selectedDateForDetails by remember { mutableStateOf<LocalDate?>(null) }
    var showYearMonthPickerDialog by remember { mutableStateOf(false) }

    var showAddEventInputViewFlag by remember { mutableStateOf(false) }
    var newEventDescriptionState by remember { mutableStateOf("") }
    var editingEventState by remember { mutableStateOf<Pair<LocalDate, CalendarEvent?>?>(null) }

    val eventsByDateState = remember {
        mutableStateMapOf<LocalDate, List<CalendarEvent>>().apply {
            put(today.plusDays(1), listOf(CalendarEvent(description = "회의"), CalendarEvent(description = "점심")))
            put(today.plusDays(3), listOf(CalendarEvent(description = "발표"), CalendarEvent(description = "스터디"), CalendarEvent(description = "운동")))
        }
    }

    val weeklyCalendarDataState = remember(currentYearMonth, today, eventsByDateState) {
        val firstDayOfRelevantWeek = today.with(JavaDayOfWeek.MONDAY)
        (0 until 7).map { dayOffset ->
            val date = firstDayOfRelevantWeek.plusDays(dayOffset.toLong())
            WeeklyCalendarDay(
                date = date.dayOfMonth.toString(),
                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                isToday = date.isEqual(today),
                events = eventsByDateState[date] ?: emptyList()
            )
        }
    }

    var isQuestionAnsweredByAllState by remember { mutableStateOf(false) }
    var aiQuestionState by remember { mutableStateOf("우리 가족만의 별명(애칭)이 있나요?") }
    var familyQuoteState by remember { mutableStateOf("\"가족은 자격이 아니라 특권이다.\" - 클린트 이스트우드") }

    val cloudImages = remember { listOf(R.drawable.cloud1, R.drawable.cloud2) }
    val numberOfClouds by remember { mutableStateOf(Random.nextInt(1, cloudImages.size + 1)) }
    val randomCloudResIds: List<Int> = remember(numberOfClouds) { cloudImages.shuffled().take(numberOfClouds) }

    val commonEditEventLogic = { date: LocalDate, event: CalendarEvent? ->
        editingEventState = date to event
        newEventDescriptionState = event?.description ?: ""
        showAddEventInputViewFlag = true
        selectedDateForDetails = null // 하단 바 닫기
    }

    val onSaveNewEventLambda = {
        editingEventState?.let { (date, eventToEdit) ->
            if (newEventDescriptionState.isNotBlank()) {
                val currentEvents = eventsByDateState[date]?.toMutableList() ?: mutableListOf()
                if (eventToEdit != null) { // 수정
                    val index = currentEvents.indexOfFirst { it.id == eventToEdit.id }
                    if (index != -1) currentEvents[index] = eventToEdit.copy(description = newEventDescriptionState)
                    else currentEvents.add(CalendarEvent(id = eventToEdit.id, description = newEventDescriptionState))
                } else { // 추가
                    currentEvents.add(CalendarEvent(description = newEventDescriptionState))
                }
                eventsByDateState[date] = currentEvents
            }
        }
        showAddEventInputViewFlag = false
        newEventDescriptionState = ""
        editingEventState = null
    }

    val onCancelNewEventInputLambda = {
        showAddEventInputViewFlag = false
        newEventDescriptionState = ""
        editingEventState = null
    }

    val onDeleteEventRequestInHomeScreenLambda = { date: LocalDate, event: CalendarEvent ->
        val currentEvents = eventsByDateState[date]?.toMutableList() ?: mutableListOf()
        val updatedEvents = currentEvents.filterNot { it.id == event.id }
        if (updatedEvents.isEmpty()) {
            eventsByDateState.remove(date)
            if (selectedDateForDetails == date) { selectedDateForDetails = null }
        } else {
            eventsByDateState[date] = updatedEvents
        }
    }

    Box(modifier = Modifier.fillMaxSize()) { // 전체 화면을 Box로 감싸서 하단 UI 오버레이 용이하게
        ActualHomeScreenContent(
            upcomingAnniversaryText = upcomingAnniversaryText,
            dDayText = dDayTextState,
            dDayTitle = dDayTitleState,
            randomCloudResIds = randomCloudResIds,
            currentYearMonth = currentYearMonth,
            currentYearMonthFormatted = currentYearMonthFormatted,
            isMonthlyView = isMonthlyView,
            selectedDateForDetails = selectedDateForDetails,
            eventsByDate = eventsByDateState,
            weeklyCalendarData = weeklyCalendarDataState,
            isQuestionAnsweredByAll = isQuestionAnsweredByAllState,
            aiQuestion = aiQuestionState,
            familyQuote = familyQuoteState,
            showAddEventInputScreen = showAddEventInputViewFlag,
            isBottomBarVisible = selectedDateForDetails != null,
            onMonthChange = { newMonth ->
                currentYearMonth = newMonth
                selectedDateForDetails = null
            },
            onDateClick = { date -> selectedDateForDetails = date },
            onToggleCalendarView = {
                isMonthlyView = !isMonthlyView
                selectedDateForDetails = null
            },
            onMonthlyCalendarHeaderTitleClick = {
                isMonthlyView = false
                selectedDateForDetails = null
            },
            onMonthlyCalendarHeaderIconClick = { showYearMonthPickerDialog = true },
            onRefreshQuestionClicked = {
                aiQuestionState = "새로운 AI 질문 생성 중..."
                isQuestionAnsweredByAllState = false
            },
            onMonthlyTodayButtonClick = {
                val now = LocalDate.now()
                currentYearMonth = now.yearMonth
                selectedDateForDetails = now
                isMonthlyView = true
            },
            // ActualHomeScreenContent가 MonthlyCalendarView에 전달할 수 있도록 유지
            onEditEventRequest = { date, event -> commonEditEventLogic(date, event) },
            onDeleteEventRequest = onDeleteEventRequestInHomeScreenLambda
        )

        // --- YearMonthPickerDialog 표시 로직 ---
        if (showYearMonthPickerDialog) {
            YearMonthPickerDialog(
                initialYearMonth = currentYearMonth,
                onDismissAndConfirm = { selectedYearMonth ->
                    currentYearMonth = selectedYearMonth
                    selectedDateForDetails = null
                    showYearMonthPickerDialog = false
                }
            )
        }

        // --- DateEventsBottomSheet (하단 상세 일정 바) 표시 로직 ---
        selectedDateForDetails?.let { date ->
            DateEventsBottomSheet(
                visible = true,
                targetDate = date,
                events = eventsByDateState[date] ?: emptyList(),
                onDismiss = { selectedDateForDetails = null },
                onAddNewEventClick = { commonEditEventLogic(date, null) },
                onEditEvent = { event -> commonEditEventLogic(date, event) },
                onDeleteEvent = { event -> onDeleteEventRequestInHomeScreenLambda(date, event) },
                modifier = Modifier.align(Alignment.BottomCenter) // Box 안에서 하단 정렬
            )
        }

        // --- AddEventInputView 표시 로직 ---
        editingEventState?.first?.let { targetDate ->
            AnimatedVisibility(
                visible = showAddEventInputViewFlag,
                modifier = Modifier.align(Alignment.BottomCenter) // Box 안에서 하단 정렬 또는 전체 화면 오버레이 방식에 맞게 조정
            ) {
                AddEventInputView(
                    visible = true, // AnimatedVisibility가 제어
                    targetDate = targetDate,
                    eventDescription = newEventDescriptionState,
                    onDescriptionChange = { desc -> newEventDescriptionState = desc },
                    onSave = onSaveNewEventLambda,
                    onCancel = onCancelNewEventInputLambda
                )
            }
        }
    } // End of main Box
} // End of HomeScreen Composable

// --- Preview 함수들 ---
@Preview(showBackground = true, name = "홈 (주간 뷰)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenWeeklyPreview() {
    DaytogetherTheme {
        val today = LocalDate.now() // 현재 날짜 (2025년 5월 15일 목요일)

        // 4개의 이벤트를 포함할 샘플 이벤트 리스트
        val fourEventsSample = listOf(
            CalendarEvent(description = "이벤트 1: 아침 회의"),
            CalendarEvent(description = "이벤트 2: 점심 약속"),
            CalendarEvent(description = "이벤트 3: 프로젝트 작업"),
            CalendarEvent(description = "이벤트 4: 저녁 운동")
        )

        val previewWeeklyData = (0..6).map { dayOffset ->
            val date = today.with(JavaDayOfWeek.MONDAY).plusDays(dayOffset.toLong()) // 이번 주 월요일부터 시작

            // 첫 번째 날짜(월요일, dayOffset == 0)에만 4개의 이벤트를 넣고,
            // 세 번째 날짜(수요일, dayOffset == 2)에는 1개의 이벤트를 넣어 비교
            // 다른 날들은 기존 로직 유지 또는 빈 이벤트로 설정
            val eventsForThisDay = when (dayOffset) {
                0 -> fourEventsSample // 월요일에 4개 이벤트
                2 -> listOf(CalendarEvent(description = "수요 주간이벤트")) // 수요일에 1개 이벤트
                else -> emptyList() // 나머지 요일은 이벤트 없음 (또는 기존 로직: if(dayOffset % 2 == 0) ...)
            }

            WeeklyCalendarDay(
                date = date.dayOfMonth.toString(),
                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                isToday = date.isEqual(today),
                events = eventsForThisDay // 수정된 이벤트 리스트 사용
            )
        }
        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-7 우리 가족 첫 여행!",
            dDayText = "D-7",
            dDayTitle = "가족 여행",
            randomCloudResIds = listOf(R.drawable.cloud1, R.drawable.cloud2),
            currentYearMonth = YearMonth.now(),
            currentYearMonthFormatted = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)),
            isMonthlyView = false,
            selectedDateForDetails = null,
            // eventsByDate는 주간 뷰에서는 직접 사용되지 않고 weeklyCalendarData를 통해 전달됩니다.
            // 필요하다면 이 부분도 weeklyCalendarData와 일관성 있게 맞출 수 있지만,
            // WeeklyCalendarView는 weeklyCalendarData.events를 직접 사용하므로 이 부분 수정은 필수는 아닙니다.
            eventsByDate = mapOf(
                today.with(JavaDayOfWeek.MONDAY) to fourEventsSample, // 월요일 이벤트
                today.with(JavaDayOfWeek.MONDAY).plusDays(2) to listOf(CalendarEvent(description = "수요 주간이벤트")) // 수요일 이벤트
            ),
            weeklyCalendarData = previewWeeklyData,
            isQuestionAnsweredByAll = false,
            aiQuestion = "이번 주말에 함께 하고 싶은 활동은 무엇인가요?",
            familyQuote = "\"가족은 사랑의 시작이자 끝이다.\" - 마더 테레사",
            showAddEventInputScreen = false,
            isBottomBarVisible = false,
            onMonthChange = {},
            onDateClick = {},
            onToggleCalendarView = {},
            onMonthlyCalendarHeaderTitleClick = {},
            onMonthlyCalendarHeaderIconClick = {},
            onRefreshQuestionClicked = {},
            onMonthlyTodayButtonClick = {},
            onEditEventRequest = { _, _ -> },
            onDeleteEventRequest = { _, _ -> }
        )
    }
}


@Preview(showBackground = true, name = "홈 (월간 뷰)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenMonthlyPreview() {
    DaytogetherTheme {
        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-3 가족 기념일!",
            dDayText = "D-3",
            dDayTitle = "엄마 생일",
            randomCloudResIds = listOf(R.drawable.cloud2),
            currentYearMonth = YearMonth.of(2025, 5),
            currentYearMonthFormatted = "2025년 05월",
            isMonthlyView = true,
            selectedDateForDetails = LocalDate.of(2025,5,12),
            eventsByDate = mapOf(
                LocalDate.of(2025,5,12) to listOf(CalendarEvent(description = "미리보기 이벤트")),
                LocalDate.of(2025,5,15) to listOf(CalendarEvent(description = "월간 약속"), CalendarEvent(description = "월간 ..."))
            ),
            weeklyCalendarData = emptyList(), // 월간 뷰에서는 사용 안 함
            isQuestionAnsweredByAll = true,
            aiQuestion = "가장 좋아하는 가족 여행지는?",
            familyQuote = "\"다른 무엇보다도, 준비가 성공의 열쇠이다.\" - 알렉산더 그레이엄 벨",
            showAddEventInputScreen = false,
            isBottomBarVisible = true, // 월간 뷰에서 날짜 선택 시 하단 바 보일 수 있음
            onMonthChange = {},
            onDateClick = {},
            onToggleCalendarView = {},
            onMonthlyCalendarHeaderTitleClick = {},
            onMonthlyCalendarHeaderIconClick = {},
            onRefreshQuestionClicked = {},
            onMonthlyTodayButtonClick = {},
            onEditEventRequest = { _,_ -> },
            onDeleteEventRequest = { _,_ -> }
        )
    }
}