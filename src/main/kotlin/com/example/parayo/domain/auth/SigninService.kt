package com.example.parayo.domain.auth

import com.example.parayo.common.ParayoException
import com.example.parayo.domain.user.User
import com.example.parayo.domain.user.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class SigninService @Autowired constructor(
    private val userRepository: UserRepository
) {

    // 데이터베이스에 해당 이메일을 사용하는 유저가 존재하는지 검색을 한 후 존재하지 않는다면 "로그인 정보를 확인해주세요"라는 메시지와 함께 Parayo Exception을 던짐. 이메일은 대소문자를 구분하지 않고 모두 소문자로 치환.
    fun signin(signinRequest: SigninRequest): SigninResponse {
        val user = userRepository
            .findByEmail(signinRequest.email.toLowerCase())
            ?: throw ParayoException("로그인 정보를 확인해주세요.")

        if (isNotValidPassword(signinRequest.password, user.password)) {
            throw ParayoException("로그인 정보를 확인해주세요.")
        }

        user.fcmToken = signinRequest.fcmToken
        userRepository.save(user)

        return responseWithTokens(user)
    }

    // 회원가입 시에 이메일이 아닌 형식으로 가입되는 유저는 없기 때문에 이메일 형식 검증은 생략. 그리고 BCrypt.checkpw(...)로 입력한 비밀번호가 데이터베이스의 비밀번호 해시값과 일치하는지를 판별한 후 일치하는 경우에만 토큰을 생성해 반환하도록 구현.
    private fun isNotValidPassword (
        plain: String,
        hashed: String
    ) = BCrypt.checkpw(plain, hashed).not()

    private fun responseWithTokens(user: User) = user.id?.let { userId ->
        SigninResponse(
            JWTUtil.createToken(user.email),
            JWTUtil.createRefreshToken(user.email),
            user.name,
            userId
        )
    } ?: throw IllegalStateException("User.id  없음.")
}
