package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
import com.example.daytogether.ui.theme.ScreenBackground // 테마 색상
import com.example.daytogether.ui.theme.TextPrimary // 테마 색상
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.remember

// LocalDate.yearMonth 확장 프로퍼티 (필요하다면 유지)
val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

@OptIn(ExperimentalFoundationApi::class) // WeeklyCalendarView 등 내부 Composable에서 필요할 수 있음
@Composable
fun ActualHomeScreenContent(
    upcomingAnniversaryText: String,
    dDayText: String,
    dDayTitle: String,
    randomCloudResIds: List<Int>,
    currentYearMonth: YearMonth,
    isMonthlyView: Boolean,
    selectedDateForDetails: LocalDate?,
    dateForBorderOnly: LocalDate?,
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    weeklyCalendarData: List<WeeklyCalendarDay>,
    isQuestionAnsweredByAll: Boolean,
    aiQuestion: String,
    familyQuote: String,
    showAddEventInputScreen: Boolean,
    isBottomBarVisible: Boolean,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate?) -> Unit,
    onToggleCalendarView: () -> Unit, // 주간/월간 뷰 전환 (주로 주간 뷰에서 월간으로 갈 때 사용)
    onMonthlyCalendarHeaderTitleClick: () -> Unit, // 월간 뷰 헤더 클릭 (주간으로 전환)
    onMonthlyCalendarHeaderIconClick: () -> Unit, // 월간 뷰 타임피커 아이콘 클릭
    onRefreshQuestionClicked: () -> Unit,
    onMonthlyTodayButtonClick: () -> Unit, // 월간 뷰 '오늘' 버튼 클릭
    onEditEventRequest: (LocalDate, CalendarEvent) -> Unit,
    onDeleteEventRequest: (LocalDate, CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // --- 표시될 년/월 결정 ---
    // 주간 뷰일 때는 항상 현재 시스템의 년/월을 사용하고,
    // 월간 뷰일 때는 HomeScreen에서 전달받은 currentYearMonth (사용자가 선택한 년/월)를 사용합니다.
    val displayYearMonth = if (isMonthlyView) currentYearMonth else YearMonth.now()
    val displayYearMonthFormatted = remember(displayYearMonth) { // displayYearMonth가 바뀔 때만 재계산
        DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN).format(displayYearMonth)
    }

    Box(modifier = modifier.fillMaxSize()) {
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
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp)
            ) {
                if (!isMonthlyView) { // 주간 뷰일 때
                    Text(
                        text = displayYearMonthFormatted, // ★ 수정: 항상 현재 년/월 기준 포맷팅된 값 사용
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clickable { onToggleCalendarView() } // 클릭 시 월간 뷰로 전환
                            .padding(bottom = 16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(MaterialTheme.typography.headlineSmall.fontSize.value.dp + 16.dp))
                }

                if (isMonthlyView) {
                    MonthlyCalendarView(
                        currentMonth = currentYearMonth, // 사용자가 선택/변경한 currentYearMonth 전달
                        onMonthChange = onMonthChange,
                        onDateClick = onDateClick,
                        eventsByDate = eventsByDate,
                        selectedDateForDetails = selectedDateForDetails,
                        dateForBorderOnly = dateForBorderOnly,
                        modifier = Modifier.fillMaxWidth(),
                        onEditEventRequest = onEditEventRequest,
                        onDeleteEventRequest = onDeleteEventRequest,
                        onTitleClick = onMonthlyCalendarHeaderTitleClick, // 클릭 시 주간 뷰로 전환
                        onCalendarIconClick = onMonthlyCalendarHeaderIconClick,
                        onTodayHeaderButtonClick = onMonthlyTodayButtonClick
                    )
                } else { // 주간 뷰
                    WeeklyCalendarView(
                        // weeklyCalendarData는 HomeScreen에서 이미 '오늘'이 포함된 주로 계산된 데이터
                        weeklyCalendarData = weeklyCalendarData,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                Spacer(modifier = Modifier.height(16.dp)) // 질문 새로고침 버튼과 하단 명언 사이 간격
            }

            // 명언 뷰 표시 조건은 그대로 유지
            val showQuote = !isMonthlyView && !isBottomBarVisible && !showAddEventInputScreen
            if (showQuote) {
                QuoteView(
                    quote = familyQuote,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)
                )
            } else {
                // 명언이 없을 때도 일정한 하단 여백을 주기 위함 (선택 사항)
                Spacer(modifier = Modifier.height(20.dp + (MaterialTheme.typography.bodyMedium.fontSize.value.dp * 2))) // 대략적인 QuoteView 높이만큼
            }
        } // End of main content Column
    } // End of Box
}