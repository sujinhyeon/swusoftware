package com.example.daytogether.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.R
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
// MonthlyCalendarView 임포트 경로 확인! (ui.home.composables 또는 ui.home)
import com.example.daytogether.ui.home.MonthlyCalendarView
import com.example.daytogether.ui.theme.*
import java.text.SimpleDateFormat
import java.time.LocalDate // LocalDate 임포트
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

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
    var monthlySelectedDate by remember { mutableStateOf<LocalDate?>(null) } // 월간 캘린더 선택 날짜

    val weeklyCalendarDataState = remember(currentYearMonth, today) {
        val todayDateStr = today.dayOfMonth.toString()
        listOf(
            WeeklyCalendarDay("14", "일", isToday = "14" == todayDateStr && currentYearMonth.month == today.month && currentYearMonth.year == today.year),
            WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의"), CalendarEvent("미팅 길어지면 이렇게 표시됩니다.")), isToday = "15" == todayDateStr && currentYearMonth.month == today.month && currentYearMonth.year == today.year),
            WeeklyCalendarDay("16", "화", events = listOf(CalendarEvent("점심 약속입니다")), isToday = "16" == todayDateStr && currentYearMonth.month == today.month && currentYearMonth.year == today.year),
            WeeklyCalendarDay("17", "수", events = listOf(CalendarEvent("저녁 약속"), CalendarEvent("영화 보기"), CalendarEvent("과제")), isToday = "17" == todayDateStr && currentYearMonth.month == today.month && currentYearMonth.year == today.year),
            WeeklyCalendarDay("18", "목", events = listOf(CalendarEvent("발표"), CalendarEvent("과제"), CalendarEvent("스터디"), CalendarEvent("저녁")), isToday = "18" == todayDateStr && currentYearMonth.month == today.month && currentYearMonth.year == today.year),
            WeeklyCalendarDay("19", "금", isToday = "19" == todayDateStr && currentYearMonth.month == today.month && currentYearMonth.year == today.year),
            WeeklyCalendarDay("20", "토", events = listOf(CalendarEvent("영화 보러 가는 날")), isToday = "20" == todayDateStr && currentYearMonth.month == today.month && currentYearMonth.year == today.year)
        )
    }
    var isQuestionAnsweredByAllState by remember { mutableStateOf(false) }
    var aiQuestionState by remember { mutableStateOf("우리 가족만의 별명(애칭)이 있나요?") }
    var familyQuoteState by remember { mutableStateOf("\"가족은 자격이 아니라 특권이다.\" - 클린트 이스트우드") }

    val cloudImages = remember {
        listOf(
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3, R.drawable.cloud4,
            R.drawable.cloud5, R.drawable.cloud6, R.drawable.cloud7, R.drawable.cloud8, R.drawable.cloud9
        )
    }
    val numberOfClouds by remember { mutableStateOf(Random.nextInt(1, 3)) }
    val randomCloudResIdsState by remember { mutableStateOf(cloudImages.shuffled().take(numberOfClouds)) }

    ActualHomeScreenContent(
        upcomingAnniversaryText = upcomingAnniversaryText,
        dDayText = dDayTextState,
        dDayTitle = dDayTitleState,
        currentYearMonth = currentYearMonth,
        currentYearMonthFormatted = currentYearMonthFormatted,
        weeklyCalendarData = weeklyCalendarDataState,
        isMonthlyView = isMonthlyView,
        onToggleCalendarView = { isMonthlyView = !isMonthlyView },
        onMonthChange = { newMonth ->
            currentYearMonth = newMonth
            monthlySelectedDate = null // 월 변경 시 선택 해제
        },
        monthlySelectedDate = monthlySelectedDate, // 월간 캘린더 선택 날짜 전달
        onMonthlyDateSelected = { date -> monthlySelectedDate = date }, // 월간 캘린더 날짜 선택 콜백
        isQuestionAnsweredByAll = isQuestionAnsweredByAllState,
        aiQuestion = aiQuestionState,
        familyQuote = familyQuoteState,
        randomCloudResIds = randomCloudResIdsState,
        onRefreshQuestionClicked = {
            aiQuestionState = "새로운 AI 질문 생성 중..."
            isQuestionAnsweredByAllState = false
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActualHomeScreenContent(
    upcomingAnniversaryText: String,
    dDayText: String,
    dDayTitle: String,
    currentYearMonth: YearMonth,
    currentYearMonthFormatted: String,
    weeklyCalendarData: List<WeeklyCalendarDay>,
    isMonthlyView: Boolean,
    onToggleCalendarView: () -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    monthlySelectedDate: LocalDate?, // 월간 캘린더용 선택된 날짜
    onMonthlyDateSelected: (LocalDate) -> Unit, // 월간 캘린더용 날짜 선택 콜백
    isQuestionAnsweredByAll: Boolean,
    aiQuestion: String,
    familyQuote: String,
    randomCloudResIds: List<Int>,
    onRefreshQuestionClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        AnniversaryBoard(text = upcomingAnniversaryText, modifier = Modifier.fillMaxWidth())

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            DDaySectionView(
                dDayText = dDayText,
                dDayTitle = dDayTitle,
                cloudImageResList = randomCloudResIds
            )
        }


        Column(modifier = Modifier.padding(horizontal = 22.dp)) { // 캘린더 섹션
            Text(
                text = currentYearMonthFormatted,
                color = TextPrimary,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable {
                        // 년/월 텍스트 클릭 시 항상 주간/월간 뷰 토글
                        onToggleCalendarView()
                        // 월간 뷰 상태에서 년/월 텍스트를 다시 클릭하면 주간 뷰로 돌아감.
                        // 월간 캘린더 헤더 내부의 년/월 텍스트를 클릭해야 년/월 피커가 뜨도록 MonthlyCalendarView에서 onShowYearMonthPicker를 사용.
                    }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (isMonthlyView) {
                MonthlyCalendarView(
                    currentMonth = currentYearMonth,
                    onMonthChange = onMonthChange,
                    onDateClick = onMonthlyDateSelected,
                    onAddEventClick = { /* TODO */ },
                    onShowYearMonthPicker = { /* TODO: 실제 년/월 피커 표시 로직 연결 */ },
                    selectedDateForDetails = monthlySelectedDate,
                    modifier = Modifier.fillMaxWidth()
                )

            } else {
                // 주간 캘린더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(BorderStroke(1.dp, TextPrimary), RoundedCornerShape(10.dp))
                        .padding(horizontal = 2.dp, vertical = 8.dp),
                ) {
                    weeklyCalendarData.forEachIndexed { index, dayData ->
                        DayCellNew(
                            dateData = dayData,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        if (index < weeklyCalendarData.size - 1) {
                            VerticalDivider(
                                color = TextPrimary.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxHeight().width(1.dp).padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            TodayQuestionHeaderWithAlert(isAnsweredByAll = isQuestionAnsweredByAll)
            Spacer(modifier = Modifier.height(12.dp))
            TodayQuestionContentCard(questionText = aiQuestion)
            Spacer(modifier = Modifier.height(18.dp))
            RefreshQuestionButton(
                isAnsweredByAll = isQuestionAnsweredByAll,
                onRefreshQuestionClicked = onRefreshQuestionClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        QuoteView(
            quote = familyQuote,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnniversaryBoard(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(AnniversaryBoardBackground, shape = RectangleShape)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextPrimary,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light, fontSize = 14.sp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .basicMarquee(iterations = Int.MAX_VALUE, velocity = 35.dp)
        )
    }
}

@Composable
fun DDaySectionView(dDayText: String, dDayTitle: String, cloudImageResList: List<Int>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = dDayText, color = TextPrimary, style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, fontSize = 48.sp))
            Text(
                text = dDayTitle,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 20.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 100.dp)
            )
        }
        Row(
            modifier = Modifier.weight(1f).height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            cloudImageResList.forEach { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "구름",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

// 주간 캘린더의 DayCellNew
@Composable
fun DayCellNew(
    dateData: WeeklyCalendarDay,
    modifier: Modifier = Modifier
) {
    val dayColor = TextPrimary
    val cellContentBackgroundColor = if (dateData.isToday) AnniversaryBoardBackground.copy(alpha = 0.5f) else Color.Transparent
    val cellBorderColor = if (dateData.isToday) TextPrimary else Color.Transparent
    val cellBorderThickness = if (dateData.isToday) 1.5.dp else 0.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(0.dp))
            .border(BorderStroke(cellBorderThickness, cellBorderColor), RoundedCornerShape(0.dp))
            .padding(vertical = 4.dp, horizontal = 1.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (dateData.isToday) cellContentBackgroundColor else Color.Transparent, shape = RoundedCornerShape(4.dp))
                .padding(vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${dateData.dayOfWeek}(${dateData.date})",
                color = if (dateData.isToday) TextPrimary else dayColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold, // 요일 Bold
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
        ) {
            if (dateData.events.isNotEmpty()) {
                dateData.events.take(3).forEach { event ->
                    Text(
                        text = event.description,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
                    )
                }
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
            Text("오늘의 질문", color = TextPrimary, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 21.sp))
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = ImageVector.vectorResource(id = if (isAnsweredByAll) R.drawable.ic_happy else R.drawable.ic_sad),
                contentDescription = if (isAnsweredByAll) "답변 완료" else "답변 필요",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        if (!isAnsweredByAll) {
            Text(
                text = "아직 오늘의 질문에\n답변하지 않은 가족이 있어요!",
                color = ErrorRed,
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
            .defaultMinSize(minHeight = 90.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, TextPrimary), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = questionText,
            color = TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
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
        enabled = isAnsweredByAll,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonActiveBackground,
            contentColor = ButtonActiveText,
            disabledContainerColor = ButtonDisabledBackground,
            disabledContentColor = ButtonDisabledText
        ),
        modifier = modifier
            .fillMaxWidth(0.50f)
            .height(38.dp)
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
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}

// --- Previews ---
@Preview(showBackground = true, name = "홈 (주간 뷰)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenWeeklyPreview() {
    DaytogetherTheme {
        val today = java.time.LocalDate.now()
        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-3 가족 기념일!",
            dDayText = "D-3",
            dDayTitle = "엄마 생일",
            currentYearMonth = YearMonth.now(),
            currentYearMonthFormatted = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)),
            weeklyCalendarData = listOf(
                WeeklyCalendarDay("14", "일", isToday = "14" == today.dayOfMonth.toString() && YearMonth.now().month == today.month && YearMonth.now().year == today.year),
                WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의")), isToday = "15" == today.dayOfMonth.toString() && YearMonth.now().month == today.month && YearMonth.now().year == today.year),
                WeeklyCalendarDay("16", "화", events = listOf(CalendarEvent("점심 약속"), CalendarEvent("추가 일정1"), CalendarEvent("추가 일정2")), isToday = "16" == today.dayOfMonth.toString() && YearMonth.now().month == today.month && YearMonth.now().year == today.year),
                WeeklyCalendarDay("17", "수", isToday = "17" == today.dayOfMonth.toString() && YearMonth.now().month == today.month && YearMonth.now().year == today.year),
                WeeklyCalendarDay("18", "목", events = listOf(CalendarEvent("발표")), isToday = "18" == today.dayOfMonth.toString() && YearMonth.now().month == today.month && YearMonth.now().year == today.year),
                WeeklyCalendarDay("19", "금", isToday = "19" == today.dayOfMonth.toString() && YearMonth.now().month == today.month && YearMonth.now().year == today.year),
                WeeklyCalendarDay("20", "토", events = listOf(CalendarEvent("영화 길게 써보기 테스트")), isToday = "20" == today.dayOfMonth.toString() && YearMonth.now().month == today.month && YearMonth.now().year == today.year)
            ),
            isMonthlyView = false,
            onToggleCalendarView = {},
            onMonthChange = {},
            monthlySelectedDate = null,
            onMonthlyDateSelected = {},
            isQuestionAnsweredByAll = true,
            aiQuestion = "우리 가족만의 별명(애칭)이 있나요?",
            familyQuote = "\"가족은 삶의 큰 축복 중 하나이다.\" - 아이린 P. 라이언",
            randomCloudResIds = listOf(R.drawable.cloud1, R.drawable.cloud2),
            onRefreshQuestionClicked = {}
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
            currentYearMonth = YearMonth.of(2025, 5),
            currentYearMonthFormatted = "2025년 05월",
            weeklyCalendarData = emptyList(), // 월간 뷰에서는 주간 데이터 불필요
            isMonthlyView = true,
            onToggleCalendarView = {},
            onMonthChange = {},
            monthlySelectedDate = java.time.LocalDate.of(2025,5,12), // Preview용 예시 선택 날짜
            onMonthlyDateSelected = {},
            isQuestionAnsweredByAll = true,
            aiQuestion = "가장 좋아하는 가족 여행지는?",
            familyQuote = "\"다른 무엇보다도, 준비가 성공의 열쇠이다.\" - 알렉산더 그레이엄 벨",
            randomCloudResIds = listOf(R.drawable.cloud3),
            onRefreshQuestionClicked = {}
        )
    }
}