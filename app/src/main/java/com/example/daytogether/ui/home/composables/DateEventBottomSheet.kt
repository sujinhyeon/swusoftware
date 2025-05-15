package com.example.daytogether.ui.home.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.ui.theme.TextPrimary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


private val EVENT_ITEM_APPROX_HEIGHT = 56.dp
private val DIVIDER_AND_PADDING_HEIGHT = 8.dp // 아이템 간 구분선 및 패딩 높이 합산 (예상치)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEventsBottomSheet(
    visible: Boolean,
    targetDate: LocalDate,
    events: List<CalendarEvent>,
    onDismiss: () -> Unit,
    onAddNewEventClick: () -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN)
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // 표시할 최대 아이템 수 (4개)
    val maxItemsToShowInitially = 4

    // LazyColumn이 차지할 최대 높이 계산 (4개 아이템 기준)
    // (아이템 높이 * 4) + (구분선/패딩 높이 * 3) -> 4개 아이템이면 구분선은 3개
    val lazyColumnContentMaxHeight = (EVENT_ITEM_APPROX_HEIGHT * maxItemsToShowInitially) + (DIVIDER_AND_PADDING_HEIGHT * (maxItemsToShowInitially - 1).coerceAtLeast(0))

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(animationSpec = tween(durationMillis = 250), initialOffsetY = { it }),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 200), targetOffsetY = { it }),
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Bottom) // 내용에 따라 높이 결정, 하단에 붙음
                .heightIn(max = screenHeight * 0.9f), // 화면의 90%를 넘지 않도록 최대 높이 제한
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp) // 하단 패딩도 추가
            ) {
                // --- 헤더: 날짜 및 닫기 버튼 ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = targetDate.format(dateFormatter),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "닫기", tint = TextPrimary.copy(alpha = 0.7f))
                    }
                }
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp)) // 헤더와 내용 사이 간격

                // --- 내용: 이벤트 목록 또는 "이벤트 없음" 안내 ---
                // 이 Box가 스크롤 가능한 LazyColumn의 높이를 제한합니다.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // 내용이 없을 때도 최소한의 높이를 가지도록, 내용이 많을 때는 계산된 최대 높이로 제한
                        .heightIn(min = 56.dp, max = lazyColumnContentMaxHeight)
                ) {
                    if (events.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(), // 부모 Box의 크기를 따름
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "등록된 일정이 없습니다.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight() // 부모 Box의 높이를 꽉 채움
                        ) {
                            itemsIndexed(items = events, key = { _, event -> event.id }) { index, event ->
                                EventListItem(
                                    event = event,
                                    onEditClick = { onEditEvent(event) },
                                    onDeleteClick = { onDeleteEvent(event) }
                                )
                                if (index < events.lastIndex) {
                                    HorizontalDivider(color = TextPrimary.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp)) // 내용과 하단 버튼 사이 간격

                // --- 하단 고정 버튼: 새 일정 만들기 ---
                Button(
                    onClick = onAddNewEventClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "새 일정 만들기 아이콘")
                    Spacer(Modifier.width(8.dp))
                    Text("새 일정 만들기", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}