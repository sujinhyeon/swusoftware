package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daytogether.ui.theme.ButtonActiveBackground // 테마 색상 경로 확인
import com.example.daytogether.ui.theme.ButtonActiveText // 테마 색상 경로 확인
import com.example.daytogether.ui.theme.ButtonDisabledBackground // 테마 색상 경로 확인
import com.example.daytogether.ui.theme.ButtonDisabledText // 테마 색상 경로 확인

@Composable
fun RefreshQuestionButton(
    isAnsweredByAll: Boolean,
    onRefreshQuestionClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onRefreshQuestionClicked,
        enabled = isAnsweredByAll,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonActiveBackground,
            contentColor = ButtonActiveText,
            disabledContainerColor = ButtonDisabledBackground,
            disabledContentColor = ButtonDisabledText
        ),
        modifier = modifier
            .fillMaxWidth(0.50f)
            .height(38.dp)
    ) {
        Text(
            "질문 한 번 더 받기",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
            maxLines = 1
        )
    }
}