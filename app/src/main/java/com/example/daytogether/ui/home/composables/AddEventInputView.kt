package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday // 날짜 아이콘
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager // LocalFocusManager 사용시
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager // LocalFocusManager 사용시
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.daytogether.ui.theme.ButtonActiveBackground // 테마 색상
import com.example.daytogether.ui.theme.ScreenBackground // 테마 색상
import com.example.daytogether.ui.theme.TextPrimary // 테마 색상
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.TextFieldDefaults // OptIn 필요할 수 있음

@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun AddEventInputView(

    visible: Boolean,

    targetDate: LocalDate, // 새 일정을 추가할 대상 날짜

    eventDescription: String,

    onDescriptionChange: (String) -> Unit,

    onSave: () -> Unit,

    onCancel: () -> Unit,

    modifier: Modifier = Modifier

) {

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd (E)", Locale.KOREAN) // "새 일정 추가.png" 형식

    val focusManager = LocalFocusManager.current



// "일정추가.jpg"의 전체적인 따뜻한 배경색 (ScreenBackground 또는 유사한 색)

    val cardBackgroundColor = ScreenBackground



    AnimatedVisibility(

        visible = visible,

        enter = slideInVertically(animationSpec = tween(durationMillis = 250), initialOffsetY = { it }),

        exit = slideOutVertically(animationSpec = tween(durationMillis = 200), targetOffsetY = { it }),

        modifier = modifier.fillMaxWidth()

    ) {

        Card(

            modifier = Modifier

                .fillMaxWidth()

                .shadow(elevation = 0.dp, shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)) // 그림자 제거 또는 최소화

                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)), // 상단만 둥글게

            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),

            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)

        ) {

            Column(

                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp) // 하단 패딩 추가

            ) {

// 헤더: 취소, "새 일정", 저장("확인" 대신 "저장" 텍스트 사용)

                Row(

                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    TextButton(onClick = { focusManager.clearFocus(); onCancel() }) {

                        Text("취소", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary.copy(alpha = 0.8f)))

                    }

                    Text(

                        "새 일정",

                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)

                    )

                    TextButton(

                        onClick = { if (eventDescription.isNotBlank()) { focusManager.clearFocus(); onSave() } },

                        enabled = eventDescription.isNotBlank()

                    ) {

                        Text(

                            "저장", // "확인" 대신 "저장"

                            style = MaterialTheme.typography.bodyLarge.copy(

                                color = if (eventDescription.isNotBlank()) ButtonActiveBackground else TextPrimary.copy(alpha = 0.4f),

                                fontWeight = FontWeight.Bold

                            )

                        )

                    }

                }



// 제목 입력 필드 ("새 일정 추가.png" 레이아웃)

                OutlinedTextField(

                    value = eventDescription,

                    onValueChange = onDescriptionChange,

                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),

                    label = { Text("제목", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary.copy(alpha = 0.7f))) },

                    placeholder = { Text("일정 제목을 입력하세요", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary.copy(alpha = 0.5f))) },

                    singleLine = true,

                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),

                    keyboardActions = KeyboardActions(onDone = {

                        if (eventDescription.isNotBlank()) {

                            focusManager.clearFocus()

                            onSave()

                        } else {

                            focusManager.clearFocus() // 내용 없으면 포커스만 해제

                        }

                    }),

                    colors = TextFieldDefaults.colors( // "일정추가.jpg" 색감 참고

                        focusedContainerColor = Color.Transparent,

                        unfocusedContainerColor = Color.Transparent,

                        disabledContainerColor = Color.Transparent,

                        focusedIndicatorColor = TextPrimary,

                        unfocusedIndicatorColor = TextPrimary.copy(alpha = 0.3f),

                        focusedLabelColor = TextPrimary,

                        cursorColor = TextPrimary,

                        focusedTextColor = TextPrimary,

                        unfocusedTextColor = TextPrimary.copy(alpha = 0.8f)

                    ),

                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)

                )



// 날짜 표시 ("새 일정 추가.png" 레이아웃)

                Row(

                    verticalAlignment = Alignment.CenterVertically,

                    modifier = Modifier

                        .fillMaxWidth()

                        .clip(RoundedCornerShape(8.dp)) // 배경에 둥근 모서리

                        .background(TextPrimary.copy(alpha = 0.05f)) // 연한 배경색

                        .padding(horizontal = 12.dp, vertical = 10.dp)

                ) {

                    Icon(

                        imageVector = Icons.Filled.CalendarToday,

                        contentDescription = "날짜",

                        tint = TextPrimary.copy(alpha = 0.7f),

                        modifier = Modifier.size(20.dp)

                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(

                        text = targetDate.format(dateFormatter),

                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),

                        color = TextPrimary

                    )

                }



            }

        }

    }

}
