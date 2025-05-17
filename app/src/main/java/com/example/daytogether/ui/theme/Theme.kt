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

// 앱의 라이트 테마 색상표
private val LightColorScheme = lightColorScheme(
    primary = ButtonActiveBackground,   // #533A28 (앱의 주요 액션/강조 색상)
    onPrimary = ButtonActiveText,       // #FFF3D9 (Primary 색상 위의 텍스트/아이콘)

    secondary = AnniversaryBoardBackground, // #CFBA94 (보조 색상, 덜 중요한 요소)
    onSecondary = TextPrimary,          // #533A28 (Secondary 색상 위의 텍스트)

    tertiary = NavIconUnselected,       // #CFBA94 (추가적인 강조 또는 보조 색상)
    onTertiary = TextPrimary,           // #533A28

    background = ScreenBackground,      // #FFF3D9 (대부분의 화면 배경)
    onBackground = TextPrimary,         // #533A28 (배경 위의 기본 텍스트)

    surface = ScreenBackground,         // #FFF3D9 (카드, 시트, 메뉴 등의 표면)
    onSurface = TextPrimary,            // #533A28 (표면 위의 텍스트)

    surfaceVariant = Brown100,          // #CFBA94 alpha 0.1 (표면의 변형, 약간 다른 배경색 등)
    onSurfaceVariant = TextPrimary,     // surfaceVariant 위의 텍스트

    outline = SelectedMonthlyBorder,    // #533A28 (입력 필드 테두리, 구분선 등)

    error = ErrorRed,                   // #FB1F1F
    onError = Color.White               // 에러 색상 위의 텍스트
)

// TODO: 다크 테마 디자인이 확정되면 DarkColorScheme을 구체적으로 정의합니다.
// 현재는 라이트 테마 색상을 기반으로 임시 설정하거나 Material 기본값을 활용할 수 있습니다.
private val DarkColorScheme = darkColorScheme(
    primary = NavIconUnselected, // 예: #CFBA94
    onPrimary = TextPrimary,     // 예: #533A28
    secondary = ButtonActiveBackground, // 예: #533A28
    onSecondary = ButtonActiveText,    // 예: #FFF3D9
    background = Color(0xFF1C1B1F), // 어두운 배경 (Material3 기본 다크 배경 근사치)
    onBackground = Color(0xFFE6E1E5), // 밝은 텍스트
    surface = Color(0xFF1C1B1F), // 카드 등
    onSurface = Color(0xFFE6E1E5),
    error = Color(0xFFF2B8B5), // Material3 다크 에러 색상
    onError = Color(0xFF601410)
)

@Composable
fun DaytogetherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 앱 고유 테마 유지를 위해 false 권장
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // 다크 모드 활성화 시
        else -> LightColorScheme      // 기본은 라이트 모드
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 상태바 색상을 앱 배경색과 동일하게 설정
            window.statusBarColor = colorScheme.background.toArgb()
            // 라이트 테마일 때 상태바 아이콘을 어둡게, 다크 테마일 때 밝게 설정
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // 위에서 정의한 AppTypography 사용
        content = content
    )
}