package com.example.daytogether.data.model // 사용자님의 실제 패키지명

data class Question(
    val id: String = "", // Firestore 문서 ID 등
    val text: String = "",
    val category: String = "", // 질문 카테고리 (선택적)
    // 답변 상태는 가족 그룹 정보나 채팅방 정보에서 관리될 수 있음
)