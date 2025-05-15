package com.example.daytogether.ui.home



// --- 기본 Compose 및 UI 관련 Imports ---
import com.example.daytogether.ui.home.composables.AnniversaryBoard
import com.example.daytogether.ui.home.composables.EventDetailsDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // DateEventsBottomSheet에서 사용
import androidx.compose.foundation.lazy.items // DateEventsBottomSheet에서 사용
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons // <<< Icons 네임스페이스 import
import androidx.compose.material.icons.filled.* // <<< Filled 스타일 아이콘들 (Close, Check, CalendarToday, Add, ChevronRight 등)
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle // DayCellNew에서 사용
import androidx.compose.ui.text.buildAnnotatedString // DayCellNew에서 사용
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle // DayCellNew에서 사용
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- 프로젝트 내부 의존성 Imports ---
import com.example.daytogether.R
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
import com.example.daytogether.ui.home.composables.MonthlyCalendarView // MonthlyCalendarView는 composables에 있다고 가정
import com.example.daytogether.ui.home.composables.YearMonthPickerDialog // YearMonthPickerDialog는 composables에 있다고 가정
import com.example.daytogether.ui.theme.*

// --- Java/Kotlin 표준 라이브러리 Imports ---
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.DayOfWeek as JavaDayOfWeek
import java.util.Locale
import kotlin.random.Random

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import java.text.SimpleDateFormat



// LocalDate.yearMonth 확장 프로퍼티 정의
val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

@Composable
fun HomeScreen() {
    var upcomingAnniversaryText by remember { mutableStateOf("D-3 가족 기념일! 오늘은 즐거운 하루 보내세요!") }
    var dDayTextState by remember { mutableStateOf("D-3") }
    var isAnniversaryAvailable by remember { mutableStateOf(true) }
    var dDayTitleState by remember { mutableStateOf(if (isAnniversaryAvailable) "엄마 생일" else "기념일 없음") }

    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val currentYearMonthFormatted = remember(currentYearMonth) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)
        currentYearMonth.format(formatter)
    }

    val today = LocalDate.now()
    var isMonthlyView by remember { mutableStateOf(false) }
    var selectedDateForDetails by remember { mutableStateOf<LocalDate?>(null) }
    var showYearMonthPickerDialog by remember { mutableStateOf(false) }

    // 이벤트 추가/수정 관련 상태
    var showAddEventInputViewFlag by remember { mutableStateOf(false) }
    var newEventDescriptionState by remember { mutableStateOf("") }
    var editingEventState by remember { mutableStateOf<Pair<LocalDate, CalendarEvent?>?>(null) } // Pair<날짜, 수정할 이벤트(없으면 null)>

    // DateEventsBottomSheet(여기서는 EventDetailsDialog로 대체) 관련 상태
    var showEventDetailsDialogFlag by remember { mutableStateOf(false) }


    val eventsByDateState = remember {
        mutableStateMapOf<LocalDate, List<CalendarEvent>>().apply {
            put(today.plusDays(1), listOf(CalendarEvent(description = "회의"), CalendarEvent(description = "점심")))
            put(today.plusDays(3), listOf(CalendarEvent(description = "발표"), CalendarEvent(description = "스터디"), CalendarEvent(description = "운동")))
        }
    }

    val weeklyCalendarDataState = remember(currentYearMonth, today, eventsByDateState) { // isMonthlyView 의존성 제거 (주간뷰는 항상 오늘 기준)
        val firstDayOfRelevantWeek = today.with(JavaDayOfWeek.MONDAY) // 주간 뷰는 항상 오늘이 포함된 주로 고정
        (0 until 7).map { dayOffset ->
            val date = firstDayOfRelevantWeek.plusDays(dayOffset.toLong())
            WeeklyCalendarDay(
                date = date.dayOfMonth.toString(),
                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                isToday = date.isEqual(today), // isToday 필드 순서 일치
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

    ActualHomeScreenContent(
        // --- 기존 UI 상태 파라미터 ---
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

        // --- 콜백 함수들 (ActualHomeScreenContent 정의와 일치시킴) ---
        onMonthChange = { newMonth ->
            currentYearMonth = newMonth
            selectedDateForDetails = null
        },
        onDateClick = { date -> // MonthlyCalendarView 또는 주간 달력 셀 클릭 시 (LocalDate? 받음)
            selectedDateForDetails = date
            if (date != null) {
                showEventDetailsDialogFlag = true // 날짜 클릭 시 EventDetailsDialog 표시
            }
        },
        onToggleCalendarView = { // 주간 뷰의 년월 텍스트 클릭 시 (월간 <-> 주간 토글)
            isMonthlyView = !isMonthlyView
            selectedDateForDetails = null // 뷰 변경 시 날짜 선택 해제
        },
        onMonthlyCalendarHeaderTitleClick = { // 월간 캘린더 헤더 년월 텍스트 클릭 시 (주간으로 돌아가기)
            isMonthlyView = false
            selectedDateForDetails = null
        },
        onMonthlyCalendarHeaderIconClick = { // 월간 캘린더 헤더 달력 아이콘 클릭 시
            showYearMonthPickerDialog = true
        },
        onRefreshQuestionClicked = {
            aiQuestionState = "새로운 AI 질문 생성 중..."
            isQuestionAnsweredByAllState = false
        },
        onEditEventRequest = { date, event -> // EventDetailsDialog 또는 다른 경로에서 호출
            editingEventState = date to event
            newEventDescriptionState = event.description
            showAddEventInputViewFlag = true
            showEventDetailsDialogFlag = false // 다른 입력창이 뜨므로 Dialog는 닫기
        },
        onDeleteEventRequest = { date, event -> // EventDetailsDialog 또는 다른 경로에서 호출
            eventsByDateState[date] = eventsByDateState[date]?.filterNot { it.id == event.id } ?: emptyList()
            if (eventsByDateState[date]?.isEmpty() == true) {
                eventsByDateState.remove(date)
            }
            if (selectedDateForDetails == date && eventsByDateState[date].isNullOrEmpty()) {
                showEventDetailsDialogFlag = false // 현재 보고 있는 날짜의 모든 일정이 삭제되면 Dialog 닫기
                selectedDateForDetails = null
            } else if (selectedDateForDetails == date) {
                // EventDetailsDialog를 강제로 recompose 시키기 위해 플래그를 잠시 껐다 켤 수 있으나,
                // eventsByDateState가 StateMap이므로 Dialog는 자동으로 갱신될 것임.
            }
        },
        onMonthlyTodayButtonClick = {
            val now = LocalDate.now()
            currentYearMonth = now.yearMonth
            selectedDateForDetails = now
            isMonthlyView = true // "오늘" 버튼은 월간 뷰 내에 있으므로 월간 뷰 유지
            if(eventsByDateState[now]?.isNotEmpty() == true) {
                showEventDetailsDialogFlag = true
            } else {
                showEventDetailsDialogFlag = false // 오늘 일정이 없으면 Dialog는 닫힌 상태 유지 또는 닫기
            }
        },

        // --- BottomSheet (상세 일정 목록) 관련 상태 및 콜백 ---
        // DateEventsBottomSheet 대신 EventDetailsDialog를 사용하므로, 관련 파라미터는 EventDetailsDialog 표시에 활용
        showDateEventsBottomSheet = showEventDetailsDialogFlag, // 이 플래그로 EventDetailsDialog 표시 제어
        targetDateForBottomSheet = selectedDateForDetails,    // EventDetailsDialog의 대상 날짜
        onDismissDateEventsSheet = {                          // EventDetailsDialog 닫힐 때
            showEventDetailsDialogFlag = false
            selectedDateForDetails = null
        },
        onAddNewEventFromSheetClick = {                       // EventDetailsDialog에서 "새 일정" 버튼 클릭 시 (가정) 또는 주간 뷰에서 새 일정 추가 시
            // 현재 EventDetailsDialog에는 새 일정 추가 버튼이 없음.
            // 이 콜백은 AddEventInputView를 직접 띄우는 역할로 변경
            val targetDate = selectedDateForDetails ?: today // 선택된 날짜가 없으면 오늘 날짜로
            editingEventState = targetDate to null
            newEventDescriptionState = ""
            showAddEventInputViewFlag = true
            showEventDetailsDialogFlag = false // Dialog 닫기
        },
        onEventItemAction = { event, actionType ->
            // 이 콜백은 EventDetailsDialog의 메뉴(수정/삭제)에서 직접 처리하므로,
            // ActualHomeScreenContent에서 직접 사용할 일은 적을 수 있음.
            // 필요시 로깅 또는 다른 액션 정의.
            println("Action '$actionType' for event '${event.description}' on date '$selectedDateForDetails'")
            if (actionType == "EDIT" && selectedDateForDetails != null) {
                editingEventState = selectedDateForDetails!! to event
                newEventDescriptionState = event.description
                showAddEventInputViewFlag = true
                showEventDetailsDialogFlag = false
            } else if (actionType == "DELETE" && selectedDateForDetails != null) {
                eventsByDateState[selectedDateForDetails!!] = eventsByDateState[selectedDateForDetails!!]?.filterNot { it.id == event.id } ?: emptyList()
                if (eventsByDateState[selectedDateForDetails!!]?.isEmpty() == true) { eventsByDateState.remove(selectedDateForDetails!!) }

                if (eventsByDateState[selectedDateForDetails!!].isNullOrEmpty()) {
                    showEventDetailsDialogFlag = false
                    selectedDateForDetails = null
                }
            }
        },

        // --- AddEventInputView (새 일정 입력) 관련 상태 및 콜백 ---
        showAddEventInputScreen = showAddEventInputViewFlag,
        newEventDescription = newEventDescriptionState,
        onNewEventDescriptionChange = { desc -> newEventDescriptionState = desc },
        onSaveNewEvent = {
            editingEventState?.let { (date, eventToEdit) ->
                if (newEventDescriptionState.isNotBlank()) {
                    val currentEvents = eventsByDateState[date]?.toMutableList() ?: mutableListOf()
                    if (eventToEdit != null) { // 수정 모드
                        val index = currentEvents.indexOfFirst { it.id == eventToEdit.id }
                        if (index != -1) {
                            currentEvents[index] = eventToEdit.copy(description = newEventDescriptionState)
                        } else { // ID를 못찾은 경우 (이론상 발생하기 어려움) 그냥 새로 추가
                            currentEvents.add(CalendarEvent(id=eventToEdit.id, description = newEventDescriptionState))
                        }
                    } else { // 새 이벤트 추가 모드
                        currentEvents.add(CalendarEvent(description = newEventDescriptionState))
                    }
                    eventsByDateState[date] = currentEvents
                }
            }
            showAddEventInputViewFlag = false
            newEventDescriptionState = ""
            editingEventState = null
        },
        onCancelNewEventInput = {
            showAddEventInputViewFlag = false
            newEventDescriptionState = ""
            editingEventState = null
        }
    )

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

    // EventDetailsDialog 표시 로직 (DateEventsBottomSheet 대신)
    selectedDateForDetails?.let { date ->
        if (showEventDetailsDialogFlag) {
            val eventsForDate = eventsByDateState[date] ?: emptyList()
            EventDetailsDialog(
                date = date,
                events = eventsForDate,
                onDismiss = {
                    showEventDetailsDialogFlag = false
                    // selectedDateForDetails = null // 다이얼로그 닫을 때 선택 해제는 onDismissDateEventsSheet에서 이미 처리
                },
                onEditEvent = { event ->
                    showEventDetailsDialogFlag = false // 현재 다이얼로그 닫고
                    editingEventState = date to event // 수정 상태 설정
                    newEventDescriptionState = event.description
                    showAddEventInputViewFlag = true   // 입력 뷰 표시
                },
                onDeleteEvent = { event ->
                    eventsByDateState[date] = eventsByDateState[date]?.filterNot { it.id == event.id } ?: emptyList()
                    if (eventsByDateState[date]?.isEmpty() == true) { eventsByDateState.remove(date) }

                    if (eventsByDateState[date].isNullOrEmpty()) { // 해당 날짜의 모든 이벤트가 삭제되면
                        showEventDetailsDialogFlag = false // 다이얼로그 닫기
                        selectedDateForDetails = null      // 날짜 선택도 해제
                    }
                    // else: 이벤트가 남아있으면 다이얼로그는 계속 떠 있고, 내부 LazyColumn이 갱신됨
                }
            )
        }
    }

    // AddEventInputView 표시 로직 (HomeScreen 최상단에서 상태에 따라 표시)
    // ActualHomeScreenContent는 이 뷰를 직접 그리지 않고, HomeScreen이 상태에 따라 화면 위에 오버레이처럼 띄움
    // 하지만 ActualHomeScreenContent의 showAddEventInputScreen 파라미터를 통해 상태를 전달하고,
    // HomeScreen.kt 파일 내에 정의된 AddEventInputView Composable을 ActualHomeScreenContent의 Box의 일부로 띄우는 것이
    // 레이어 관리나 UI 흐름상 더 자연스러울 수 있습니다.
    // 현재는 HomeScreen의 Box 최상위에 다른 컴포저블과 별도로 렌더링되도록 되어 있음.
    // 이는 ActualHomeScreenContent 함수 내부의 Box 아래에 배치되어야 함.
    // -> 이 부분은 ActualHomeScreenContent 함수 내부에서 showAddEventInputScreen 상태를 보고 처리하도록 변경하는 것이 좋음.
    //    여기서는 HomeScreen 최상위에서 관리하는 것으로 유지하되, 필요시 ActualHomeScreenContent 내부로 옮기는 것을 고려.

    // editingEventState?.first 는 targetDateForBottomSheet 와 동일한 역할을 할 수 있음
    editingEventState?.first?.let { targetDate ->
        AnimatedVisibility(visible = showAddEventInputViewFlag) { // AddEventInputView 자체에 AnimatedVisibility가 있지만, 여기서도 제어
            AddEventInputView( // HomeScreen.kt 파일 내에 정의된 Composable
                visible = true, // 내부 AnimatedVisibility 제어를 위해 true 전달 또는 AddEventInputView에서 visible 파라미터 제거
                targetDate = targetDate,
                eventDescription = newEventDescriptionState,
                onDescriptionChange = { newEventDescriptionState = it },
                onSave = { // onSaveNewEvent 콜백으로 연결
                    editingEventState?.let { (date, eventToEdit) ->
                        if (newEventDescriptionState.isNotBlank()) {
                            val currentEvents = eventsByDateState[date]?.toMutableList() ?: mutableListOf()
                            if (eventToEdit != null) { // 수정
                                val index = currentEvents.indexOfFirst { it.id == eventToEdit.id }
                                if (index != -1) {
                                    currentEvents[index] = eventToEdit.copy(description = newEventDescriptionState)
                                } else {
                                    currentEvents.add(CalendarEvent(id=eventToEdit.id, description = newEventDescriptionState))
                                }
                            } else { // 추가
                                currentEvents.add(CalendarEvent(description = newEventDescriptionState))
                            }
                            eventsByDateState[date] = currentEvents
                        }
                    }
                    showAddEventInputViewFlag = false
                    newEventDescriptionState = ""
                    editingEventState = null
                },
                onCancel = {
                    showAddEventInputViewFlag = false
                    newEventDescriptionState = ""
                    editingEventState = null
                }
            )
        }
    }
} // End of HomeScreen Composable

@Composable
fun DateEventsBottomSheet(
    visible: Boolean,
    targetDate: LocalDate,
    events: List<CalendarEvent>,
    onDismiss: () -> Unit,
    onAddNewEventClick: () -> Unit,
    onEventItemClick: (CalendarEvent) -> Unit, // EventListItem에서 호출됨
    modifier: Modifier = Modifier
) {

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN)



// 실제 BottomSheet를 사용하려면 ModalBottomSheetLayout 사용 필요.

// 여기서는 AnimatedVisibility와 Card를 사용하여 유사한 효과를 냅니다.

    AnimatedVisibility(

        visible = visible,

        enter = slideInVertically(animationSpec = tween(durationMillis = 250), initialOffsetY = { it }),

        exit = slideOutVertically(animationSpec = tween(durationMillis = 200), targetOffsetY = { it }),

        modifier = modifier.fillMaxWidth()

    ) {

        Card(

            modifier = Modifier

                .fillMaxWidth()

                .defaultMinSize(minHeight = 200.dp), // 최소 높이 지정

            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),

            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),

            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // 일반 표면색

        ) {

            Column(

                modifier = Modifier.padding(16.dp)

            ) {

// 헤더: 선택된 날짜 표시

                Row(

                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),

                    horizontalArrangement = Arrangement.SpaceBetween,

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(

                        text = targetDate.format(dateFormatter),

                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)

                    )

// 닫기 버튼 (선택 사항, 보통 아래로 스와이프하거나 바깥쪽 클릭으로 닫음)

                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {

                        Icon(Icons.Filled.Close, contentDescription = "닫기", tint = TextPrimary.copy(alpha = 0.7f))

                    }

                }



                HorizontalDivider(color = TextPrimary.copy(alpha = 0.2f))



// 이벤트 목록 또는 "+ 새 일정 만들기" 버튼

                if (events.isEmpty()) {

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(vertical = 32.dp), // 중앙에 오도록 패딩

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

                } else {

                    LazyColumn(

                        modifier = Modifier

                            .weight(1f, fill = false) // 내용만큼만 높이 차지, 최대 높이는 Card의 minHeight에 의해 제한될 수 있음

                            .padding(top = 8.dp, bottom = 8.dp)

                    ) {

                        items(events, key = { it.id }) { event ->

                            EventListItem(event = event, onClick = { onEventItemClick(event) })

                            HorizontalDivider(color = TextPrimary.copy(alpha = 0.1f))

                        }

                    }

// 목록 하단에 "+ 새 일정 만들기" 버튼 (항상 표시)

                    Button(

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

fun EventListItem(

    event: CalendarEvent,

    onClick: () -> Unit, // 이벤트를 클릭했을 때의 동작 (수정/삭제 메뉴 표시용)

    modifier: Modifier = Modifier

) {

    Row(

        modifier = modifier

            .fillMaxWidth()

            .clickable { onClick() }

            .padding(vertical = 16.dp, horizontal = 8.dp), // 상하 패딩으로 각 항목 구분

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween // 내용과 아이콘을 양 끝으로

    ) {

// 일정 제목(description)만 표시

        Text(

            text = event.description,

            style = MaterialTheme.typography.bodyLarge, // 폰트 스타일

            color = TextPrimary, // 텍스트 색상

            modifier = Modifier.weight(1f) // 제목이 길 경우 아이콘이 밀려나지 않도록

        )



// 오른쪽에 > 아이콘 표시

        Icon(

            imageVector = Icons.Filled.ChevronRight,

            contentDescription = "일정 옵션 보기", // 접근성용 설명

            tint = TextPrimary.copy(alpha = 0.7f) // 아이콘 색상 (약간 연하게)

        )

    }

}





@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun AddEventInputView(

    visible: Boolean,

    targetDate: LocalDate, // 새 일정을 추가할 대상 날짜

    eventDescription: String,

    onDescriptionChange: (String) -> Unit,

    onSave: () -> Unit,

    onCancel: () -> Unit,

    modifier: Modifier = Modifier

) {

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd (E)", Locale.KOREAN) // "새 일정 추가.png" 형식

    val focusManager = LocalFocusManager.current



// "일정추가.jpg"의 전체적인 따뜻한 배경색 (ScreenBackground 또는 유사한 색)

    val cardBackgroundColor = ScreenBackground



    AnimatedVisibility(

        visible = visible,

        enter = slideInVertically(animationSpec = tween(durationMillis = 250), initialOffsetY = { it }),

        exit = slideOutVertically(animationSpec = tween(durationMillis = 200), targetOffsetY = { it }),

        modifier = modifier.fillMaxWidth()

    ) {

        Card(

            modifier = Modifier

                .fillMaxWidth()

                .shadow(elevation = 0.dp, shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)) // 그림자 제거 또는 최소화

                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)), // 상단만 둥글게

            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),

            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)

        ) {

            Column(

                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp) // 하단 패딩 추가

            ) {

// 헤더: 취소, "새 일정", 저장("확인" 대신 "저장" 텍스트 사용)

                Row(

                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    TextButton(onClick = { focusManager.clearFocus(); onCancel() }) {

                        Text("취소", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary.copy(alpha = 0.8f)))

                    }

                    Text(

                        "새 일정",

                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)

                    )

                    TextButton(

                        onClick = { if (eventDescription.isNotBlank()) { focusManager.clearFocus(); onSave() } },

                        enabled = eventDescription.isNotBlank()

                    ) {

                        Text(

                            "저장", // "확인" 대신 "저장"

                            style = MaterialTheme.typography.bodyLarge.copy(

                                color = if (eventDescription.isNotBlank()) ButtonActiveBackground else TextPrimary.copy(alpha = 0.4f),

                                fontWeight = FontWeight.Bold

                            )

                        )

                    }

                }



// 제목 입력 필드 ("새 일정 추가.png" 레이아웃)

                OutlinedTextField(

                    value = eventDescription,

                    onValueChange = onDescriptionChange,

                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),

                    label = { Text("제목", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary.copy(alpha = 0.7f))) },

                    placeholder = { Text("일정 제목을 입력하세요", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary.copy(alpha = 0.5f))) },

                    singleLine = true,

                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),

                    keyboardActions = KeyboardActions(onDone = {

                        if (eventDescription.isNotBlank()) {

                            focusManager.clearFocus()

                            onSave()

                        } else {

                            focusManager.clearFocus() // 내용 없으면 포커스만 해제

                        }

                    }),

                    colors = TextFieldDefaults.colors( // "일정추가.jpg" 색감 참고

                        focusedContainerColor = Color.Transparent,

                        unfocusedContainerColor = Color.Transparent,

                        disabledContainerColor = Color.Transparent,

                        focusedIndicatorColor = TextPrimary,

                        unfocusedIndicatorColor = TextPrimary.copy(alpha = 0.3f),

                        focusedLabelColor = TextPrimary,

                        cursorColor = TextPrimary,

                        focusedTextColor = TextPrimary,

                        unfocusedTextColor = TextPrimary.copy(alpha = 0.8f)

                    ),

                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)

                )



// 날짜 표시 ("새 일정 추가.png" 레이아웃)

                Row(

                    verticalAlignment = Alignment.CenterVertically,

                    modifier = Modifier

                        .fillMaxWidth()

                        .clip(RoundedCornerShape(8.dp)) // 배경에 둥근 모서리

                        .background(TextPrimary.copy(alpha = 0.05f)) // 연한 배경색

                        .padding(horizontal = 12.dp, vertical = 10.dp)

                ) {

                    Icon(

                        imageVector = Icons.Filled.CalendarToday,

                        contentDescription = "날짜",

                        tint = TextPrimary.copy(alpha = 0.7f),

                        modifier = Modifier.size(20.dp)

                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(

                        text = targetDate.format(dateFormatter),

                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),

                        color = TextPrimary

                    )

                }

// "일정추가.jpg"의 "캘린더 >" 부분은 현재 구현 범위에서는 제외 (단순 날짜 표시)

            }

        }

    }

}





@OptIn(ExperimentalFoundationApi::class)

@Composable

fun ActualHomeScreenContent(

// --- 기존 UI 상태 파라미터 ---

    upcomingAnniversaryText: String,

    dDayText: String,

    dDayTitle: String,

    randomCloudResIds: List<Int>,

    currentYearMonth: YearMonth,

    currentYearMonthFormatted: String,

    isMonthlyView: Boolean,

    selectedDateForDetails: LocalDate?,

    eventsByDate: Map<LocalDate, List<CalendarEvent>>,

    weeklyCalendarData: List<WeeklyCalendarDay>,

    isQuestionAnsweredByAll: Boolean,

    aiQuestion: String,

    familyQuote: String,



// --- 콜백 함수들 ---

    onMonthChange: (YearMonth) -> Unit,

    onDateClick: (LocalDate?) -> Unit,

    onToggleCalendarView: () -> Unit, // 화면 상단 년월 텍스트 클릭 시

    onMonthlyCalendarHeaderTitleClick: () -> Unit, // 월간 캘린더 헤더 년월 텍스트 클릭 시

    onMonthlyCalendarHeaderIconClick: () -> Unit, // 월간 캘린더 헤더 달력 아이콘 클릭 시

    onRefreshQuestionClicked: () -> Unit,

    onEditEventRequest: (LocalDate, CalendarEvent) -> Unit,

    onDeleteEventRequest: (LocalDate, CalendarEvent) -> Unit,

    onMonthlyTodayButtonClick: () -> Unit,



// --- BottomSheet (상세 일정 목록) 관련 상태 및 콜백 ---

    showDateEventsBottomSheet: Boolean,

    targetDateForBottomSheet: LocalDate?,

    onDismissDateEventsSheet: () -> Unit,

    onAddNewEventFromSheetClick: () -> Unit,

    onEventItemAction: (event: CalendarEvent, actionType: String) -> Unit,



// --- AddEventInputView (새 일정 입력) 관련 상태 및 콜백 ---

    showAddEventInputScreen: Boolean,

    newEventDescription: String,

    onNewEventDescriptionChange: (String) -> Unit,

    onSaveNewEvent: () -> Unit,

    onCancelNewEventInput: () -> Unit

) {

    val today = LocalDate.now()

    val weeklyCalendarBackgroundColor = TextPrimary.copy(alpha = 0.05f)



    Box(modifier = Modifier.fillMaxSize()) {

        Column(

            modifier = Modifier

                .fillMaxSize()

                .background(ScreenBackground)

        ) {

            AnniversaryBoard(text = upcomingAnniversaryText)

            Spacer(modifier = Modifier.height(24.dp))

            DDaySectionView(

                dDayText = dDayText,

                dDayTitle = dDayTitle,

                cloudImageResList = randomCloudResIds,

                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)

            )

            Spacer(modifier = Modifier.height(24.dp))



            Column(

                modifier = Modifier

                    .weight(1f)

                    .padding(horizontal = 20.dp)

            ) {

                if (!isMonthlyView) {

                    Text(

                        text = currentYearMonthFormatted,

                        color = TextPrimary,

                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),

                        modifier = Modifier

                            .align(Alignment.Start)

                            .clickable { onToggleCalendarView() } // 화면 상단 년월 텍스트 클릭

                            .padding(bottom = 16.dp)

                    )

                } else {

                    Spacer(modifier = Modifier.height(MaterialTheme.typography.headlineSmall.fontSize.value.dp + 16.dp))

                }



                if (isMonthlyView) {

                    MonthlyCalendarView(

                        currentMonth = currentYearMonth,

                        onMonthChange = onMonthChange,

                        onDateClick = onDateClick,

                        eventsByDate = eventsByDate,

                        selectedDateForDetails = selectedDateForDetails,

                        modifier = Modifier.fillMaxWidth(),

                        onEditEventRequest = onEditEventRequest, // 전달

                        onDeleteEventRequest = onDeleteEventRequest, // 전달

                        onTitleClick = onMonthlyCalendarHeaderTitleClick, // 월간 헤더 년월 텍스트 클릭

                        onCalendarIconClick = onMonthlyCalendarHeaderIconClick, // 월간 헤더 달력 아이콘 클릭

                        onTodayHeaderButtonClick = onMonthlyTodayButtonClick // 월간 헤더 "오늘" 버튼 클릭 전달

                    )

                } else { // 주간 뷰

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                            .background(weeklyCalendarBackgroundColor, RoundedCornerShape(12.dp))

                            .padding(horizontal = 1.dp, vertical = 1.dp)

                    ) {

                        Row(

                            modifier = Modifier.fillMaxWidth().height(120.dp),

                            ) {

                            weeklyCalendarData.forEachIndexed { index, dayData ->

                                DayCellNew(

                                    dateData = dayData,

                                    modifier = Modifier.weight(1f).fillMaxHeight()

                                )

                                if (index < weeklyCalendarData.size - 1) {

                                    VerticalDivider(

                                        color = TextPrimary.copy(alpha = 0.15f),

                                        thickness = 1.dp,

                                        modifier = Modifier.fillMaxHeight().padding(vertical = 6.dp)

                                    )

                                }

                            }

                        }

                    }

                }

                Spacer(modifier = Modifier.height(24.dp))

                TodayQuestionHeaderWithAlert(isAnsweredByAll = isQuestionAnsweredByAll)

                Spacer(modifier = Modifier.height(12.dp))

                TodayQuestionContentCard(questionText = aiQuestion)

                Spacer(modifier = Modifier.height(18.dp))

                RefreshQuestionButton(

                    isAnsweredByAll = isQuestionAnsweredByAll,

                    onRefreshQuestionClicked = onRefreshQuestionClicked,

                    modifier = Modifier.align(Alignment.CenterHorizontally)

                )

                Spacer(modifier = Modifier.height(16.dp))

            }



            val showQuote = !isMonthlyView && !showDateEventsBottomSheet && !showAddEventInputScreen

            if (showQuote) {

                QuoteView(

                    quote = familyQuote,

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(horizontal = 20.dp, vertical = 20.dp)

                )

            } else {

                Spacer(modifier = Modifier.height(20.dp))

            }

        } // End of main content Column



// 하단 UI (상세 일정 목록 또는 새 일정 입력)

        if (showDateEventsBottomSheet && targetDateForBottomSheet != null) {

            DateEventsBottomSheet(

                visible = true, // AnimatedVisibility는 DateEventsBottomSheet 내부에서 처리 가정

                targetDate = targetDateForBottomSheet,

                events = eventsByDate[targetDateForBottomSheet] ?: emptyList(),

                onDismiss = onDismissDateEventsSheet,

                onAddNewEventClick = onAddNewEventFromSheetClick,

// onEventItemClick의 람다 파라미터에 타입을 명시적으로 지정

                onEventItemClick = { event: CalendarEvent -> onEventItemAction(event, "SHOW_OPTIONS") },

                modifier = Modifier.align(Alignment.BottomCenter)

            )

        }



        if (showAddEventInputScreen && targetDateForBottomSheet != null) {

            AddEventInputView(

                visible = true, // AnimatedVisibility는 AddEventInputView 내부에서 처리 가정

                targetDate = targetDateForBottomSheet,

                eventDescription = newEventDescription,

                onDescriptionChange = onNewEventDescriptionChange,

                onSave = onSaveNewEvent,

                onCancel = onCancelNewEventInput,

                modifier = Modifier.align(Alignment.BottomCenter)

            )

        }

    } // End of Box

}









@Composable

fun DDaySectionView(

    dDayText: String,

    dDayTitle: String,

    cloudImageResList: List<Int>,

    modifier: Modifier = Modifier

) {

    Row( // D-Day 정보와 구름 이미지를 가로로 배치하는 전체 Row는 유지

        modifier = modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically // 수직 중앙 정렬

    ) {

        Column( // D-3와 엄마생일을 세로로 배치

            horizontalAlignment = Alignment.Start // 내부 텍스트들 왼쪽 정렬

        ) {

            Text(

                text = dDayText, // 예: "D-3"

                style = MaterialTheme.typography.displayMedium.copy(

                    fontWeight = FontWeight.Medium,

                    color = TextPrimary

                )

            )

            Spacer(modifier = Modifier.height(4.dp)) // D-3와 제목 사이 간격

            Text(

                text = dDayTitle, // 예: "엄마 생일"

                style = MaterialTheme.typography.titleMedium.copy(

                    color = TextPrimary



                )

            )

        }



        Spacer(modifier = Modifier.weight(1f)) // 구름 이미지를 오른쪽으로 밀기



// 클라우드 이미지 로직 (오른쪽에 배치)

        cloudImageResList.forEach { resId ->

            Image(

                painter = painterResource(id = resId),

                contentDescription = "cloud",

                modifier = Modifier

                    .size(130.dp) // 이미지 크기 (기존과 동일 또는 조정)

                    .padding(start = 0.dp) // 이미지 간 간격

            )

        }

    }

}



@Composable

fun DayCellNew(

    dateData: WeeklyCalendarDay,

    modifier: Modifier = Modifier

) {

    Column(

        horizontalAlignment = Alignment.CenterHorizontally,

        modifier = modifier

            .fillMaxHeight() // 부모 Row의 높이를 따름 (여기서는 약 120dp 근처)

            .padding(horizontal = 4.dp, vertical = 6.dp), // 셀 내부 상하 패딩 조정

        verticalArrangement = Arrangement.Top

    ) {

// 상단: "숫자(요일)" 헤더

        val annotatedDateString = buildAnnotatedString {

            withStyle(style = SpanStyle(fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)) { // 숫자 크기 약간 줄임

                append(dateData.date)

            }

            withStyle(style = SpanStyle(fontSize = 9.sp, color = TextPrimary.copy(alpha = 0.7f))) { // 요일 크기 약간 줄임

                append("(${dateData.dayOfWeek})")

            }

        }

        Text(

            text = annotatedDateString,

            textAlign = TextAlign.Center,

            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp) // 헤더와 구분선 사이 간격

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

                .weight(1f) // 남은 공간을 모두 차지하도록 하여 이벤트가 아래로 밀리지 않게 함

                .fillMaxWidth(),

            horizontalAlignment = Alignment.Start,

            verticalArrangement = Arrangement.spacedBy(2.dp) // 이벤트 간 수직 간격

        ) {

            dateData.events.take(3).forEach { event -> // 최대 3개의 이벤트 표시

                Text(

                    text = event.description,

                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),

                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), // 이벤트 폰트 크기 약간 줄임

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



@Composable

fun TodayQuestionHeaderWithAlert(isAnsweredByAll: Boolean, modifier: Modifier = Modifier) {

    Row(

        modifier = modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("오늘의 질문", color = TextPrimary, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium, fontSize = 21.sp))

            Spacer(modifier = Modifier.width(6.dp))

            Icon(

                imageVector = ImageVector.vectorResource(id = if (isAnsweredByAll) R.drawable.ic_happy else R.drawable.ic_sad),

                contentDescription = if (isAnsweredByAll) "답변 완료" else "답변 필요",

                tint = TextPrimary, // 아이콘 색상 테마에 맞게

                modifier = Modifier.size(24.dp)

            )

        }

        Spacer(Modifier.weight(1f)) // 오른쪽으로 밀기

        if (!isAnsweredByAll) {

            Text(

                text = "아직 오늘의 질문에 답변하지 않았어요!",

                color = ErrorRed, // 에러/알림 색상

                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 12.sp),

                textAlign = TextAlign.End

            )

        }

    }

}



@Composable

fun TodayQuestionContentCard(questionText: String, modifier: Modifier = Modifier) {

    Box(

        modifier = modifier

            .fillMaxWidth()

            .defaultMinSize(minHeight = 90.dp) // 최소 높이

            .clip(RoundedCornerShape(10.dp))

            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)) // 카드 배경색 (테마에 따라 조정)

            .border(BorderStroke(1.dp, TextPrimary.copy(alpha = 0.7f)), RoundedCornerShape(10.dp)), // 테두리 색상 조정

        contentAlignment = Alignment.Center

    ) {

        Text(

            text = questionText,

            color = TextPrimary,

            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), // 글꼴 스타일 조정

            textAlign = TextAlign.Center,

            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp), // 내부 패딩

            lineHeight = 20.sp

        )

    }

}





@Composable

fun RefreshQuestionButton(

    isAnsweredByAll: Boolean,

    onRefreshQuestionClicked: () -> Unit,

    modifier: Modifier = Modifier

){

    Button(

        onClick = onRefreshQuestionClicked,

        enabled = isAnsweredByAll, // 모든 가족 답변 시 활성화

        shape = RoundedCornerShape(8.dp),

        colors = ButtonDefaults.buttonColors(

            containerColor = ButtonActiveBackground,

            contentColor = ButtonActiveText,

            disabledContainerColor = ButtonDisabledBackground,

            disabledContentColor = ButtonDisabledText

        ),

        modifier = modifier

            .fillMaxWidth(0.50f) // 버튼 너비 조정

            .height(38.dp) // 버튼 높이

    ) {

        Text(

            "질문 한 번 더 받기",

            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),

            maxLines = 1

        )

    }

}



@Composable

fun QuoteView(quote: String, modifier: Modifier = Modifier) {

    Text(

        text = quote,

        color = TextPrimary.copy(alpha = 0.7f),

        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp), // 명언 폰트 크기

        textAlign = TextAlign.Center,

        modifier = modifier

            .fillMaxWidth()

// .padding(vertical = 12.dp) // 외부에서 vertical padding 제어

    )

}



@Preview(showBackground = true, name = "홈 (주간 뷰)", widthDp = 390, heightDp = 844)

@Composable

fun DefaultHomeScreenWeeklyPreview() {

    DaytogetherTheme {

        val today = LocalDate.now()

        val previewWeeklyData = (0..6).map { dayOffset ->

            val date = today.with(JavaDayOfWeek.MONDAY).plusDays(dayOffset.toLong())

            WeeklyCalendarDay(

                date = date.dayOfMonth.toString(),

                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),

                isToday = date.isEqual(today),

                events = if(dayOffset % 2 == 0) listOf(CalendarEvent(description = "주간이벤트")) else emptyList()

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

            eventsByDate = mapOf(

                today.plusDays(1) to listOf(CalendarEvent(description = "미리보기 주간 이벤트 1")),

                today.plusDays(3) to listOf(CalendarEvent(description = "미리보기 주간 이벤트 2"))

            ),

            weeklyCalendarData = previewWeeklyData,

            isQuestionAnsweredByAll = false,

            aiQuestion = "이번 주말에 함께 하고 싶은 활동은 무엇인가요?",

            familyQuote = "\"가족은 사랑의 시작이자 끝이다.\" - 마더 테레사",

            onMonthChange = {},

            onDateClick = {},

            onToggleCalendarView = {},

            onMonthlyCalendarHeaderTitleClick = {},

            onMonthlyCalendarHeaderIconClick = {},

            onMonthlyTodayButtonClick = {}, // 추가

            onRefreshQuestionClicked = {},

            onEditEventRequest = { _, _ -> },

            onDeleteEventRequest = { _, _ -> },

            showDateEventsBottomSheet = false,

            targetDateForBottomSheet = null,

            onDismissDateEventsSheet = {},

            onAddNewEventFromSheetClick = {},

            onEventItemAction = { _, _ -> },

            showAddEventInputScreen = false,

            newEventDescription = "",

            onNewEventDescriptionChange = {},

            onSaveNewEvent = {},

            onCancelNewEventInput = {}

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

            weeklyCalendarData = emptyList(),

            isQuestionAnsweredByAll = true,

            aiQuestion = "가장 좋아하는 가족 여행지는?",

            familyQuote = "\"다른 무엇보다도, 준비가 성공의 열쇠이다.\" - 알렉산더 그레이엄 벨",

            onMonthChange = {},

            onDateClick = {},

            onToggleCalendarView = {},

            onMonthlyCalendarHeaderTitleClick = {},

            onMonthlyCalendarHeaderIconClick = {},

            onMonthlyTodayButtonClick = {}, // 추가

            onRefreshQuestionClicked = {},

            onEditEventRequest = { _,_ -> },

            onDeleteEventRequest = { _,_ -> },

            showDateEventsBottomSheet = false,

            targetDateForBottomSheet = null,

            onDismissDateEventsSheet = {},

            onAddNewEventFromSheetClick = {},

            onEventItemAction = { _, _ -> },

            showAddEventInputScreen = false,

            newEventDescription = "",

            onNewEventDescriptionChange = {},

            onSaveNewEvent = {},

            onCancelNewEventInput = {}

        )

    }

}