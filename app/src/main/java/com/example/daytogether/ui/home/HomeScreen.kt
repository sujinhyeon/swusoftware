package com.example.daytogether.ui.home

import com.example.daytogether.ui.home.composables.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.daytogether.R
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
import com.example.daytogether.ui.theme.DaytogetherTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random
import java.time.DayOfWeek as JavaDayOfWeek

val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

@Composable
fun HomeScreen() {
    var upcomingAnniversaryText by remember { mutableStateOf("D-3 가족 기념일! 오늘은 즐거운 하루 보내세요!") }
    var dDayTextState by remember { mutableStateOf("D-3") }
    var isAnniversaryAvailable by remember { mutableStateOf(true) }
    var dDayTitleState by remember { mutableStateOf(if (isAnniversaryAvailable) "엄마 생일" else "기념일 없음") }

    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

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

    val weeklyCalendarDataState = remember(today, eventsByDateState, isMonthlyView) {
        val referenceDateForWeek = today
        val firstDayOfRelevantWeek = referenceDateForWeek.with(JavaDayOfWeek.MONDAY)
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
                val currentEvents = eventsByDateState[date]?.toList() ?: emptyList()
                val newEventList: List<CalendarEvent>
                if (eventToEdit != null) {
                    var eventUpdated = false
                    newEventList = currentEvents.map { existingEvent ->
                        if (existingEvent.id == eventToEdit.id) {
                            eventUpdated = true
                            existingEvent.copy(description = newEventDescriptionState)
                        } else { existingEvent }
                    }
                    if (!eventUpdated) { /* ID가 같은 이벤트가 없는 경우의 처리 (선택적) */ }
                } else {
                    newEventList = currentEvents + CalendarEvent(description = newEventDescriptionState)
                }
                eventsByDateState[date] = newEventList
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
                eventsByDateState.remove(date)
            } else {
                eventsByDateState[date] = updatedEvents.toList()
            }
        }
        if (eventsByDateState[date].isNullOrEmpty() && selectedDateForDetails == date) {
            selectedDateForDetails = null
            dateForBorderOnly = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ActualHomeScreenContent(
            upcomingAnniversaryText = upcomingAnniversaryText,
            dDayText = dDayTextState,
            dDayTitle = dDayTitleState,
            randomCloudResIds = randomCloudResIds,
            currentYearMonth = currentYearMonth,
            // currentYearMonthFormatted 파라미터는 ActualHomeScreenContent 내부에서 계산하므로 전달 안 함
            isMonthlyView = isMonthlyView,
            selectedDateForDetails = selectedDateForDetails,
            // dateForBorderOnly는 아래 다른 파라미터들 사이에 올바르게 한 번만 전달됩니다.
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
            onDateClick = { clickedDate ->
                if (clickedDate == null) {
                    selectedDateForDetails = null
                    dateForBorderOnly = null
                    return@ActualHomeScreenContent
                }
                if (dateForBorderOnly?.isEqual(today) == true && clickedDate.isEqual(today)) {
                    selectedDateForDetails = today
                    dateForBorderOnly = null
                } else {
                    selectedDateForDetails = clickedDate
                    dateForBorderOnly = null
                }
            },
            onToggleCalendarView = {
                val switchToMonthly = !isMonthlyView
                isMonthlyView = switchToMonthly
                selectedDateForDetails = null
                dateForBorderOnly = null
                if (switchToMonthly) {
                    currentYearMonth = YearMonth.from(today)
                }
            },
            onMonthlyCalendarHeaderTitleClick = {
                isMonthlyView = false
                selectedDateForDetails = null
                dateForBorderOnly = null
            },
            onMonthlyCalendarHeaderIconClick = {
                if (isMonthlyView) {
                    showYearMonthPickerDialog = true
                }
            },
            onRefreshQuestionClicked = {
                aiQuestionState = "새로운 AI 질문을 로딩하고 있습니다..."
                isQuestionAnsweredByAllState = false
            },
            onMonthlyTodayButtonClick = {
                val now = LocalDate.now()
                currentYearMonth = YearMonth.from(now)
                selectedDateForDetails = null
                dateForBorderOnly = now
                if (!isMonthlyView) isMonthlyView = true
            },
            onEditEventRequest = { date, event -> commonEditEventLogic(date, event) },
            onDeleteEventRequest = onDeleteEventRequestInHomeScreenLambda,
            // --- 여기가 수정된 부분 ---
            dateForBorderOnly = dateForBorderOnly // <<< onDeleteEventRequest 뒤에 쉼표로 구분하여 정확히 한 번 전달
        )

        if (showYearMonthPickerDialog) {
            YearMonthPickerDialog(
                initialYearMonth = currentYearMonth,
                onDismissAndConfirm = { selectedYearMonth ->
                    if (selectedYearMonth != null) {
                        currentYearMonth = selectedYearMonth
                    }
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
                    visible = showAddEventInputViewFlag,
                    targetDate = targetDate,
                    eventDescription = newEventDescriptionState,
                    isEditing = editingEventState?.second != null,
                    onDescriptionChange = { desc -> newEventDescriptionState = desc },
                    onSave = onSaveNewEventLambda,
                    onCancel = onCancelNewEventInputLambda
                )
            }
        }
    }
}


// --- Preview 함수들 수정 ---

@Preview(showBackground = true, name = "홈 (주간 뷰)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenWeeklyPreview() {
    DaytogetherTheme {
        val today = LocalDate.now()
        var currentMonthPreview by remember { mutableStateOf(YearMonth.now()) }
        var selectedDateForDetailsPreview by remember { mutableStateOf<LocalDate?>(null) }
        var dateForBorderOnlyPreview by remember { mutableStateOf<LocalDate?>(null) }

        val fourEventsSample = listOf(
            CalendarEvent(description = "주간 샘플 이벤트 1"),
            CalendarEvent(description = "주간 샘플 이벤트 2")
        )
        val previewWeeklyData = (0..6).map { dayOffset ->
            val date = today.with(JavaDayOfWeek.MONDAY).plusDays(dayOffset.toLong())
            WeeklyCalendarDay(
                date = date.dayOfMonth.toString(),
                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                isToday = date.isEqual(today),
                events = if (dayOffset == 0) fourEventsSample else emptyList()
            )
        }
        val previewEventsByDate = mapOf(
            today.with(JavaDayOfWeek.MONDAY) to fourEventsSample
        )

        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-5 주간 미리보기!",
            dDayText = "D-5",
            dDayTitle = "주간 행사 (미리보기)",
            randomCloudResIds = listOf(R.drawable.cloud1),
            currentYearMonth = currentMonthPreview,
            // currentYearMonthFormatted 파라미터 제거됨
            isMonthlyView = false,
            selectedDateForDetails = selectedDateForDetailsPreview,
            dateForBorderOnly = dateForBorderOnlyPreview, // 한 번만 전달
            eventsByDate = previewEventsByDate,
            weeklyCalendarData = previewWeeklyData,
            isQuestionAnsweredByAll = false,
            aiQuestion = "주간 뷰 AI 질문?",
            familyQuote = "\"주간 미리보기 명언\"",
            showAddEventInputScreen = false,
            isBottomBarVisible = selectedDateForDetailsPreview != null,
            onMonthChange = { np -> currentMonthPreview = np; selectedDateForDetailsPreview = null; dateForBorderOnlyPreview = null },
            onDateClick = { date -> if(date!=null) { selectedDateForDetailsPreview = date; dateForBorderOnlyPreview = null } }, // 간단한 버전
            onToggleCalendarView = {},
            onMonthlyCalendarHeaderTitleClick = {},
            onMonthlyCalendarHeaderIconClick = {},
            onRefreshQuestionClicked = {},
            onMonthlyTodayButtonClick = { val nowL = LocalDate.now(); currentMonthPreview = YearMonth.from(nowL); selectedDateForDetailsPreview = null; dateForBorderOnlyPreview = nowL },
            onEditEventRequest = { _, _ -> },
            onDeleteEventRequest = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "홈 (월간 뷰)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenMonthlyPreview() {
    DaytogetherTheme {
        val today = LocalDate.now()
        var currentMonthPreview by remember { mutableStateOf(YearMonth.now()) }
        var selectedDateForDetailsPreview by remember { mutableStateOf<LocalDate?>(null) }
        var dateForBorderOnlyPreview by remember { mutableStateOf<LocalDate?>(null) }

        val previewEventsByDate = mapOf(
            currentMonthPreview.atDay(12) to listOf(CalendarEvent(description = "월간 미리보기 이벤트")),
            today to listOf(CalendarEvent(description = "오늘의 월간 이벤트"))
        )

        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-10 월간 미리보기!",
            dDayText = "D-10",
            dDayTitle = "월간 행사 (미리보기)",
            randomCloudResIds = listOf(R.drawable.cloud2),
            currentYearMonth = currentMonthPreview,
            // currentYearMonthFormatted 파라미터 제거됨
            isMonthlyView = true,
            selectedDateForDetails = selectedDateForDetailsPreview,
            dateForBorderOnly = dateForBorderOnlyPreview, // 한 번만 전달
            eventsByDate = previewEventsByDate,
            weeklyCalendarData = emptyList(),
            isQuestionAnsweredByAll = true,
            aiQuestion = "월간 뷰 AI 질문?",
            familyQuote = "\"월간 미리보기 명언\"",
            showAddEventInputScreen = false,
            isBottomBarVisible = selectedDateForDetailsPreview != null,
            onMonthChange = { np -> currentMonthPreview = np; selectedDateForDetailsPreview = null; dateForBorderOnlyPreview = null },
            onDateClick = { date -> if(date!=null) {selectedDateForDetailsPreview = date; dateForBorderOnlyPreview = null}}, // 간단한 버전
            onToggleCalendarView = {},
            onMonthlyCalendarHeaderTitleClick = {},
            onMonthlyCalendarHeaderIconClick = {},
            onRefreshQuestionClicked = {},
            onMonthlyTodayButtonClick = { val nowL = LocalDate.now(); currentMonthPreview = YearMonth.from(nowL); selectedDateForDetailsPreview = null; dateForBorderOnlyPreview = nowL },
            onEditEventRequest = { _, _ -> },
            onDeleteEventRequest = { _, _ -> }
        )
    }
}