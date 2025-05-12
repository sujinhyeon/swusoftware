package com.example.daytogether.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable // 날짜 선택을 위해
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
    var upcomingAnniversaryText by remember { mutableStateOf("D-3 가족 기념일이 얼마 남지 않았어요! 오늘은 즐거운 하루 보내세요!") }
    var dDayTextState by remember { mutableStateOf("D-3") }
    // 1. "기념일 없음"으로 문구 변경 (예시: isAnniversaryAvailable 상태로 관리)
    var isAnniversaryAvailable by remember { mutableStateOf(true) }
    var dDayTitleState by remember { mutableStateOf(if (isAnniversaryAvailable) "엄마 생일" else "기념일 없음") }

    val currentYearMonthState by remember {
        val cal = Calendar.getInstance();
        val sdf = SimpleDateFormat("yyyy년 MM월", Locale.KOREAN);
        mutableStateOf(sdf.format(cal.time))
    }

    // 5. 오늘 날짜를 항상 표시하고, 선택된 날짜 관리
    val todayCalendar = Calendar.getInstance()
    val todayDateStr = todayCalendar.get(Calendar.DAY_OF_MONTH).toString()
    var selectedDateStr by remember { mutableStateOf(todayDateStr) } // 초기 선택은 오늘

    val weeklyCalendarDataState = remember(todayDateStr) { // todayDateStr이 변경되면 weeklyCalendarData도 재계산 (예시)
        // 실제로는 현재 주를 기준으로 7일치 데이터를 동적으로 생성해야 함
        listOf(
            WeeklyCalendarDay("14", "일", isToday = "14" == todayDateStr),
            WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의"), CalendarEvent("미팅")), isToday = "15" == todayDateStr),
            WeeklyCalendarDay("16", "화", events = listOf(CalendarEvent("점심 약속")), isToday = "16" == todayDateStr),
            WeeklyCalendarDay("17", "수", isToday = "17" == todayDateStr),
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
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
            R.drawable.cloud4, R.drawable.cloud5, R.drawable.cloud6,
            R.drawable.cloud7, R.drawable.cloud8, R.drawable.cloud9
        )
    }
    val numberOfClouds by remember { mutableStateOf(Random.nextInt(1, 3)) }
    val randomCloudResIdsState by remember { mutableStateOf(cloudImages.shuffled().take(numberOfClouds)) }

    // "기념일 없음" 텍스트 테스트용 토글 (실제로는 데이터 유무에 따라 결정)
    //LaunchedEffect(Unit) {
    //    kotlinx.coroutines.delay(5000) // 5초 후
    //    isAnniversaryAvailable = false
    //    dDayTitleState = if (isAnniversaryAvailable) "엄마 생일" else "기념일 없음"
    //    if (!isAnniversaryAvailable) dDayTextState = "D-?"
    //}


    ActualHomeScreenContent(
        upcomingAnniversaryText = upcomingAnniversaryText,
        dDayText = dDayTextState,
        dDayTitle = dDayTitleState,
        currentYearMonth = currentYearMonthState,
        weeklyCalendarData = weeklyCalendarDataState,
        selectedDate = selectedDateStr, // 선택된 날짜 전달
        onDateSelected = { date -> selectedDateStr = date }, // 날짜 선택 콜백 전달
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
    selectedDate: String, // 현재 선택된 날짜
    onDateSelected: (String) -> Unit, // 날짜 선택 시 호출될 콜백
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
            .padding(vertical = 12.dp), // 상하 전체 여백 약간 줄임
        verticalArrangement = Arrangement.spacedBy(20.dp) // 섹션 간 기본 간격 조정 (이전 28dp)
    ) {
        AnniversaryBoard(text = upcomingAnniversaryText, modifier = Modifier.fillMaxWidth())

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            DDaySectionView(
                dDayText = dDayText,
                dDayTitle = dDayTitle,
                cloudImageResList = randomCloudResIds
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = currentYearMonth,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))
            WeeklyCalendarCardView(
                weeklyCalendarData = weeklyCalendarData,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            TodayQuestionHeaderWithAlert(isAnsweredByAll = isQuestionAnsweredByAll)
            Spacer(modifier = Modifier.height(10.dp))
            TodayQuestionContentCard(questionText = aiQuestion)
            Spacer(modifier = Modifier.height(16.dp))
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
        Spacer(modifier = Modifier.height(4.dp))
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnniversaryBoard(text: String, modifier: Modifier = Modifier) {
    Box( // 1. 전광판 모서리 직각
        modifier = modifier
            .background(AnniversaryBoardBackground, shape = RectangleShape) // 모서리 직각
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextPrimary,
            // 2. 전광판 텍스트 스타일 얇게
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light, fontSize = 13.sp),
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
                // 1. dDayTitle 텍스트 스타일 더 얇게, 한 줄에 7자 정도
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light, fontSize = 14.sp, lineHeight = 16.sp),
                maxLines = 2, // 2줄까지 허용
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 90.dp) // 7글자 정도에 맞게 너비 제한 (폰트따라 조절)
            )
        }
        Row(
            modifier = Modifier.weight(1f).height(100.dp), // 구름 영역 높이 유지 또는 증가
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), // 구름 간 간격
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 2. 구름 크기 더 크게
            cloudImageResList.forEach { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "구름",
                    modifier = Modifier.size(95.dp), // 구름 크기 증가
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun WeeklyCalendarCardView(
    weeklyCalendarData: List<WeeklyCalendarDay>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, TextPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // 3. 주간캘린더가 비정상적으로 길어서 -> 각 DayCellNew의 높이를 제한
        // Row의 높이는 내부 DayCellNew 중 가장 큰 높이에 맞춰짐.
        // DayCellNew 내부에서 이벤트 표시에 따라 높이가 가변적이지 않도록 고정 높이 사용.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp) // 주간 캘린더 전체의 고정 높이 (1/3 정도로 줄임)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            weeklyCalendarData.forEachIndexed { index, dayData ->
                DayCellNew(
                    dateData = dayData,
                    isSelected = dayData.date == selectedDate,
                    onDateClicked = { onDateSelected(dayData.date) },
                    modifier = Modifier.weight(1f).fillMaxHeight() // 각 셀이 높이를 꽉 채움
                )
                if (index < weeklyCalendarData.size - 1) {
                    VerticalDivider(
                        color = TextPrimary.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxHeight().width(1.dp).padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DayCellNew(
    dateData: WeeklyCalendarDay,
    isSelected: Boolean,
    onDateClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayColor = when (dateData.dayOfWeek) {
        "토" -> Color.Blue.copy(alpha = 0.9f)
        "일" -> ErrorRed.copy(alpha = 0.9f)
        else -> TextPrimary
    }
    // 5 & 6. 오늘 날짜 및 선택된 날짜 스타일
    val cellBorderColor = if (isSelected || dateData.isToday) TextPrimary else Color.Transparent
    val cellBackgroundColor = if (dateData.isToday) AnniversaryBoardBackground.copy(alpha = 0.7f) else Color.Transparent // 오늘만 배경색

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp)) // 약간의 둥근 모서리 (선택 시 테두리 위함)
            .clickable { onDateClicked() }
            .border(BorderStroke(if (isSelected || dateData.isToday) 1.5.dp else 0.dp, cellBorderColor), RoundedCornerShape(4.dp))
            .background(cellBackgroundColor)
            .padding(vertical = 6.dp, horizontal = 2.dp), // 내부 패딩
        verticalArrangement = Arrangement.Top // 상단 정렬하여 이벤트 공간 확보
    ) {
        Text( // 요일
            text = dateData.dayOfWeek,
            color = dayColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp) // 4. 요일 글자 크고 진하게
        )
        Spacer(modifier = Modifier.height(6.dp)) // 요일과 날짜 사이 간격
        Text( // 날짜
            text = dateData.date,
            color = if (dateData.isToday) MaterialTheme.colorScheme.primary else TextPrimary,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal, fontSize = 12.sp) // 4. 날짜 bold 해제, 작게
        )
        Spacer(modifier = Modifier.height(6.dp)) // 날짜와 이벤트 사이 간격

        // 4. 요일 아래 이벤트 표시 (주간캘린더.jpg 참고)
        Column(
            modifier = Modifier.weight(1f), // 남은 공간을 이벤트 표시에 사용
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp) // 이벤트 간 간격
        ) {
            if (dateData.events.isNotEmpty()) {
                if (dateData.events.size > 2) { // 이벤트가 2개 초과면 +n
                    // 첫 번째 이벤트 텍스트 표시 (선택적)
                    Text(
                        text = dateData.events.first().description,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "+${dateData.events.size - 1}", // 나머지 개수
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    )
                } else { // 이벤트 1~2개면 텍스트로 표시
                    dateData.events.forEach { event ->
                        Text(
                            text = event.description,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
            Text("오늘의 질문", color = TextPrimary, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = ImageVector.vectorResource(id = if (isAnsweredByAll) R.drawable.ic_happy else R.drawable.ic_sad),
                contentDescription = if (isAnsweredByAll) "답변 완료" else "답변 필요",
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
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
            .fillMaxWidth(0.7f) // 5. 버튼 가로 크기 조정 (텍스트가 한 줄에 들어가도록)
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
        // 6. 명언 문구 크기 키움
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
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
        val todayDateStr = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        ActualHomeScreenContent(
            upcomingAnniversaryText = "D-3 가족 기념일!오늘은 즐거운 하루! 길게길게 테스트 문구입니다.",
            dDayText = "D-3",
            dDayTitle = "엄마 생일",
            currentYearMonth = "2025년 04월",
            weeklyCalendarData = listOf(
                WeeklyCalendarDay("14", "일", isToday = "14" == todayDateStr),
                WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의"), CalendarEvent("미팅")), isToday = "15" == todayDateStr),
                WeeklyCalendarDay("16", "화", events = listOf(CalendarEvent("점심 약속")), isToday = "16" == todayDateStr),
                WeeklyCalendarDay("17", "수", isToday = "17" == todayDateStr),
                WeeklyCalendarDay("18", "목", events = listOf(CalendarEvent("발표"), CalendarEvent("과제"), CalendarEvent("스터디"), CalendarEvent("저녁")), isToday = "18" == todayDateStr),
                WeeklyCalendarDay("19", "금", isToday = "19" == todayDateStr),
                WeeklyCalendarDay("20", "토", events = listOf(CalendarEvent("영화")), isToday = "20" == todayDateStr)
            ),
            selectedDate = todayDateStr,
            onDateSelected = {},
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
        val todayDateStr = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        ActualHomeScreenContent(
            upcomingAnniversaryText = "기념일 없음",
            dDayText = "D-?",
            dDayTitle = "기념일 없음",
            currentYearMonth = "2025년 04월",
            weeklyCalendarData = listOf(
                WeeklyCalendarDay("14", "일", isToday = "14" == todayDateStr),
                WeeklyCalendarDay("15", "월", events = listOf(CalendarEvent("회의")), isToday = "15" == todayDateStr),
                WeeklyCalendarDay("16", "화", isToday = "16" == todayDateStr),
                WeeklyCalendarDay("17", "수", events = listOf(CalendarEvent("미팅")), isToday = "17" == todayDateStr),
                WeeklyCalendarDay("18", "목", isToday = "18" == todayDateStr),
                WeeklyCalendarDay("19", "금", events = listOf(CalendarEvent("영화"), CalendarEvent("약속")), isToday = "19" == todayDateStr),
                WeeklyCalendarDay("20", "토", isToday = "20" == todayDateStr)
            ),
            selectedDate = todayDateStr,
            onDateSelected = {},
            isQuestionAnsweredByAll = false,
            aiQuestion = "최근에 가장 기억에 남는 가족 행사는?",
            familyQuote = "\"가장 중요한 것은 가족이다.\" - 월트 디즈니",
            randomCloudResIds = listOf(R.drawable.cloud3),
            onRefreshQuestionClicked = {}
        )
    }
}