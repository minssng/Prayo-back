package com.example.parayo.common

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice // 스프링에게 이 클래스가 전역적인 익셉션 핸들러임을 알려주는 애노테이션
@RestController
class ParayoExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(ParayoException::class) // 이 함수가 괄호 안에 들어간 타입의 예외를 처리할 것이라는 것을 알려줌
    fun handleParayoException(e: ParayoException): ApiResponse {
        logger.error("API error", e)
        return ApiResponse.error(e.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ApiResponse {
        logger.error("API error", e)
        return ApiResponse.error("알 수 없는 오류가 발생했습니다.")
    }
}
