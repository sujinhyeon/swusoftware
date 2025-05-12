package com.example.daytogether.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String? = null, // 이메일은 선택적일 수 있음
    val birthDate: String? = null, // "YYYY-MM-DD" 형식
    val familyRole: String? = null, // 예: "아빠", "엄마", "첫째"
    // 프로필 사진 URL 등 추가 정보
)