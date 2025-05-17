package com.example.daytogether.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import com.example.daytogether.R // R 클래스 경로 확인
import com.example.daytogether.ui.auth.LoginScreen // LoginScreen 임포트
import com.example.daytogether.ui.theme.DaytogetherTheme
import com.example.daytogether.ui.theme.PagerIndicatorActive // Color.kt에 정의된 색상
import com.example.daytogether.ui.theme.PagerIndicatorInactive // Color.kt에 정의된 색상

data class OnboardingPageItem(
    val imageRes: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val onboardingPages = listOf(
        OnboardingPageItem(
            imageRes = R.drawable.ic_cloud_sad, // 슬픈 구름 PNG
            title = "가족과의 대화,\n점점 줄어들고 있진 않나요?", // Figma 온보딩1 제목 참조 (Type.kt의 headlineLarge 스타일 적용)
            description = "바쁜 일상 속 놓치기 쉬운 소중한 대화의 순간들\n돌아보는 시간을 가져보세요." // Figma 온보딩1 설명 참조 (Type.kt의 bodyMedium 스타일 적용)
        ),
        OnboardingPageItem(
            imageRes = R.drawable.ic_cloud_happy, // 행복한 구름 PNG
            title = "소소한 대화가 만드는\n따뜻한 가족의 유대감", // Figma 온보딩2 제목 참조
            description = "하루함께가 건네는 한 줄 질문으로\n서로의 하루를 채워 보세요" // Figma 온보딩2 설명 참조
        )
    )

    DaytogetherTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HorizontalPager(
                count = 3, // 온보딩 2페이지 + 로그인 1페이지
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> OnboardingPageContent(item = onboardingPages[0])
                    1 -> OnboardingPageContent(item = onboardingPages[1])
                    2 -> LoginScreen(navController = navController, fromOnboarding = true)
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 32.dp), // Figma 이미지 참고하여 패딩 조절
                activeColor = PagerIndicatorActive, // Color.kt에 정의
                inactiveColor = PagerIndicatorInactive, // Color.kt에 정의
                indicatorWidth = 10.dp,
                indicatorHeight = 10.dp,
                spacing = 8.dp
            )
        }
    }
}

@Composable
fun OnboardingPageContent(item: OnboardingPageItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp), // Figma 이미지 참고, 좌우 패딩
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // 콘텐츠를 중앙에 배치
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier
                .fillMaxWidth(0.7f) // 구름 이미지 너비 조절
                .aspectRatio(1.8f) // 구름 이미지 비율 (가로가 더 길게)
                .padding(bottom = 48.dp) // Figma 이미지 참고, 아이콘과 제목 사이 간격
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineLarge, // Type.kt에서 정의
            color = MaterialTheme.colorScheme.primary, // TextPrimary 색상
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp) // Figma 이미지 참고, 제목과 구분선 사이 간격
        )
        Divider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), // 구분선 색상 및 투명도
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth(0.85f) // 구분선 길이
                .padding(bottom = 24.dp) // Figma 이미지 참고, 구분선과 설명 사이 간격
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium, // Type.kt에서 정의
            color = MaterialTheme.colorScheme.onBackground, // TextPrimary 또는 유사한 어두운 색상
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun OnboardingScreenPreview() {
    DaytogetherTheme {
        OnboardingScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun OnboardingPage1Preview() {
    DaytogetherTheme {
        OnboardingPageContent(
            item = OnboardingPageItem(
                R.drawable.ic_cloud_sad,
                "가족과의 대화,\n점점 줄어들고 있진 않나요?",
                "바쁜 일상 속 놓치기 쉬운 소중한 대화의 순간들\n돌아보는 시간을 가져보세요."
            )
        )
    }
}