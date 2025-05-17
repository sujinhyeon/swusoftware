package com.example.daytogether.ui.theme // 사용자님의 실제 패키지명

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// GothicA1 폰트 패밀리를 기본으로 사용 (위에서 정의한 GothicA1 변수 사용)
val AppTypography = Typography(
    // 스플래시 화면 타이틀 ("하루 함께")
    displayLarge = TextStyle(
        fontFamily = GothicA1,
        fontWeight = FontWeight.Medium, // 가장 두꺼운 스타일
        fontSize = 70.sp,
        lineHeight = 78.sp, // 줄 간격 조절
        letterSpacing = (-0.25).sp
    ),
    // 온보딩 화면 제목 ("가족과의 대화...")
    headlineLarge = TextStyle(
        fontFamily = GothicA1,
        fontWeight = FontWeight.ExtraBold, // 충분히 굵게
        fontSize = 18.sp, // Figma 이미지와 유사하게 조절
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    // 온보딩 화면 본문 설명
    bodyMedium = TextStyle(
        fontFamily = GothicA1,
        fontWeight = FontWeight.Normal, // 일반 두께
        fontSize = 12.sp, // Figma 이미지와 유사하게 조절
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    ),

    // 로그인/회원가입 화면 제목 등
    titleLarge = TextStyle(
        fontFamily = GothicA1,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // 일반 버튼 텍스트
    labelLarge = TextStyle(
        fontFamily = GothicA1,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, // 버튼 텍스트 크기
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // 입력 필드 텍스트, 작은 설명 등
    bodySmall = TextStyle(
        fontFamily = GothicA1,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // 기본값으로 제공된 다른 스타일들도 필요에 따라 유지하거나 앱 디자인에 맞게 수정합니다.
    displayMedium = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.Black, fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp),
    displaySmall = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.Black, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp),
    headlineMedium = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp),
    headlineSmall = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    labelMedium = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontFamily = GothicA1, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp)
)