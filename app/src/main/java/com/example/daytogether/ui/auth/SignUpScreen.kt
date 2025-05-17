package com.example.daytogether.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // AutoMirrored 아이콘 사용
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.daytogether.ui.theme.DaytogetherTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    // TODO: 약관 동의 상태 변수들 추가

    DaytogetherTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("회원가입") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // 뒤로가기 아이콘
                                contentDescription = "뒤로가기"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background, // 배경색과 동일하게
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding) // Scaffold의 내부 패딩 적용
                    .padding(horizontal = 32.dp, vertical = 24.dp), // 추가적인 화면 패딩
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp) // 요소들 간의 기본 간격
            ) {
                Text("회원가입 화면 내용을 채워주세요.", style = MaterialTheme.typography.bodyLarge)
                // TODO: 프로필 이미지, 이름, 생년월일, 이메일, 비밀번호, 비밀번호 확인 TextField 구현
                // TODO: 약관 동의 체크박스 구현
                // TODO: 회원가입 버튼 구현
                Button(onClick = { /* TODO: 회원가입 로직 및 화면 이동 */ }) {
                    Text("회원가입 완료 (임시)")
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SignUpScreenPreview() {
    DaytogetherTheme {
        SignUpScreen(navController = rememberNavController())
    }
}