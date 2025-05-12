package com.example.daytogether.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee // 수정: basicMarquee 직접 임포트
// import androidx.compose.foundation.clickable // DayCellNew에서 선택 기능을 제거했으므로 주석 처리 또는 삭제
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
import com.example.daytogether.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun HomeScreen() {
    var upcomingAnniversaryText by remember { mutableStateOf("D-3 가족 기념일! 오늘은 즐거운 하루 보내세요! 반복 확인용 텍스트") }
    var dDayTextState by remember { mutableStateOf("D-3") }
    var isAnniversaryAvailable by remember { mutableStateOf(true) }
    var dDayTitleState by remember { mutableStateOf(if (isAnniversaryAvailable) "엄마 생일" else "기념일 없음") }

    val currentYearMonthState by remember {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy년 MM월", Locale.KOREAN)
        mutableStateOf(sdf.format(cal.time))
    }

    val todayCalendar = Calendar.getInstance()
    val todayDateStr = todayCalendar.get(Calendar.DAY_OF_MONTH).toString()

    val weeklyCalendarDataState = remember(todayDateStr) {
        listOf(
            WeeklyCalendarDay("14", "일", isToday = "14" == todayDateStr),
            WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의"), CalendarEvent("미팅 길어지면 이렇게")), isToday = "15" == todayDateStr),
            WeeklyCalendarDay("16", "화", events = listOf(CalendarEvent("점심 약속")), isToday = "16" == todayDateStr),
            WeeklyCalendarDay("17", "수", events = listOf(CalendarEvent("저녁 약속"), CalendarEvent("영화 보기"), CalendarEvent("과제")), isToday = "17" == todayDateStr),
            WeeklyCalendarDay("18", "목", events = listOf(CalendarEvent("발표"), CalendarEvent("과제"), CalendarEvent("스터디"), CalendarEvent("저녁")), isToday = "18" == todayDateStr),
            WeeklyCalendarDay("19", "금", isToday = "19" == todayDateStr),
            WeeklyCalendarDay("20", "토", events = listOf(CalendarEvent("영화")), isToday = "20" == todayDateStr)
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
        currentYearMonth = currentYearMonthState,
        weeklyCalendarData = weeklyCalendarDataState,
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
    currentYearMonth: String,
    weeklyCalendarData: List<WeeklyCalendarDay>,
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
            .padding(vertical = 20.dp), // 상하 전체 여백 증가
        verticalArrangement = Arrangement.spacedBy(32.dp) // 1. 레이아웃 간격 (이전 30dp)
    ) {
        AnniversaryBoard(text = upcomingAnniversaryText, modifier = Modifier.fillMaxWidth())

        // "빨간색 칸" 영역 (D-Day, 구름)
        Column(modifier = Modifier.padding(horizontal = 22.dp)) { // 좌우 패딩 약간 증가
            DDaySectionView(
                dDayText = dDayText,
                dDayTitle = dDayTitle,
                cloudImageResList = randomCloudResIds
            )
        }

        // "파란색 칸" 영역 (년월, 주간 캘린더)
        Column(modifier = Modifier.padding(horizontal = 22.dp)) { // 좌우 패딩 약간 증가
            Text(
                text = currentYearMonth,
                color = TextPrimary,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 21.sp),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))
            // 5. 원래 있던 캘린더 박스(Card)는 없애고 Row로 바로 구성
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // 주간 캘린더 전체 높이 유지
                    .border(BorderStroke(1.dp, TextPrimary), RoundedCornerShape(10.dp)) // 4. 캘린더 외곽선 추가 및 둥글게
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

        // "검은색 칸" 영역 (오늘의 질문 관련)
        Column(modifier = Modifier.padding(horizontal = 22.dp)) { // 좌우 패딩 약간 증가
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
            modifier = Modifier.padding(horizontal = 22.dp) // 좌우 패딩 약간 증가
        )
        Spacer(modifier = Modifier.height(10.dp)) // 하단 여백 약간 증가
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
            Text(text = dDayText, color = TextPrimary, style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 48.sp))
            Text(
                text = dDayTitle,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 20.sp), // 2. 엄마생일 Bold
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

// WeeklyCalendarCardView 함수는 이제 Row로 직접 구성되므로 삭제하거나,
// 이전처럼 Card로 감싸는 형태를 유지하고 싶다면 내부 Row에만 수정 적용합니다.
// 여기서는 요청에 따라 Card를 제거하고 Row로 바로 구성하는 형태로 변경합니다.
// (ActualHomeScreenContent에서 Row로 직접 호출하도록 변경됨)

@Composable
fun DayCellNew(
    dateData: WeeklyCalendarDay,
    modifier: Modifier = Modifier // isSelected와 onDateClicked는 이전 요청에 따라 제거됨
) {
    val dayColor = TextPrimary
    // 4. 오늘 날짜 칸에만 전광판 배경색(투명도 50%) 적용
    val cellContentBackgroundColor = if (dateData.isToday) AnniversaryBoardBackground.copy(alpha = 0.5f) else Color.Transparent
    // 오늘 날짜 테두리는 유지, 다른 날짜 테두리는 없음 (클릭 효과 제거됨)
    val cellBorderColor = if (dateData.isToday) TextPrimary else Color.Transparent
    val cellBorderThickness = if (dateData.isToday) 1.5.dp else 0.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(0.dp)) // 전체 셀의 모서리는 직각
            .border(BorderStroke(cellBorderThickness, cellBorderColor), RoundedCornerShape(0.dp)) // 오늘 날짜에만 테두리
            .padding(vertical = 4.dp, horizontal = 1.dp) // 셀 내부 상하좌우 최소 패딩
            .height(95.dp), // 셀 높이 (이벤트 3줄 고려하여 약간 늘림, 이전 90dp)
        verticalArrangement = Arrangement.Top // 내용물을 위로 정렬
    ) {
        // 1. 요일(날짜) 형식으로 한 줄에 배치, 이 부분에만 배경색 적용
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (dateData.isToday) cellContentBackgroundColor else Color.Transparent, shape = RoundedCornerShape(4.dp)) // 오늘 날짜의 "요일(날짜)" 부분에만 배경색 및 약간 둥근 모서리
                .padding(vertical = 5.dp), // 텍스트 상자의 상하 패딩
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${dateData.dayOfWeek}(${dateData.date})", // "일(14)" 형식
                color = if (dateData.isToday) TextPrimary else dayColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold, // 요일 폰트 스타일 extralight
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(4.dp)) // 날짜(요일)와 이벤트 내용 사이 간격

        // 이벤트 내용 표시 (최대 3줄, 1줄 이상 넘는 내용은 잘림)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // 남은 공간을 이벤트 표시에 사용
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically) // 이벤트 간 간격 및 수직 중앙 정렬
        ) {
            if (dateData.events.isNotEmpty()) {
                dateData.events.take(3).forEach { event -> // 최대 3개의 이벤트만 표시
                    Text(
                        text = event.description,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1, // 내용도 1줄로 제한
                        overflow = TextOverflow.Ellipsis, // 1줄 이상 넘는 글자는 잘림
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp) // 각 이벤트 텍스트 좌우 패딩
                    )
                }
            }
        }
    }
}


// --- WeeklyCalendarCardView 함수는 이전 답변과 동일하게 유지 ---
// DayCellNew의 높이가 95dp로 변경되었으므로, Card의 Row 높이도 그에 맞게 조정 (예: 110.dp)
@Composable
fun WeeklyCalendarCardView(
    weeklyCalendarData: List<WeeklyCalendarDay>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp), // 4. 캘린더 외곽선 추가 및 둥글게
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, TextPrimary), // 4. 캘린더 외곽선
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp) // DayCellNew 높이(95dp) + 상하패딩(8dp*2) 고려하여 조절
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


// ... (TodayQuestionHeaderWithAlert, TodayQuestionContentCard, RefreshQuestionButton, QuoteView 함수는 이전과 동일) ...
// (RefreshQuestionButton의 가로 크기를 fillMaxWidth(0.45f) 정도로 다시 조정)

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
                text = "아직 오늘의 질문에 답변하지 않았어요!",
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
            .fillMaxWidth(0.45f) // 4. 버튼 가로 크기 (이전 0.50f)
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
@Preview(showBackground = true, name = "홈 (답변 완료)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenPreview() {
    DaytogetherTheme {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-3 가족 기념일!오늘은 즐거운 하루!",
            dDayText = "D-3",
            dDayTitle = "엄마 생일",
            currentYearMonth = "2025년 04월",
            weeklyCalendarData = listOf(
                WeeklyCalendarDay("14", "일", isToday = "14" == today),
                WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의"), CalendarEvent("미팅")), isToday = "15" == today),
                WeeklyCalendarDay("16", "화", events = listOf(CalendarEvent("점심 약속")), isToday = "16" == today),
                WeeklyCalendarDay("17", "수", events = listOf(CalendarEvent("저녁 약속"), CalendarEvent("영화 보기"), CalendarEvent("과제")), isToday = "17" == today),
                WeeklyCalendarDay("18", "목", events = listOf(CalendarEvent("발표"), CalendarEvent("과제"), CalendarEvent("스터디"), CalendarEvent("저녁")), isToday = "18" == today),
                WeeklyCalendarDay("19", "금", isToday = "19" == today),
                WeeklyCalendarDay("20", "토", events = listOf(CalendarEvent("영화")), isToday = "20" == today)
            ),
            isQuestionAnsweredByAll = true,
            aiQuestion = "우리 가족만의 별명(애칭)이 있나요?",
            familyQuote = "\"가족은 삶의 큰 축복 중 하나이다.\" - 아이린 P. 라이언",
            randomCloudResIds = listOf(R.drawable.cloud1, R.drawable.cloud2),
            onRefreshQuestionClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "홈 (답변 미완료)", widthDp = 390, heightDp = 844)
@Composable
fun HomeScreenNotAnsweredPreview() {
    DaytogetherTheme {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        ActualHomeScreenContent(
            upcomingAnniversaryText = "기념일 없음",
            dDayText = "D-?",
            dDayTitle = "기념일 없음",
            currentYearMonth = "2025년 04월",
            weeklyCalendarData = listOf(
                WeeklyCalendarDay("14", "일", isToday = "14" == today),
                WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의")), isToday = "15" == today),
                WeeklyCalendarDay("16", "화", isToday = "16" == today),
                WeeklyCalendarDay("17", "수", events = listOf(CalendarEvent("미팅"), CalendarEvent("추가 이벤트1"), CalendarEvent("추가 이벤트2")), isToday = "17" == today),
                WeeklyCalendarDay("18", "목", isToday = "18" == today),
                WeeklyCalendarDay("19", "금", events = listOf(CalendarEvent("영화 약속 길게길게")), isToday = "19" == today),
                WeeklyCalendarDay("20", "토", isToday = "20" == today)
            ),
            isQuestionAnsweredByAll = false,
            aiQuestion = "최근에 가장 기억에 남는 가족 행사는?",
            familyQuote = "\"가장 중요한 것은 가족이다.\" - 월트 디즈니",
            randomCloudResIds = listOf(R.drawable.cloud3),
            onRefreshQuestionClicked = {}
        )
    }
}