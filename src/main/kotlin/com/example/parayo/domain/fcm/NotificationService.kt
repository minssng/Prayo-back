package com.example.parayo.domain.fcm

import com.example.parayo.domain.user.User
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class NotificationService {

    // API 서버에서 푸시 알림을 보내기 위해서는 앞서 준비한 비공개 키를 이용해 Firebase SDK를
    // 초기화해줄 필요가 있기 때문에 애플리케이션 객체를 프로퍼티로 선언했습니다. 이 애플리케이션
    // 객체는 추후 Firebase의 다른 기능을 사용할 때 이용하게 될 수도 있으므로 Configuration을
    // 이용해 독립적인 빈으로 선언하는 것이 좋지만 여기에서는 푸시알림에 대해서만 설명하므로
    // NotificationService의 프로퍼티로 선언했습니다.
    private val firebaseApp by lazy {
        val inputStream = ClassPathResource(
            "parayo-eaaa3-firebase-adminsdk-d19i8-4f8e5c0264.json" // 2
        ).inputStream

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(inputStream))
            .build()

        FirebaseApp.initializeApp(options) // 3
    }

    fun sendToUser(user: User, title: String, content: String) =
        user.fcmToken?.let { token ->
            // 4
            val message = Message.builder()
                .setToken(token)
                .putData("title", title)
                .putData("cotent", content)
                .build()

            FirebaseMessaging.getInstance(firebaseApp).send(message) // 5
        }
}
