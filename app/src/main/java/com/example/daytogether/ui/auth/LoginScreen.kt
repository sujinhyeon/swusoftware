package com.example.daytogether.ui.auth

import com.example.daytogether.navigation.AppDestinations

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.R // R 클래스 경로 확인
import com.example.daytogether.ui.theme.DaytogetherTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, fromOnboarding: Boolean = false) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    // var passwordError by remember { mutableStateOf<String?>(null) } // 필요시 비밀번호 오류 상태

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    DaytogetherTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 32.dp)
                .verticalScroll(scrollState), // 키보드 올라올 때 스크롤 가능하도록
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (fromOnboarding) Arrangement.Center else Arrangement.SpaceBetween // 온보딩에서는 중앙, 단독일땐 공간 분배
        ) {
            // 온보딩 페이저 내부에서는 상단 여백을 다르게 주거나 요소를 숨길 수 있음
            if (fromOnboarding) {
                Spacer(modifier = Modifier.height(60.dp)) // 온보딩 내 페이지일 경우 상단 공간 확보
            } else {
                // 단독 로그인 화면일 경우 로고나 앱 이름 표시 가능
                Text(
                    text = "하루 함께", // 앱 이름 또는 로고
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 40.sp), // 적절한 스타일
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 80.dp, bottom = 40.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null // 입력 시 오류 메시지 초기화
                    },
                    label = { Text("이메일") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    isError = emailError != null,
                    colors = OutlinedTextFieldDefaults.colors( // 또는 TextFieldDefaults.colors()
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        errorBorderColor = MaterialTheme.colorScheme.error,

                    )

                )
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        // TODO: 로그인 시도
                        // if (email.isEmpty() || password.isEmpty()) { emailError = "이메일을 입력해주세요."} else { /* 로그인 로직 */ }
                    }),
                    colors = OutlinedTextFieldDefaults.colors( // 또는 TextFieldDefaults.colors()
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        // focusedLabelColor = MaterialTheme.colorScheme.primary,
                        // cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (email.isBlank()) { // 간단한 유효성 검사 예시
                            emailError = "이메일을 입력해주세요."
                            return@Button
                        }
                        // TODO: ViewModel을 통해 로그인 로직 처리
                        // 예: navController.navigate(AppDestinations.MAIN_ROUTE) { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp), // 피그마 디자인의 버튼 모양
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // ButtonActiveBackground
                        contentColor = MaterialTheme.colorScheme.onPrimary  // ButtonActiveText
                    )
                ) {
                    Text("로그인", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "SNS 계정으로 로그인",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    SocialLoginButton(iconRes = R.drawable.ic_logo_naver, text = "네이버") {
                        // TODO: 네이버 로그인 로직
                    }
                    SocialLoginButton(iconRes = R.drawable.ic_logo_kakao, text = "카카오") {
                        // TODO: 카카오 로그인 로직
                    }
                }
            }


            Row(
                modifier = Modifier.padding(top = 24.dp, bottom = if (fromOnboarding) 80.dp else 32.dp), // 온보딩에서는 하단 공간 추가 확보
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ClickableText(
                    text = AnnotatedString("회원가입"),
                    onClick = { navController.navigate(AppDestinations.SIGNUP_ROUTE) }, // 회원가입 화면 라우트
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        textDecoration = TextDecoration.Underline
                    )
                )
                Text("|", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                ClickableText(
                    text = AnnotatedString("아이디/비밀번호 찾기"),
                    onClick = { navController.navigate(AppDestinations.FIND_ACCOUNT_ROUTE) }, // 계정 찾기 화면 라우트
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

@Composable
fun SocialLoginButton(
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton( // 또는 다른 버튼 스타일
        onClick = onClick,
        modifier = Modifier.size(60.dp), // 원형 버튼 크기
        shape = CircleShape, // 원형
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "$text 로그인",
            modifier = Modifier.size(28.dp) // 아이콘 크기
        )
    }
}

@Preview(showBackground = true, name = "Login In Onboarding", widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenFromOnboardingPreview() {
    DaytogetherTheme {
        LoginScreen(navController = rememberNavController(), fromOnboarding = true)
    }
}

@Preview(showBackground = true, name = "Login Standalone", widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenStandalonePreview() {
    DaytogetherTheme {
        LoginScreen(navController = rememberNavController(), fromOnboarding = false)
    }
}