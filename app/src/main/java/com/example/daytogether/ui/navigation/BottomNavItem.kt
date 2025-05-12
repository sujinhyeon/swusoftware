package com.example.daytogether.ui.navigation // 사용자님의 실제 패키지명

import com.example.daytogether.R // R 클래스 임포트

// 네비게이션 경로 상수화
object Routes {
    const val HOME = "home"
    const val MESSAGE = "message"
    const val GALLERY = "gallery"
    const val SETTINGS = "settings"
}

// 하단 네비게이션 아이템 정보 정의
sealed class BottomNavItem(val route: String, val iconResId: Int, val label: String) {
    object Home : BottomNavItem(Routes.HOME, R.drawable.ic_home, "홈")
    object Message : BottomNavItem(Routes.MESSAGE, R.drawable.ic_message, "메시지")
    object Gallery : BottomNavItem(Routes.GALLERY, R.drawable.ic_gallery, "갤러리")
    object Settings : BottomNavItem(Routes.SETTINGS, R.drawable.ic_settings, "설정")
}