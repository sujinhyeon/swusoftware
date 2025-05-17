package com.example.daytogether

import com.example.daytogether.navigation.AppNavigation
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.ui.navigation.BottomNavItem
import com.example.daytogether.ui.navigation.Routes
import com.example.daytogether.ui.theme.DaytogetherTheme
import com.example.daytogether.ui.theme.NavIconSelected
import com.example.daytogether.ui.theme.NavIconUnselected
import com.example.daytogether.ui.theme.TextPrimary
import com.example.daytogether.ui.home.HomeScreen
// import dagger.hilt.android.AndroidEntryPoint // Hilt 사용 시 (지금은 주석 처리)

// @AndroidEntryPoint // Hilt 사용 시
class MainActivity : ComponentActivity() {

    // @Inject // Hilt 사용 시 DataStore Repository 주입 예시
    // lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaytogetherTheme {
                // 기존 Scaffold 및 AppNavigationHost 대신 AppNavigation() 호출
                AppNavigation(
                    // userPreferencesRepository = userPreferencesRepository // DataStore 사용 시 주입
                )
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
        composable(Routes.HOME) {

            HomeScreen()
        }
        composable(Routes.MESSAGE) { Text("메시지 화면입니다.") } // 실제 화면으로 교체 필요
        composable(Routes.GALLERY) { Text("갤러리 화면입니다.") } // 실제 화면으로 교체 필요
        composable(Routes.SETTINGS) { Text("설정 화면입니다.") } // 실제 화면으로 교체 필요
    }
}