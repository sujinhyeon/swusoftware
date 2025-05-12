package com.example.daytogether.ui.theme // 사용자님의 실제 패키지명

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 앱의 라이트 테마 색상표 (새로운 색상 적용)
private val LightColorScheme = lightColorScheme(
    primary = ButtonActiveBackground, // #533A28 (버튼 활성 배경, 주요 액션 색상)
    onPrimary = ButtonActiveText,     // #FFF3D9 (Primary 색상 위의 텍스트/아이콘)
    secondary = AnniversaryBoardBackground, // #CFBA94 (보조 색상, 전광판 배경 등)
    onSecondary = TextPrimary,        // #533A28 (Secondary 색상 위의 텍스트)
    tertiary = NavIconUnselected,     // #CFBA94 (추가적인 강조 색상)
    onTertiary = TextPrimary,         // #533A28
    background = ScreenBackground,    // #FFF3D9 (화면 전체 배경)
    onBackground = TextPrimary,       // #533A28 (배경 위의 텍스트)
    surface = ScreenBackground,       // #FFF3D9 (카드, 하단 바 등의 표면 색상 - 필요시 조정)
    onSurface = TextPrimary,          // #533A28 (표면 위의 텍스트)
    error = ErrorRed,                 // #FB1F1F
    onError = Color.White             // 에러 색상 위의 텍스트
    // 다른 Material3 색상들도 필요에 따라 정의 가능
)

// TODO: 다크 테마를 지원하려면 DarkColorScheme도 Figma 디자인에 맞게 정의합니다.
// 현재는 라이트 테마와 유사하게 설정하거나 기본값으로 둡니다.
private val DarkColorScheme = darkColorScheme(
    primary = NavIconUnselected, // 예시
    onPrimary = TextPrimary,
    secondary = ButtonActiveBackground,
    onSecondary = ButtonActiveText,
    background = Color(0xFF1E1E1E), // 어두운 배경
    onBackground = Color(0xFFE0E0E0), // 밝은 텍스트
    surface = Color(0xFF2A2A2A),
    onSurface = Color(0xFFE0E0E0),
    error = ErrorRed,
    onError = Color.Black
)

@Composable
fun DaytogetherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Dynamic Color는 앱 고유 테마와 충돌할 수 있어 false로 유지
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // 다크 모드 지원 시 (현재는 예시)
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 상태바 색상을 앱 배경색과 동일하게 하거나, Primary 색상으로 설정
            window.statusBarColor = colorScheme.background.toArgb() // 또는 colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme // 라이트 테마일 때 상태바 아이콘 어둡게
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // GothicA1 폰트가 적용된 AppTypography 사용
        content = content
    )
}