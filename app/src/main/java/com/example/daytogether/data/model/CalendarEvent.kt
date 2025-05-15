package com.example.daytogether.data.model // 파일의 패키지 선언

import java.util.UUID // UUID를 사용하기 위해 import

data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(), // 각 이벤트를 구분하기 위한 고유 ID
    val description: String,                       // 일정 내용 (이것이 "Unresolved reference: description" 오류의 원인)
    // 필요하다면 여기에 다른 속성들을 추가할 수 있습니다.
    // 예: val startTime: Long? = null,
    //     val endTime: Long? = null,
    //     val color: String? = null
)