package com.example.daytogether.data.model // 사용자님의 실제 패키지명

data class CalendarEvent(
    val description: String // 이벤트 설명 (예: "회의", "점심 약속")
)

data class WeeklyCalendarDay(
    val date: String, // 날짜 (예: "14")
    val dayOfWeek: String, // 요일 (예: "일")
    val events: List<CalendarEvent> = emptyList(), // 해당 날짜의 이벤트 목록
    val isToday: Boolean = false
)