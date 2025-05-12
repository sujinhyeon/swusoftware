package com.example.daytogether.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.R
import com.example.daytogether.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// 홈 화면 전체
@Composable
fun HomeScreen() {
    // --- 상태 변수들 (ViewModel로 이전 고려) ---
    var upcomingAnniversaryText by remember { mutableStateOf("D-3 가족 기념일이 얼마 남지 않았어요!") }
    var dDayText by remember { mutableStateOf("D-3") }
    var dDayTitle by remember { mutableStateOf("엄마 생일") }
    val currentYearMonth by remember {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy년 MM월", Locale.KOREAN)
        mutableStateOf(sdf.format(cal.time))
    }
    val weeklyDates = remember { listOf("14(일)","15(월)","16(화)","17(수)","18(목)","19(금)","20(토)") }
    var currentIsQuestionAnsweredByAll by remember { mutableStateOf(false) } // 변수명 변경하여 명확화
    var aiQuestion by remember { mutableStateOf("우리 가족만의 별명(애칭)이 있나요?") }
    var familyQuote by remember { mutableStateOf("\"가족은 자격이 아니라 특권이다.\" - 클린트 이스트우드") }

    val cloudImages = remember {
        listOf(
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
            R.drawable.cloud4, R.drawable.cloud5, R.drawable.cloud6,
            R.drawable.cloud7, R.drawable.cloud8, R.drawable.cloud9
        )
    }
    val randomCloudResIds by remember { mutableStateOf(cloudImages.shuffled().take(3)) }

    // --- UI 구성 ---
    // HomeScreenContent Composable을 호출하여 실제 UI를 그림
    // 이렇게 하면 상태 로직과 UI 로직을 분리하고 Preview에서 테스트하기 용이합니다.
    ActualHomeScreenContent(
        upcomingAnniversaryText = upcomingAnniversaryText,
        dDayText = dDayText,
        dDayTitle = dDayTitle,
        currentYearMonth = currentYearMonth,
        weeklyDates = weeklyDates,
        isQuestionAnsweredByAll = currentIsQuestionAnsweredByAll, // 현재 상태 전달
        aiQuestion = aiQuestion,
        familyQuote = familyQuote,
        randomCloudResIds = randomCloudResIds,
        onRefreshQuestionClicked = {
            aiQuestion = "새로운 AI 질문 생성 중..." // 새 질문으로 업데이트 (ViewModel에서 처리)
            currentIsQuestionAnsweredByAll = false // 답변 상태 초기화
        }
    )
}

// 실제 홈 화면의 UI 레이아웃을 담당하는 Composable (상태를 파라미터로 받음)
@Composable
fun ActualHomeScreenContent(
    upcomingAnniversaryText: String,
    dDayText: String,
    dDayTitle: String,
    currentYearMonth: String,
    weeklyDates: List<String>,
    isQuestionAnsweredByAll: Boolean, // 파라미터로 받음
    aiQuestion: String,
    familyQuote: String,
    randomCloudResIds: List<Int>,
    onRefreshQuestionClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 20.dp, end = 20.dp, top = 8.dp)
    ) {
        AnniversaryBoard(text = upcomingAnniversaryText, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        DDaySectionView(
            dDayText = dDayText,
            dDayTitle = dDayTitle,
            cloudImageResList = randomCloudResIds
        )
        Spacer(modifier = Modifier.height(20.dp))
        WeeklyCalendarCardView(
            yearMonth = currentYearMonth,
            dates = weeklyDates
        )
        Spacer(modifier = Modifier.height(24.dp))
        TodayQuestionHeader(isAnsweredByAll = isQuestionAnsweredByAll) // 전달받은 상태 사용
        Spacer(modifier = Modifier.height(8.dp))
        TodayQuestionContentCard(questionText = aiQuestion)
        Spacer(modifier = Modifier.height(12.dp))

        RefreshQuestionButton(
            isAnsweredByAll = isQuestionAnsweredByAll, // 전달받은 상태 사용
            onRefreshQuestionClicked = onRefreshQuestionClicked, // 람다 전달
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // "아직 답변하지 않은 가족" 메시지는 버튼 바로 아래에 표시되도록 버튼과 함께 묶거나,
        // 버튼 컴포저블 내부에서 isAnsweredByAll 상태에 따라 표시하는 것이 더 자연스러울 수 있습니다.
        // 여기서는 일단 HomeScreenContent 내부에 둡니다.
        if (!isQuestionAnsweredByAll) { // 전달받은 상태 사용
            Text(
                text = "아직 오늘의 질문에 답변하지 않은 가족이 있어요!",
                color = ErrorRed,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        QuoteView(quote = familyQuote)
        Spacer(modifier = Modifier.height(8.dp))
    }
}


// --- 각 UI 섹션 Composable 함수들은 이전과 동일하게 유지 ---
// (AnniversaryBoard, DDaySectionView, WeeklyCalendarCardView, DayCell, TodayQuestionHeader, TodayQuestionContentCard, RefreshQuestionButton, QuoteView)
// 이 함수들은 상태를 직접 갖지 않고, 파라미터로 필요한 데이터를 받아서 UI만 그리는 역할에 집중합니다.
// RefreshQuestionButton은 Modifier를 파라미터로 받도록 수정된 상태여야 합니다.

@Composable
fun AnniversaryBoard(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AnniversaryBoardBackground)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        color = TextPrimary,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DDaySectionView(dDayText: String, dDayTitle: String, cloudImageResList: List<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = dDayText,
                color = TextPrimary,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            Text(
                text = dDayTitle,
                color = TextPrimary,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Image(
                painter = painterResource(id = cloudImageResList.getOrElse(0) { R.drawable.cloud1 }),
                contentDescription = "구름1",
                modifier = Modifier.size(80.dp).offset(x = 20.dp, y = (-10).dp),
                contentScale = ContentScale.Fit
            )
            if (cloudImageResList.size > 1) {
                Image(
                    painter = painterResource(id = cloudImageResList.getOrElse(1) { R.drawable.cloud2 }),
                    contentDescription = "구름2",
                    modifier = Modifier.size(60.dp).offset(x = (-20).dp, y = 10.dp),
                    contentScale = ContentScale.Fit
                )
            }
            if (cloudImageResList.size > 2) {
                Image(
                    painter = painterResource(id = cloudImageResList.getOrElse(2) { R.drawable.cloud3 }),
                    contentDescription = "구름3",
                    modifier = Modifier.size(70.dp).offset(x = 0.dp, y = 25.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun WeeklyCalendarCardView(yearMonth: String, dates: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = yearMonth,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
                dates.take(7).forEachIndexed { index, dateWithDay ->
                    val parts = dateWithDay.split("(")
                    val dateNum = parts.getOrNull(0) ?: ""
                    val dayName = parts.getOrNull(1)?.replace(")", "") ?: daysOfWeek.getOrElse(index){""}
                    DayCell(
                        day = dayName,
                        date = dateNum,
                        isToday = dateNum == "16" // TODO: 실제 오늘 날짜와 비교
                    )
                }
            }
        }
    }
}

@Composable
fun DayCell(day: String, date: String, isToday: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .padding(horizontal = 1.dp)
    ) {
        Text(
            text = day,
            color = if(day == "토" || day == "일") ErrorRed.copy(alpha = 0.8f) else TextPrimary.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 11.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(38.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isToday) AnniversaryBoardBackground.copy(alpha = 0.7f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun TodayQuestionHeader(isAnsweredByAll: Boolean){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "오늘의 질문",
            color = TextPrimary,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = ImageVector.vectorResource(id = if (isAnsweredByAll) R.drawable.ic_happy else R.drawable.ic_sad),
            contentDescription = if (isAnsweredByAll) "답변 완료" else "답변 필요",
            tint = TextPrimary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun TodayQuestionContentCard(questionText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = questionText,
            color = TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            lineHeight = 20.sp
        )
    }
}

@Composable
fun RefreshQuestionButton(
    isAnsweredByAll: Boolean,
    onRefreshQuestionClicked: () -> Unit,
    modifier: Modifier = Modifier // Modifier 파라미터 추가
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
        modifier = modifier // 전달받은 modifier 사용 (align은 여기서 적용하지 않음)
            .fillMaxWidth(0.5f) // 버튼 너비는 여기서 설정하거나, 호출부에서 Modifier로 조절
            .height(40.dp)
    ) {
        Text(
            "질문 한 번 더 받기",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun QuoteView(quote: String) {
    Text(
        text = quote,
        color = TextPrimary.copy(alpha = 0.7f),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}


// --- Previews ---
// Preview를 위한 별도의 Content 함수는 유지하되, HomeScreen 내부 로직과 일치하도록 수정
@Preview(showBackground = true, name = "홈 (답변 완료)", widthDp = 390, heightDp = 844)
@Composable
fun DefaultHomeScreenPreview() {
    DaytogetherTheme {
        ActualHomeScreenContent( // 실제 UI를 그리는 함수 호출
            upcomingAnniversaryText = "D-3 가족 기념일!",
            dDayText = "D-3",
            dDayTitle = "엄마 생일",
            currentYearMonth = "2025년 05월",
            weeklyDates = listOf("14(일)","15(월)","16(화)","17(수)","18(목)","19(금)","20(토)"),
            isQuestionAnsweredByAll = true, // 답변 완료된 상태
            aiQuestion = "우리 가족만의 별명(애칭)이 있나요?",
            familyQuote = "\"가족은 삶의 큰 축복 중 하나이다.\" - 아이린 P. 라이언",
            randomCloudResIds = listOf(R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3), // 고정된 구름 이미지
            onRefreshQuestionClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "홈 (답변 미완료)", widthDp = 390, heightDp = 844)
@Composable
fun HomeScreenNotAnsweredPreview() {
    DaytogetherTheme {
        ActualHomeScreenContent( // 실제 UI를 그리는 함수 호출
            upcomingAnniversaryText = "다가오는 기념일이 없어요!",
            dDayText = "D-?",
            dDayTitle = "다가오는 가족기념일 없음",
            currentYearMonth = "2025년 05월",
            weeklyDates = listOf("14(일)","15(월)","16(화)","17(수)","18(목)","19(금)","20(토)"),
            isQuestionAnsweredByAll = false, // 답변 미완료 상태
            aiQuestion = "최근에 가장 기억에 남는 가족 행사는?",
            familyQuote = "\"가장 중요한 것은 가족이다.\" - 월트 디즈니",
            randomCloudResIds = listOf(R.drawable.cloud4, R.drawable.cloud5, R.drawable.cloud6), // 다른 고정된 구름 이미지
            onRefreshQuestionClicked = {}
        )
    }
}