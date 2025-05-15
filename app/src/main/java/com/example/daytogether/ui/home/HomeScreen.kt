package com.example.daytogether.ui.home // 실제 패키지 경로

import com.example.daytogether.ui.home.composables.*
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

    var isMonthlyView by remember { mutableStateOf(false) }
    var selectedDateForDetails by remember { mutableStateOf<LocalDate?>(null) }
    var dateForBorderOnly by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now()

    var showYearMonthPickerDialog by remember { mutableStateOf(false) }

    var showAddEventInputViewFlag by remember { mutableStateOf(false) }
    var newEventDescriptionState by remember { mutableStateOf("") }
    var editingEventState by remember { mutableStateOf<Pair<LocalDate, CalendarEvent?>?>(null) }

    val eventsByDateState = remember {
        mutableStateMapOf<LocalDate, List<CalendarEvent>>().apply {
            put(today.plusDays(1), listOf(CalendarEvent(description = "회의"), CalendarEvent(description = "점심")))
            put(today.plusDays(3), listOf(CalendarEvent(description = "발표"), CalendarEvent(description = "스터디"), CalendarEvent(description = "운동")))
            put(today, listOf(CalendarEvent(description="오늘 미팅"), CalendarEvent(description="오늘 약속")))
        }
    }

    val weeklyCalendarDataState = remember(currentYearMonth, today, eventsByDateState, isMonthlyView) {
        val referenceDate = if (isMonthlyView) currentYearMonth.atDay(1) else today
        val firstDayOfRelevantWeek = referenceDate.with(JavaDayOfWeek.MONDAY)
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
    val numberOfClouds by remember(cloudImages) { mutableStateOf(if (cloudImages.isNotEmpty()) Random.nextInt(1, cloudImages.size + 1) else 0) }
    val randomCloudResIds: List<Int> = remember(numberOfClouds, cloudImages) { cloudImages.shuffled().take(numberOfClouds) }

    val commonEditEventLogic: (LocalDate, CalendarEvent?) -> Unit = { date, event ->
        editingEventState = date to event
        newEventDescriptionState = event?.description ?: ""
        showAddEventInputViewFlag = true
        selectedDateForDetails = null
        dateForBorderOnly = null
    }

    val onSaveNewEventLambda: () -> Unit = {
        editingEventState?.let { (date, eventToEdit) ->
            if (newEventDescriptionState.isNotBlank()) {
                // 항상 기존 리스트를 복사하여 새 리스트를 기반으로 작업 시작
                val currentEvents = eventsByDateState[date]?.toList() ?: emptyList()
                val newEventList: List<CalendarEvent>

                if (eventToEdit != null) { // 수정 모드
                    var eventUpdated = false
                    newEventList = currentEvents.map { existingEvent ->
                        if (existingEvent.id == eventToEdit.id) {
                            eventUpdated = true
                            existingEvent.copy(description = newEventDescriptionState)
                        } else {
                            existingEvent
                        }
                    }
                    // 만약 ID가 같은 이벤트가 없어서 업데이트가 안 되었다면, 새 이벤트로 추가 (선택적 로직)
                    // 이 경우는 보통 eventToEdit.id가 잘못되었거나 데이터 동기화 문제일 수 있음
                    if (!eventUpdated) {
                        // 기존 ID를 사용하거나, 새 ID를 생성하여 추가할 수 있음
                        // 여기서는 기존 ID를 사용한다고 가정
                        // newEventList = newEventList + CalendarEvent(id = eventToEdit.id, description = newEventDescriptionState)
                    }
                } else { // 추가 모드
                    // 새 이벤트 추가 시 ID는 CalendarEvent 내부에서 UUID.randomUUID().toString() 등으로 자동 생성되도록 가정
                    newEventList = currentEvents + CalendarEvent(description = newEventDescriptionState)
                }
                eventsByDateState[date] = newEventList // ★★★ 새로운 리스트 인스턴스로 교체 ★★★
            }
        }
        showAddEventInputViewFlag = false
        newEventDescriptionState = ""
        editingEventState = null
    }


    val onCancelNewEventInputLambda: () -> Unit = {
        showAddEventInputViewFlag = false
        newEventDescriptionState = ""
        editingEventState = null
    }


    val onDeleteEventRequestInHomeScreenLambda: (LocalDate, CalendarEvent) -> Unit = { date, eventToDelete ->
        eventsByDateState[date]?.let { currentEventsList ->
            val updatedEvents = currentEventsList.filterNot { it.id == eventToDelete.id }
            if (updatedEvents.isEmpty()) {
                eventsByDateState.remove(date) // ★★★ 키 자체를 제거 (recomposition 유발) ★★★
            } else {
                eventsByDateState[date] = updatedEvents // ★★★ filterNot은 이미 새 리스트를 반환 ★★★
            }
        }
        // 상세 팝업이 삭제된 이벤트가 포함된 날짜를 보고 있었다면, 해당 팝업을 닫거나 UI를 업데이트
        if (eventsByDateState[date].isNullOrEmpty() && selectedDateForDetails == date) {
            selectedDateForDetails = null
            dateForBorderOnly = null // 관련 상태도 초기화
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            isBottomBarVisible = selectedDateForDetails != null && !showAddEventInputViewFlag,
            onMonthChange = { newMonth ->
                currentYearMonth = newMonth
                selectedDateForDetails = null
                dateForBorderOnly = null
            },
            onDateClick = { clickedDate -> // 사용자가 날짜 '셀'을 클릭했을 때
                if (clickedDate == null) {
                    selectedDateForDetails = null
                    dateForBorderOnly = null // 모든 선택 상태 초기화
                    return@ActualHomeScreenContent
                }

                val today = LocalDate.now() // 오늘 날짜 확인

                if (dateForBorderOnly?.isEqual(today) == true && clickedDate.isEqual(today)) {
                    // 경우 1: '헤더의 오늘 버튼'을 눌러서 오늘 날짜에 테두리만 있는 상태였는데,
                    //         그 오늘 날짜 셀을 사용자가 클릭한 경우 (즉, '오늘' 시나리오의 두 번째 액션)
                    selectedDateForDetails = today // 팝업을 보여준다.
                    dateForBorderOnly = null       // '테두리만 표시' 상태는 해제한다.
                } else {
                    // 경우 2: 그 외 모든 경우 (오늘 날짜 셀 직접 첫 클릭, 다른 날짜 클릭)
                    // dateForBorderOnly 상태가 오늘이 아니거나, 클릭된 날짜가 오늘이 아니거나,
                    // 또는 오늘이지만 dateForBorderOnly가 설정되지 않은 경우.
                    selectedDateForDetails = clickedDate // 클릭된 날짜에 대해 바로 팝업을 보여준다.
                    dateForBorderOnly = null             // '테두리만 표시' 상태는 없앤다 (다른 날짜를 클릭했거나, 오늘을 직접 클릭하여 팝업을 띄웠으므로).
                }
            },

            onMonthlyTodayButtonClick = { // 캘린더 헤더의 '오늘' 문구(버튼)를 클릭했을 때
                val now = LocalDate.now()
                currentYearMonth = YearMonth.from(now) // 달력을 현재 달로 이동
                selectedDateForDetails = null          // 팝업은 보여주지 말고
                dateForBorderOnly = now                // 오늘 날짜에 '테두리만 표시' 상태로 만들자
            },
            onToggleCalendarView = {
                isMonthlyView = !isMonthlyView
                selectedDateForDetails = null
                dateForBorderOnly = null
            },
            onMonthlyCalendarHeaderTitleClick = {
                isMonthlyView = false
                selectedDateForDetails = null
                dateForBorderOnly = null
            },
            onMonthlyCalendarHeaderIconClick = { showYearMonthPickerDialog = true },
            onRefreshQuestionClicked = {
                aiQuestionState = "새로운 AI 질문을 로딩하고 있습니다..."
                isQuestionAnsweredByAllState = false
            },

            onEditEventRequest = { date, event -> commonEditEventLogic(date, event) },
            onDeleteEventRequest = onDeleteEventRequestInHomeScreenLambda, // 타입이 명시된 람다 전달
            dateForBorderOnly = dateForBorderOnly
        )

        if (showYearMonthPickerDialog) {
            YearMonthPickerDialog(
                initialYearMonth = currentYearMonth,
                onDismissAndConfirm = { selectedYearMonth ->
                    currentYearMonth = selectedYearMonth
                    selectedDateForDetails = null
                    dateForBorderOnly = null
                    showYearMonthPickerDialog = false
                }
            )
        }

        selectedDateForDetails?.let { date ->
            if (!showAddEventInputViewFlag) {
                DateEventsBottomSheet(
                    visible = true,
                    targetDate = date,
                    events = eventsByDateState[date] ?: emptyList(),
                    onDismiss = { selectedDateForDetails = null },
                    onAddNewEventClick = { commonEditEventLogic(date, null) },
                    onEditEvent = { event -> commonEditEventLogic(date, event) },
                    onDeleteEvent = { event -> onDeleteEventRequestInHomeScreenLambda(date, event) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        editingEventState?.first?.let { targetDate ->
            AnimatedVisibility(
                visible = showAddEventInputViewFlag,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                AddEventInputView(
                    visible = true,
                    targetDate = targetDate,
                    eventDescription = newEventDescriptionState,
                    onDescriptionChange = { desc -> newEventDescriptionState = desc },
                    onSave = onSaveNewEventLambda,
                    onCancel = onCancelNewEventInputLambda
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "홈 (주간 뷰)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenWeeklyPreview() {
    DaytogetherTheme {
        val today = LocalDate.now()

        // --- 프리뷰용 상태 및 샘플 데이터 ---
        var currentMonthPreview by remember { mutableStateOf(YearMonth.now()) }
        var selectedDateForDetailsPreview by remember { mutableStateOf<LocalDate?>(null) }
        var dateForBorderOnlyPreview by remember { mutableStateOf<LocalDate?>(null) }

        val fourEventsSample = listOf(
            CalendarEvent(description = "주간 샘플 이벤트 1"),
            CalendarEvent(description = "주간 샘플 이벤트 2")
        )

        val previewWeeklyData = (0..6).map { dayOffset ->
            val date = today.with(JavaDayOfWeek.MONDAY).plusDays(dayOffset.toLong())
            val eventsForThisDay = when (dayOffset) {
                0 -> fourEventsSample
                2 -> listOf(CalendarEvent(description = "다른 주간 이벤트"))
                else -> emptyList()
            }
            WeeklyCalendarDay(
                date = date.dayOfMonth.toString(),
                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                isToday = date.isEqual(today),
                events = eventsForThisDay
            )
        }

        // eventsByDate도 주간 뷰 프리뷰에 맞게 설정
        val previewEventsByDate = mutableMapOf<LocalDate, List<CalendarEvent>>()
        if (previewWeeklyData.isNotEmpty()) {
            val monday = today.with(JavaDayOfWeek.MONDAY)
            previewEventsByDate[monday] = fourEventsSample
            previewEventsByDate[monday.plusDays(2)] = listOf(CalendarEvent(description = "다른 주간 이벤트"))
        }


        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-5 주간 미리보기 기념일!",
            dDayText = "D-5",
            dDayTitle = "주간 가족 행사 (미리보기)",
            randomCloudResIds = listOf(R.drawable.cloud1, R.drawable.cloud2), // 실제 R.drawable ID
            currentYearMonth = currentMonthPreview,
            currentYearMonthFormatted = currentMonthPreview.format(DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)),
            isMonthlyView = false, // 주간 뷰
            selectedDateForDetails = selectedDateForDetailsPreview,
            dateForBorderOnly = dateForBorderOnlyPreview,
            eventsByDate = previewEventsByDate,
            weeklyCalendarData = previewWeeklyData,
            isQuestionAnsweredByAll = false,
            aiQuestion = "주간 뷰 미리보기 AI 질문입니다. 오늘 기분은?",
            familyQuote = "\"주간 미리보기 가족 명언입니다.\"",
            showAddEventInputScreen = false,
            isBottomBarVisible = selectedDateForDetailsPreview != null,

            onMonthChange = { newMonth ->
                currentMonthPreview = newMonth
                selectedDateForDetailsPreview = null
                dateForBorderOnlyPreview = null
            },
            onDateClick = { clickedDate ->
                if (clickedDate == null) return@ActualHomeScreenContent
                if (clickedDate.isEqual(today)) {
                    if (dateForBorderOnlyPreview?.isEqual(today) == true) {
                        selectedDateForDetailsPreview = today
                        dateForBorderOnlyPreview = null
                    } else {
                        selectedDateForDetailsPreview = null
                        dateForBorderOnlyPreview = today
                    }
                } else {
                    selectedDateForDetailsPreview = clickedDate
                    dateForBorderOnlyPreview = null
                }
            },
            onToggleCalendarView = {}, // 프리뷰에서는 보통 비워둠
            onMonthlyCalendarHeaderTitleClick = {}, // 프리뷰에서는 보통 비워둠
            onMonthlyCalendarHeaderIconClick = {}, // 프리뷰에서는 보통 비워둠
            onRefreshQuestionClicked = {}, // 프리뷰에서는 보통 비워둠
            onMonthlyTodayButtonClick = {
                currentMonthPreview = YearMonth.from(today)
                selectedDateForDetailsPreview = null
                dateForBorderOnlyPreview = today
            },
            onEditEventRequest = { _, _ -> }, // 프리뷰에서는 보통 비워둠
            onDeleteEventRequest = { _, _ -> } // 프리뷰에서는 보통 비워둠
        )
    }
}

@Preview(showBackground = true, name = "홈 (월간 뷰)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenMonthlyPreview() {
    DaytogetherTheme {
        val today = LocalDate.now() // 이 프리뷰의 '오늘' 기준

        // --- 프리뷰용 상태 변수 추가 ---
        var selectedDateForDetailsPreview by remember { mutableStateOf<LocalDate?>(null) } // 초기 선택 없음
        var dateForBorderOnlyPreview by remember { mutableStateOf<LocalDate?>(null) }
        var currentMonthPreview by remember { mutableStateOf(YearMonth.now()) } // 현재 달로 시작

        val previewEventsByDate = mapOf(
            currentMonthPreview.atDay(12) to listOf(CalendarEvent(description = "월간 미리보기 이벤트")),
            currentMonthPreview.atDay(15) to listOf(CalendarEvent(description = "월간 약속"), CalendarEvent(description = "월간 ...")),
            today to listOf(CalendarEvent(description = "오늘의 월간 이벤트")) // 오늘 날짜에도 이벤트 추가
        )

        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-10 월간 미리보기 기념일!",
            dDayText = "D-10",
            dDayTitle = "월간 가족 식사 (미리보기)",
            randomCloudResIds = listOf(R.drawable.cloud2), // 실제 R.drawable ID
            currentYearMonth = currentMonthPreview,
            currentYearMonthFormatted = currentMonthPreview.format(DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)),
            isMonthlyView = true, // 월간 뷰
            selectedDateForDetails = selectedDateForDetailsPreview,
            dateForBorderOnly = dateForBorderOnlyPreview,
            eventsByDate = previewEventsByDate,
            weeklyCalendarData = emptyList(), // 월간 뷰에서는 주간 데이터는 비워두거나 의미 없는 값
            isQuestionAnsweredByAll = true,
            aiQuestion = "월간 뷰 AI 질문: 가장 기억에 남는 가족 여행은?",
            familyQuote = "\"월간 미리보기: 가족은 나의 힘이다.\"",
            showAddEventInputScreen = false,
            isBottomBarVisible = selectedDateForDetailsPreview != null,

            onMonthChange = { newMonth ->
                currentMonthPreview = newMonth
                selectedDateForDetailsPreview = null
                dateForBorderOnlyPreview = null
            },
            onDateClick = { clickedDate ->
                if (clickedDate == null) return@ActualHomeScreenContent
                if (clickedDate.isEqual(today)) { // 이 프리뷰의 today 사용
                    if (dateForBorderOnlyPreview?.isEqual(today) == true) {
                        selectedDateForDetailsPreview = today
                        dateForBorderOnlyPreview = null
                    } else {
                        selectedDateForDetailsPreview = null
                        dateForBorderOnlyPreview = today
                    }
                } else {
                    selectedDateForDetailsPreview = clickedDate
                    dateForBorderOnlyPreview = null
                }
            },
            onToggleCalendarView = {},
            onMonthlyCalendarHeaderTitleClick = {},
            onMonthlyCalendarHeaderIconClick = {},
            onRefreshQuestionClicked = {},
            onMonthlyTodayButtonClick = {
                currentMonthPreview = YearMonth.from(today)
                selectedDateForDetailsPreview = null
                dateForBorderOnlyPreview = today
            },
            onEditEventRequest = { _, _ -> },
            onDeleteEventRequest = { _, _ -> }
        )
    }
}


