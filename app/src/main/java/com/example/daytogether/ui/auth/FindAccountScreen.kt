package com.example.daytogether.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun FindAccountScreen(navController: NavController) {
    // 아이디 찾기 관련 상태
    var findIdName by remember { mutableStateOf("") }
    var findIdPhoneNumber by remember { mutableStateOf("") }

    // 비밀번호 찾기 관련 상태
    var findPwName by remember { mutableStateOf("") }
    var findPwEmail by remember { mutableStateOf("") }

    DaytogetherTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("아이디/비밀번호 찾기") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
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
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // TODO: 아이디 찾기 UI 구현 (이름, 휴대폰 번호 입력, 찾기 버튼)
                Text("아이디 찾기 섹션 (구현 예정)", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = findIdName,
                    onValueChange = { findIdName = it },
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth()
                )
                // ...

                Divider(modifier = Modifier.padding(vertical = 24.dp))

                // TODO: 비밀번호 찾기 UI 구현 (이름, 이메일 입력, 찾기 버튼)
                Text("비밀번호 찾기 섹션 (구현 예정)", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = findPwName,
                    onValueChange = { findPwName = it },
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth()
                )
                // ...
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun FindAccountScreenPreview() {
    DaytogetherTheme {
        FindAccountScreen(navController = rememberNavController())
    }
}