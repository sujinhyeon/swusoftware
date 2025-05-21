package com.example.daytogether

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.daytogether.ui.theme.DaytogetherTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import com.example.daytogether.AuthManager

data class ChatMessage(
    val content: String,
    val sender: String,
    val timestamp: Date = Date()
)

class MainActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val messages = mutableStateListOf<ChatMessage>()
    private var currentUserId: String = "" // 현재 로그인된 유저 ID
    private var currentUserName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()

//        if (!AuthManager.isUserLoggedIn()) {
//            // 로그인 안되어 있으면 로그인 화면으로 이동
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }
        if (auth.currentUser == null) {
            // 로그인이 안 되어 있으면 로그인 화면 띄우거나, 자동 회원가입 시켜도 됨
            // 여기서는 테스트용으로 자동 로그인
            auth.signInWithEmailAndPassword("test@example.com", "123456")
                .addOnSuccessListener {
                    currentUserId = auth.currentUser?.uid ?: ""
                    loadCurrentUser()
                    listenForMessages()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
        } else {
            currentUserId = auth.currentUser?.uid ?: ""
            loadCurrentUser()
            listenForMessages()
        }

        listenForMessages()

        setContent {
            DaytogetherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ChatScreen(
                            messages = messages,
                            onSend = { message -> sendMessage(message) },
                            currentUserId = currentUserId
                        )
                    }
                }
            }
        }
    }


    private fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("members")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    currentUserName = document.getString("name") ?: "Unknown"
                }
            }
    }

    private fun sendMessage(text: String) {
        val message = hashMapOf(
            "sender" to currentUserName,
            "content" to text,
            "timestamp" to Date()
        )
        db.collection("chatRoom").add(message)
    }

    private fun listenForMessages() {
        db.collection("chatRoom")
            .orderBy("timestamp", Query.Direction.ASCENDING) // timestamp 기준 정렬 명시
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val newMessages = snapshot.documents.map { doc ->
                        val content = doc.getString("content") ?: ""
                        val sender = doc.getString("sender") ?: "unknown"
                        val timestamp = doc.getDate("timestamp") ?: Date()
                        ChatMessage(content, sender, timestamp)
                    }
                    messages.clear()
                    messages.addAll(newMessages.sortedBy { it.timestamp }) // 다시 로컬에서도 확실히 정렬
                }
            }
    }
}


@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    onSend: (String) -> Unit,
    currentUserId: String
) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages.size) { index ->
                val msg = messages[index]
                MessageBubble(message = msg, isMine = msg.sender == currentUserId)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f).padding(8.dp),
                placeholder = { Text("메시지를 입력하세요") }
            )
            Button(onClick = {
                if (input.isNotBlank()) {
                    onSend(input)
                    input = ""
                }
            }) {
                Text("전송")
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage, isMine: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.sender,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

