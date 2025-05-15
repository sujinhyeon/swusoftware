package com.example.daytogether.ui.theme // 사용자님의 실제 패키지명

import androidx.compose.ui.graphics.Color

// Figma 기반 색상 정의
val ScreenBackground = Color(0xFFFFF3D9)
val AnniversaryBoardBackground = Color(0xFFCFBA94)
val TextPrimary = Color(0xFF533A28) // 글자 기본 색상

// 버튼 색상 - 활성화
val ButtonActiveText = Color(0xFFFFF3D9) // Figma Fill
val ButtonActiveBackground = Color(0xFF533A28) // Figma Fill

// 버튼 색상 - 비활성화
val ButtonDisabledText = Color(0xFF747474)
val ButtonDisabledBackground = Color(0xFFDADADA)

// 네비게이션 아이콘 색상
val NavIconSelected = Color(0xFF533A28)
val NavIconUnselected = Color(0xFFCFBA94)

val ErrorRed = Color(0xFFFB1F1F) // 빨간색



val TodayMonthlyBackground = AnniversaryBoardBackground.copy(alpha = 0.2f) // 이전 0.5f 또는 직접 지정한 값에서 변경
val SelectedMonthlyBorder = Color(0xFF533A28)
val OtherMonthDayText = Color(0xFFBFBFBF)

val Brown100 = AnniversaryBoardBackground.copy(alpha = 0.1f)

