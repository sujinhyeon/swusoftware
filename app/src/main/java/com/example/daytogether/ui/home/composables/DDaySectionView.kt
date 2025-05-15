package com.example.daytogether.ui.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.daytogether.ui.theme.TextPrimary // 테마 색상 경로 확인

@Composable
fun DDaySectionView(
    dDayText: String,
    dDayTitle: String,
    cloudImageResList: List<Int>, // 예: List<Int> R.drawable.cloud1 등
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = dDayText,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dDayTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        cloudImageResList.forEach { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = "cloud",
                modifier = Modifier
                    .size(130.dp)
                    .padding(start = 0.dp) // 필요에 따라 이미지 간 간격 조정
            )
        }
    }
}