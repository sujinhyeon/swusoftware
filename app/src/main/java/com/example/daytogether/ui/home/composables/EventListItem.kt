package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.data.model.CalendarEvent
import com.example.daytogether.ui.theme.TextPrimary

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
            .padding(vertical = 8.dp, horizontal = 8.dp), // 패딩 조정
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = event.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary, // TextPrimary는 테마에서 가져오거나 정의되어 있어야 합니다.
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Box { // IconButton과 DropdownMenu를 올바르게 정렬하기 위함
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "일정 옵션 더보기",
                    tint = TextPrimary.copy(alpha = 0.7f) // TextPrimary 사용
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