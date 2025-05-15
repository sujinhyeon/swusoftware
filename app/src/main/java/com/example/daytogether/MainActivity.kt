package com.example.daytogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp // Divider 두께를 위해 추가
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.ui.home.ActualHomeScreenContent
import com.example.daytogether.ui.home.HomeScreen
import com.example.daytogether.ui.navigation.BottomNavItem
import com.example.daytogether.ui.navigation.Routes
import com.example.daytogether.ui.theme.DaytogetherTheme
import com.example.daytogether.ui.theme.NavIconSelected
import com.example.daytogether.ui.theme.NavIconUnselected
import com.example.daytogether.ui.theme.TextPrimary // 중복 제거 후 하나만 남김
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun HomeScreen() {
    // TODO: 실제 ViewModel 에서 상태를 받아오도록 교체하세요.
    ActualHomeScreenContent(
        upcomingAnniversaryText = "D-Day!",
        dDayText = "D-1",
        dDayTitle = "테스트",
        randomCloudResIds = listOf(R.drawable.cloud1, R.drawable.cloud2),
        currentYearMonth = YearMonth.now(),
        currentYearMonthFormatted = YearMonth.now()
            .format(DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREAN)),
        isMonthlyView = false,
        selectedDateForDetails = null,
        eventsByDate = emptyMap(),
        weeklyCalendarData = emptyList(),
        isQuestionAnsweredByAll = false,
        aiQuestion = "가족과 함께 하고 싶은 주말 활동은?",
        familyQuote = "\"가족은 최고의 선물입니다.\"",
        onMonthChange = {},
        onDateClick = {},
        onToggleCalendarView = {},
        onMonthlyCalendarHeaderTitleClick = {},
        onMonthlyCalendarHeaderIconClick = {},
        onMonthlyTodayButtonClick = {},
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


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaytogetherTheme {
                val navController = rememberNavController()
                val bottomNavItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Message,
                    BottomNavItem.Gallery,
                    BottomNavItem.Settings
                )

                Scaffold(
                    bottomBar = {
                        Column {
                            Divider(color = TextPrimary.copy(alpha = 0.3f), thickness = 1.dp) // 색상을 TextPrimary의 연한 버전으로
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                                bottomNavItems.forEach { screen ->
                                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = screen.iconResId),
                                                contentDescription = screen.label, // 접근성을 위해 contentDescription은 남겨둡니다.
                                                tint = if (isSelected) NavIconSelected else NavIconUnselected
                                            )
                                        },
                                        // label = { Text(...) }, // 글자 제외
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        alwaysShowLabel = false, // 라벨을 항상 숨김 (아이콘만 표시)
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavigationHost(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) { HomeScreen() }
        composable(Routes.MESSAGE) { Text("메시지 화면입니다.") } // 실제 화면으로 교체 필요
        composable(Routes.GALLERY) { Text("갤러리 화면입니다.") } // 실제 화면으로 교체 필요
        composable(Routes.SETTINGS) { Text("설정 화면입니다.") } // 실제 화면으로 교체 필요
    }
}