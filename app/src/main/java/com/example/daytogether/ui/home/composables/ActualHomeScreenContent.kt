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
import java.time.YearMonth // LocalDate와 같은 패키지인지 확인

// LocalDate.yearMonth 확장 프로퍼티 (필요하다면 유지)
val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActualHomeScreenContent(
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
    showAddEventInputScreen: Boolean,
    isBottomBarVisible: Boolean,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate?) -> Unit,
    onToggleCalendarView: () -> Unit,
    onMonthlyCalendarHeaderTitleClick: () -> Unit,
    onMonthlyCalendarHeaderIconClick: () -> Unit,
    onRefreshQuestionClicked: () -> Unit,
    onMonthlyTodayButtonClick: () -> Unit,
    onEditEventRequest: (LocalDate, CalendarEvent) -> Unit,
    onDeleteEventRequest: (LocalDate, CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
        ) {
            AnniversaryBoard(text = upcomingAnniversaryText) // AnniversaryBoard 호출
            Spacer(modifier = Modifier.height(24.dp))
            DDaySectionView( // DDaySectionView 호출
                dDayText = dDayText,
                dDayTitle = dDayTitle,
                cloudImageResList = randomCloudResIds,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp)
            ) {
                if (!isMonthlyView) {
                    Text(
                        text = currentYearMonthFormatted,
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 23.sp),
                        modifier = Modifier.align(Alignment.Start).clickable { onToggleCalendarView() }.padding(bottom = 16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(MaterialTheme.typography.headlineSmall.fontSize.value.dp + 16.dp))
                }

                if (isMonthlyView) {
                    MonthlyCalendarView( // MonthlyCalendarView 호출
                        currentMonth = currentYearMonth,
                        onMonthChange = onMonthChange,
                        onDateClick = onDateClick,
                        eventsByDate = eventsByDate,
                        selectedDateForDetails = selectedDateForDetails,
                        modifier = Modifier.fillMaxWidth(),
                        onEditEventRequest = onEditEventRequest,
                        onDeleteEventRequest = onDeleteEventRequest,
                        onTitleClick = onMonthlyCalendarHeaderTitleClick,
                        onCalendarIconClick = onMonthlyCalendarHeaderIconClick,
                        onTodayHeaderButtonClick = onMonthlyTodayButtonClick
                    )
                } else { // 주간 뷰
                    WeeklyCalendarView( // WeeklyCalendarView 호출
                        weeklyCalendarData = weeklyCalendarData,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                TodayQuestionHeaderWithAlert(isAnsweredByAll = isQuestionAnsweredByAll) // 호출
                Spacer(modifier = Modifier.height(12.dp))
                TodayQuestionContentCard(questionText = aiQuestion) // 호출
                Spacer(modifier = Modifier.height(18.dp))
                RefreshQuestionButton( // 호출
                    isAnsweredByAll = isQuestionAnsweredByAll,
                    onRefreshQuestionClicked = onRefreshQuestionClicked,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            val showQuote = !isMonthlyView && !isBottomBarVisible && !showAddEventInputScreen
            if (showQuote) {
                QuoteView( // QuoteView 호출
                    quote = familyQuote,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }
        } // End of main content Column
    } // End of Box
}