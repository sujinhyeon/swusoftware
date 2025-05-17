package com.example.daytogether.ui.onboarding

import androidx.compose.ui.text.font.FontWeight
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
    // ... (pagerState 및 onboardingPages 정의 유지) ...
    val pagerState = rememberPagerState() // pagerState를 여기서 선언
    val onboardingPages = listOf(
        OnboardingPageItem(
            imageRes = R.drawable.ic_cloud_sad,
            title = "가족과의 대화,\n점점 줄어들고 있진 않나요?",
            description = "바쁜 일상 속 놓치기 쉬운 소중한 대화의 순간들\n돌아보는 시간을 가져보세요."
        ),
        OnboardingPageItem(
            imageRes = R.drawable.ic_cloud_happy,
            title = "소소한 대화가 만드는\n따뜻한 가족의 유대감",
            description = "하루함께가 건네는 한 줄 질문으로\n서로의 하루를 채워 보세요"
        )
    )


    DaytogetherTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HorizontalPager(
                count = 3,
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
                    .padding(vertical = 32.dp),
                activeColor = PagerIndicatorActive,
                inactiveColor = PagerIndicatorInactive,
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
            .padding(horizontal = 30.dp)
            // 전체적인 내용을 아래로 조금 더 내리기 위해 상단에 여백 추가 또는 verticalArrangement 조정
            .padding(top = 120.dp), // <<-- 상단 여백 추가 (값은 조절 가능)
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // 위에서부터 배치 시작 (Spacer로 간격 조절 용이)
    ) {
        Spacer(modifier = Modifier.weight(0.15f)) // 상단 공간을 약간 띄움 (비율로 조정)

        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier
                .size(width = 800.dp, height = 440.dp)
                .padding(bottom = 0.dp) // 이미지와 제목 사이 간격 (조절)
        )

        Spacer(modifier = Modifier.height(0.dp)) // 제목 위 간격 (조절)

        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineLarge.copy( // Type.kt의 스타일을 기반으로 굵기만 변경
                fontWeight = FontWeight.Medium // 기존 ExtraBold에서 Bold로 변경 (또는 SemiBold)
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp) // 제목과 구분선 사이 간격 (조절)
        )
        Divider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 20.dp) // 구분선과 설명 사이 간격 (조절)
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.25f)) // 하단 공간 확보 (페이지 표시기와 겹치지 않도록)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun OnboardingScreenPreview() {
    DaytogetherTheme {
        OnboardingScreen(navController = rememberNavController())
    }
}

