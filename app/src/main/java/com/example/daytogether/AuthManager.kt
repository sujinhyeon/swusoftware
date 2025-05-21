package com.example.daytogether

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //회원가입
    fun registerUser(
        name: String,
        email: String,
        password: String,
        position: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val memberId = user?.uid ?: ""

                    val memberData = hashMapOf(
                        "member_id" to memberId,
                        "name" to name,
                        "email" to email,
                        "position" to position
                    )

                    db.collection("members")
                        .document(memberId)
                        .set(memberData)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message)
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    //로그인
    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    //로그아웃
    fun logoutUser() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
