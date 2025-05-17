package com.example.daytogether.navigation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.ui.auth.LoginScreen // LoginScreen 임포트
import com.example.daytogether.ui.home.HomeScreen
import com.example.daytogether.ui.onboarding.OnboardingScreen
import com.example.daytogether.ui.splash.SplashScreen
import com.example.daytogether.ui.home.HomeScreen // MainScreen 임포트 (하단 네비게이션 포함)
// import com.example.daytogether.ui.auth.SignUpScreen // 실제 SignUpScreen 경로로 수정 필요
// import com.example.daytogether.ui.auth.FindAccountScreen // 실제 FindAccountScreen 경로로 수정 필요

// 라우트 정의 객체
object AppDestinations {
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
    const val LOGIN_ROUTE = "login" // 온보딩 페이저 내 로그인 외 별도 로그인 화면 접근 시
    const val MAIN_ROUTE = "main_graph" // 메인 화면 진입점 (하단 네비게이션을 포함하는 그래프)
    const val SIGNUP_ROUTE = "signup"
    const val FIND_ACCOUNT_ROUTE = "find_account"
}

@Composable
fun AppNavigation(
    // userPreferencesRepository: UserPreferencesRepository // DataStore/SharedPreferences 사용 시 주입
) {
    val navController = rememberNavController()

    // TODO: DataStore 또는 SharedPreferences를 사용하여 실제 '최초 실행 여부'를 관리해야 합니다.
    // 현재는 테스트를 위해 'true'로 고정하여 항상 온보딩이 보이도록 합니다.
    val isFirstLaunch by remember { mutableStateOf(true) }
    Log.d("AppNavigation", "isFirstLaunch 값: $isFirstLaunch")

    val startDestination = AppDestinations.SPLASH_ROUTE
    Log.d("AppNavigation", "AppNavigation NavHost startDestination: $startDestination")

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestinations.SPLASH_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.SPLASH_ROUTE}")
            SplashScreen(
                onTimeout = {
                    val route = if (isFirstLaunch) {
                        Log.d("AppNavigation", "SplashScreen Timeout: Navigating to ONBOARDING_ROUTE")
                        AppDestinations.ONBOARDING_ROUTE
                    } else {
                        Log.d("AppNavigation", "SplashScreen Timeout: Navigating to MAIN_ROUTE")
                        AppDestinations.MAIN_ROUTE
                    }
                    navController.navigate(route) {
                        popUpTo(AppDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.ONBOARDING_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.ONBOARDING_ROUTE}")
            OnboardingScreen(navController = navController)
            // 온보딩 완료 후 (예: LoginScreen에서 로그인 성공 시) isFirstLaunch 값을 false로 업데이트하는 로직 필요
            // 그리고 MAIN_ROUTE로 navigate 해야 함. (예: LoginScreen 내부에서 로그인 성공 시)
            // navController.navigate(AppDestinations.MAIN_ROUTE) {
            //     popUpTo(AppDestinations.ONBOARDING_ROUTE) { inclusive = true }
            // }
        }

        // 온보딩 페이저 내의 LoginScreen 외에, 다른 경로로 직접 LoginScreen에 접근해야 할 경우를 위한 라우트
        composable(AppDestinations.LOGIN_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.LOGIN_ROUTE}")
            LoginScreen(navController = navController, fromOnboarding = false)
        }

        composable(AppDestinations.SIGNUP_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.SIGNUP_ROUTE}")
            // 여기에 실제 회원가입 화면 컴포저블을 배치합니다.
            // 예: SignUpScreen(navController = navController)
            Text("회원가입 화면입니다. (구현 예정)") // 임시
        }

        composable(AppDestinations.FIND_ACCOUNT_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.FIND_ACCOUNT_ROUTE}")
            // 여기에 실제 아이디/비밀번호 찾기 화면 컴포저블을 배치합니다.
            // 예: FindAccountScreen(navController = navController)
            Text("아이디/비밀번호 찾기 화면입니다. (구현 예정)") // 임시
        }

        composable(AppDestinations.MAIN_ROUTE) {
            Log.d("AppNavigation", "Current Route: ${AppDestinations.MAIN_ROUTE}")
            // MainScreen은 내부에 Scaffold와 BottomNavigation, 그리고 자체 NavHost를 가집니다.
            HomeScreen()
        }
    }
}