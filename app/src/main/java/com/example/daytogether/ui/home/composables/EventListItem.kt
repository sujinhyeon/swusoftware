
package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.ui.theme.TextPrimary // 실제 테마 경로에 맞게

@Composable
fun EventListItem(
    event: CalendarEvent,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            // 각 항목의 높이를 일정하게 하려면 여기에 .height(일정_높이.dp) 추가
            .defaultMinSize(minHeight = 56.dp) // 최소 높이를 지정하여 내용이 적어도 일정 높이 유지
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = event.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f).padding(end = 8.dp), // 더보기 메뉴와의 간격
            maxLines = 2, // 텍스트가 길 경우 최대 두 줄로 제한
            overflow = TextOverflow.Ellipsis
        )

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "일정 옵션 더보기",
                    tint = TextPrimary.copy(alpha = 0.7f)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("수정") },
                    onClick = {
                        onEditClick()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("삭제") },
                    onClick = {
                        onDeleteClick()
                        showMenu = false
                    }
                )
            }
        }
    }
}