package com.example.daytogether.ui.auth

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.R
import com.example.daytogether.navigation.AppDestinations
import com.example.daytogether.ui.theme.DaytogetherTheme
import com.example.daytogether.ui.theme.ButtonActiveBackground
import com.example.daytogether.ui.theme.ButtonActiveText
import com.example.daytogether.ui.theme.ButtonDisabledBackground
import com.example.daytogether.ui.theme.ButtonDisabledText
import com.example.daytogether.ui.theme.ErrorRed
import com.example.daytogether.ui.theme.TextPrimary // TextPrimary 추가 (레이블 색상 등)

// LoginScreen의 다양한 상태를 위한 데이터 클래스 (Preview용) - 이전과 동일
data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val fromOnboarding: Boolean = false,
    val simulateFilled: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    fromOnboarding: Boolean = false,
    initialEmail: String = "",
    initialPassword: String = "",
    initialEmailError: String? = null
) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }
    var emailError by remember { mutableStateOf(initialEmailError) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val isLoginButtonEnabled = email.isNotBlank() && password.isNotBlank()

    DaytogetherTheme {
        Column( // 최상위 Column
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 32.dp)
                .verticalScroll(scrollState), // 스크롤 가능하게 유지
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // 콘텐츠를 위아래로 분산시키고, Spacer로 조절
        ) {
            // (1) 상단 여백용 Spacer (fromOnboarding에 따라 다른 크기)
            Spacer(modifier = Modifier.height(if (fromOnboarding) 80.dp else 120.dp)) // 값을 늘려서 전체적으로 아래로 밀기

            // (2) 주요 입력 필드 및 버튼들을 담는 Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
                // 이 Column에는 별도의 verticalArrangement를 주지 않거나,
                // Arrangement.Center로 내부 요소들을 중앙 정렬할 수 있습니다.
            ) {
                // ID(Email) 입력 필드 섹션
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ID(Email)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (emailError != null) {
                            Text(
                                text = emailError!!,
                                color = ErrorRed,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = { Text("이메일 주소를 입력해주세요") },
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (emailError != null) ErrorRed else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (emailError != null) ErrorRed else MaterialTheme.colorScheme.outline,
                            errorBorderColor = ErrorRed,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password 입력 필드 섹션
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("비밀번호를 입력해주세요") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            if (isLoginButtonEnabled) {
                                // TODO: 로그인 시도
                            }
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { /* ... */ },
                    enabled = isLoginButtonEnabled,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonActiveBackground,
                        contentColor = ButtonActiveText,
                        disabledContainerColor = ButtonDisabledBackground,
                        disabledContentColor = ButtonDisabledText
                    )
                ) {
                    Text("로그인", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    "SNS 계정으로 로그인",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
                ) {
                    SocialLoginIconButton(iconRes = R.drawable.ic_logo_naver, text = "네이버") { /* ... */ }
                    SocialLoginIconButton(iconRes = R.drawable.ic_logo_kakao, text = "카카오") { /* ... */ }
                }
            } // 주요 입력 필드 Column 끝

            // (3) 하단 링크들을 담는 Column (이 Column과 주요 입력 필드 Column 사이에 Spacer를 추가할 수도 있음)
            Column(
                modifier = Modifier.padding(bottom = if (fromOnboarding) 60.dp else 32.dp), // 하단 여백
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClickableText(
                    text = AnnotatedString("회원가입"),
                    onClick = { navController.navigate(AppDestinations.SIGNUP_ROUTE) },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        textDecoration = TextDecoration.Underline
                    )
                )
                ClickableText(
                    text = AnnotatedString("아이디/비밀번호 찾기"),
                    onClick = { navController.navigate(AppDestinations.FIND_ACCOUNT_ROUTE) },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

// SocialLoginIconButton 함수는 이전과 동일하게 유지
@Composable
fun SocialLoginIconButton(
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(68.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "$text 로그인",
            modifier = Modifier.fillMaxSize(0.75f)
        )
    }
}

// --- Preview 함수들 수정 ---

// Preview 상태 제공자 (이전과 동일)
class LoginScreenStateProvider : PreviewParameterProvider<LoginScreenState> {
    override val values = sequenceOf(
        LoginScreenState(), // 기본 (비활성 버튼)
        LoginScreenState(email = "test", emailError = "올바르지 않은 이메일 형식입니다."), // 이메일 오류
        LoginScreenState(email = "test@example.com", password = "password123", simulateFilled = true) // 입력 완료 (활성 버튼)
    )
}

@Preview(showBackground = true, name = "Login States (From Onboarding)", widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenAllStatesFromOnboardingPreview(
    @PreviewParameter(LoginScreenStateProvider::class) state: LoginScreenState
) {
    DaytogetherTheme {
        LoginScreen(
            navController = rememberNavController(),
            fromOnboarding = true, // 온보딩에서 호출된 것처럼
            initialEmail = state.email,
            initialPassword = if (state.simulateFilled) state.password else "",
            initialEmailError = state.emailError
        )
    }
}

@Preview(showBackground = true, name = "Login States (Standalone)", widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenAllStatesStandalonePreview(
    @PreviewParameter(LoginScreenStateProvider::class) state: LoginScreenState
) {
    DaytogetherTheme {
        LoginScreen(
            navController = rememberNavController(),
            fromOnboarding = false, // 단독 화면으로 호출된 것처럼
            initialEmail = state.email,
            initialPassword = if (state.simulateFilled) state.password else "",
            initialEmailError = state.emailError
        )
    }
}