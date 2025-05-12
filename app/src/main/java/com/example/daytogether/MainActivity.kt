package com.example.daytogether // 사용자님의 실제 패키지명

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // by navController.currentBackStackEntryAsState() 에 필요
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
// Navigation Compose 관련 필수 임포트들
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
// --- 여기까지 Navigation Compose 관련 필수 임포트 ---
import com.example.daytogether.ui.home.HomeScreen
import com.example.daytogether.ui.navigation.BottomNavItem // 사용자 정의 클래스
import com.example.daytogether.ui.navigation.Routes      // 사용자 정의 객체
import com.example.daytogether.ui.theme.DaytogetherTheme
import com.example.daytogether.ui.theme.NavIconSelected
import com.example.daytogether.ui.theme.NavIconUnselected

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaytogetherTheme {
                val navController = rememberNavController() // 여기!
                val bottomNavItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Message,
                    BottomNavItem.Gallery,
                    BottomNavItem.Settings
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination // 여기!

                            bottomNavItems.forEach { screen ->
                                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true // it, hierarchy
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = screen.iconResId),
                                            contentDescription = screen.label,
                                            tint = if (isSelected) NavIconSelected else NavIconUnselected
                                        )
                                    },
                                    label = {
                                        Text(
                                            screen.label,
                                            color = if (isSelected) NavIconSelected else NavIconUnselected
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        navController.navigate(screen.route) { // navigation
                                            popUpTo(navController.graph.findStartDestination().id) { // popUpTo, findStartDestination
                                                saveState = true // saveState
                                            }
                                            launchSingleTop = true // launchSingleTop
                                            restoreState = true // restoreState
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = MaterialTheme.colorScheme.surface
                                    )
                                )
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
fun AppNavigationHost(navController: NavHostController, modifier: Modifier = Modifier) { // NavHostController
    NavHost( // NavHost
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) { HomeScreen() } // composable
        composable(Routes.MESSAGE) { Text("메시지 화면입니다.") }
        composable(Routes.GALLERY) { Text("갤러리 화면입니다.") }
        composable(Routes.SETTINGS) { Text("설정 화면입니다.") }
    }
}