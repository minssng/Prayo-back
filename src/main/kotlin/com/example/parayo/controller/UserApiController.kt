package com.example.parayo.controller

import com.example.parayo.common.ApiResponse
import com.example.parayo.domain.auth.SignupRequest
import com.example.parayo.domain.auth.SignupService
import com.example.parayo.domain.auth.UserContextHolder
import com.example.parayo.domain.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class UserApiController @Autowired constructor(
    private val signupService: SignupService,
    private val userContextHolder: UserContextHolder,
    private val userService: UserService
) {
    @PostMapping("/user") // HTTP의 POST 메서드를 이용해 맵핑된 주소를 호출했을때 함수가 동작함. 데이터를 저장하는 API는 대부분 POST 메서드를 사용하도록 권장
    fun signup(@RequestBody signupRequest: SignupRequest) = // 데이터를 HTTP의 바디에서 읽는다는 것을 의미함. ?password=test처럼 URI에 따라 붙는 쿼리스트링은 웹서버의 로그에 그대로 저장되거나 웹브라우저의 히스토리 캐시에 저장될 수 있으므로 보안상 치명적일 수 있습니다. 이를 피하기 위해서 데이터를 바디에 담고 HTTPS를 이용해 통신하면 회원 가입 시 비밀번호를 탈취당하는 기본적인 보안 문제를 피해갈 수 있음.
        ApiResponse.ok(signupService.signup(signupRequest))

    @PutMapping("/users/fcm-token")
    fun updateFcmToken(@RequestBody fcmToken: String) =
        userContextHolder.id?.let { userId ->
            ApiResponse.ok(userService.updateFcmToken(userId, fcmToken))
        } ?: ApiResponse.error("토큰 갱신 실패")
}
