package com.example.daytogether.ui.home

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // HomeScreen 상태 관리를 위해 필요
import androidx.compose.runtime.remember // HomeScreen 상태 관리를 위해 필요
import androidx.compose.runtime.setValue // HomeScreen 상태 관리를 위해 필요
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // appNavController 타입
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.R // 아이콘 리소스
import com.example.daytogether.ui.navigation.BottomNavItem // BottomNavItem 정의
import com.example.daytogether.ui.settings.SettingsScreen // SettingsScreen 임포트
import com.example.daytogether.ui.theme.DaytogetherTheme // 앱 테마
import com.example.daytogether.ui.theme.NavIconSelected // 테마 색상
import com.example.daytogether.ui.theme.NavIconUnselected // 테마 색상
import com.example.daytogether.ui.theme.TextPrimary // 테마 색상

// ActualHomeScreenContent를 호출하기 위한 임시 데이터 및 콜백 (실제로는 ViewModel 등에서 관리)
// 이 부분은 실제 프로젝트의 상태 관리 방식에 맞게 수정되어야 합니다.
// 지금은 HomeScreen이 정상적으로 Scaffold와 NavHost를 통해 ActualHomeScreenContent를
// 호출하고 화면에 그릴 수 있도록 하기 위한 최소한의 구조입니다.
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.data.model.WeeklyCalendarDay
import com.example.daytogether.ui.home.composables.ActualHomeScreenContent // ActualHomeScreenContent 임포트
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random
import java.time.DayOfWeek as JavaDayOfWeek


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(appNavController: NavController) { // AppNavigation으로부터 NavController를 받음
    val mainNavController = rememberNavController() // 하단 탭 내부 화면 전환용 NavController

    // BottomNavItem 리스트 (BottomNavItem.kt 파일 내용 참고)
    // 각 아이콘 리소스 ID는 실제 프로젝트에 맞게 수정해야 합니다.
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Message,
        BottomNavItem.Gallery,
        BottomNavItem.Settings
    )

    // HomeScreen 내부의 상태 변수들 (ActualHomeScreenContent에 전달하기 위함)
    // 이 부분은 실제 앱에서는 ViewModel을 통해 관리하는 것이 좋습니다.
    // 여기서는 UI 구조를 잡기 위한 예시 값을 사용합니다.
    var upcomingAnniversaryText by remember { mutableStateOf("D-3 샘플 기념일!") }
    var dDayTextState by remember { mutableStateOf("D-3") }
    var dDayTitleState by remember { mutableStateOf("샘플 기념일") }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var isMonthlyView by remember { mutableStateOf(false) }
    var selectedDateForDetails by remember { mutableStateOf<LocalDate?>(null) }
    var dateForBorderOnly by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now()
    val eventsByDateState = remember { mutableStateMapOf<LocalDate, List<CalendarEvent>>() }
    val weeklyCalendarDataState = remember(today, eventsByDateState) {
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
    var aiQuestionState by remember { mutableStateOf("오늘의 AI 질문 예시입니다.") }
    var familyQuoteState by remember { mutableStateOf("\"가족 명언 예시입니다.\"") }
    val randomCloudResIds = remember { listOf(R.drawable.cloud1, R.drawable.cloud2) } // 실제 drawable 확인

    Scaffold(
        bottomBar = {
            Column {
                Divider(color = TextPrimary.copy(alpha = 0.2f), thickness = 0.5.dp)
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = screen.iconResId),
                                    contentDescription = screen.label,
                                    tint = if (isSelected) NavIconSelected else NavIconUnselected
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                mainNavController.navigate(screen.route) {
                                    popUpTo(mainNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding) // Scaffold의 innerPadding 적용
        ) {
            composable(BottomNavItem.Home.route) {
                ActualHomeScreenContent(
                    // 여기에 ActualHomeScreenContent에 필요한 모든 파라미터를 전달합니다.
                    // 위에서 정의한 상태 변수들을 사용합니다.
                    upcomingAnniversaryText = upcomingAnniversaryText,
                    dDayText = dDayTextState,
                    dDayTitle = dDayTitleState,
                    randomCloudResIds = randomCloudResIds,
                    currentYearMonth = currentYearMonth,
                    isMonthlyView = isMonthlyView,
                    selectedDateForDetails = selectedDateForDetails,
                    dateForBorderOnly = dateForBorderOnly,
                    eventsByDate = eventsByDateState,
                    weeklyCalendarData = weeklyCalendarDataState,
                    isQuestionAnsweredByAll = isQuestionAnsweredByAllState,
                    aiQuestion = aiQuestionState,
                    familyQuote = familyQuoteState,
                    showAddEventInputScreen = false, // 이 값들은 실제 로직에 따라 변경되어야 함
                    isBottomBarVisible = true,    // 이 값들은 실제 로직에 따라 변경되어야 함
                    onMonthChange = { currentYearMonth = it },
                    onDateClick = { selectedDateForDetails = it },
                    onToggleCalendarView = { isMonthlyView = !isMonthlyView },
                    onMonthlyCalendarHeaderTitleClick = { isMonthlyView = false },
                    onMonthlyCalendarHeaderIconClick = { /* TODO */ },
                    onRefreshQuestionClicked = { /* TODO */ },
                    onMonthlyTodayButtonClick = { currentYearMonth = YearMonth.from(LocalDate.now()); selectedDateForDetails = LocalDate.now() },
                    onEditEventRequest = { _, _ -> /* TODO */ },
                    onDeleteEventRequest = { _, _ -> /* TODO */ }
                    // modifier = Modifier // NavHost에 이미 innerPadding이 적용되어 있으므로, 여기서는 추가 Modifier 불필요
                )
            }
            composable(BottomNavItem.Message.route) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { Text("메시지 화면") }
            }
            composable(BottomNavItem.Gallery.route) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { Text("갤러리 화면") }
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(navController = appNavController) // AppNavigation의 NavController 전달
            }
        }
    }
}


@Preview(showBackground = true, name = "전체 홈 화면 (Scaffold 포함)", widthDp = 390, heightDp = 844)
@Composable
fun FullHomeScreenPreview() {
    DaytogetherTheme {
        // HomeScreen은 NavController를 필요로 하므로, Preview에서는 rememberNavController()로 임시 전달
        HomeScreen(appNavController = rememberNavController())
    }
}