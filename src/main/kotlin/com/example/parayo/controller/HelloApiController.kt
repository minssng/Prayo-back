package com.example.parayo.controller

import com.example.parayo.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController // 스프링에서 HTTP 호출의 응답으로 뷰를 렌더링하지 않고 HTTPT의 본문에 직접 텍스트로 이루어진 데이터를 쓴다는 것을 나타냄
class HelloApiController {

    @GetMapping("/api/v1/hello") // HTTP의 GET 메서드를 통해 해당 함수를 실행하도록 이 반환값을 응답으로 되도력준다는 의미.
    fun hello() = ApiResponse.ok("world")
}
