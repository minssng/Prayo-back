package com.example.parayo.domain.auth

import com.example.parayo.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * UserRepository로부터 사용자 정보를 읽어 ThreadLocal<UserHolder> 타입의
 * 프로퍼티에 사용자 정보를 저장해주는 set() 함수와 이를 초기화시켜주는 clear()
 * 함수를 가지고 있음. 우리의 사용자 정보 중 사용될만한 정보는 id, email, name 세가지가
 * 존재하므로 UserHolder 클래스에는 이 세 개의 프로퍼티를 선언해줌.
 */

@Service
class UserContextHolder @Autowired constructor(
    private val userRepository: UserRepository
) {
    private val userHolder = ThreadLocal.withInitial {
        UserHolder()
    }

    val id: Long? get() = userHolder.get().id
    val name: String? get() = userHolder.get().name
    val email: String? get() = userHolder.get().email

    fun set(email: String) = userRepository
        .findByEmail(email)?.let { user ->
            this.userHolder.get().apply {
                this.id = user.id
                this.name = user.name
                this.email = user.email
            }.run(userHolder::set)
        }

    fun clear() {
        userHolder.remove()
    }

    class UserHolder {
        var id: Long? = null
        var email: String? = null
        var name: String? = null
    }
}
