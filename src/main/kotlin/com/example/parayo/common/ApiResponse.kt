package com.example.parayo.common

data class ApiResponse (
    val success: Boolean,
    val data: Any? = null,
    val message: String? = null
) {
    // ApiResponse의 생성자에 값을 넣어줄 수도 있지만 보다 명료하게 성공이나 실패 응답을 생성해주는 ok(),
    // error()라는 정적 함수를 함께 정의했습니다. 이제 우리는 ApiResponse.ok(data)와 같은 방식으로 API
    // 응답을 생성할 수 있습니다.
    companion object {
        fun ok(data: Any? = null) = ApiResponse(true, data)
        fun error(message: String? = null) =
            ApiResponse(false, message = message)
    }
}
