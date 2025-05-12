package com.example.daytogether.data.model // 사용자님의 실제 패키지명

data class Anniversary(
    val id: String = "",
    val title: String = "",
    val date: Long = 0L, // Timestamp (밀리초)
    val type: String = "BIRTHDAY" // "BIRTHDAY", "WEDDING_ANNIVERSARY", "CUSTOM" 등
)