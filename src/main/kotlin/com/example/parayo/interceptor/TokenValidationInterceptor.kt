package com.example.parayo.interceptor

import com.example.parayo.domain.auth.JWTUtil
import com.example.parayo.domain.auth.UserContextHolder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 검증된 토큰으로부터 이메일을 가져와 UserContextHolder에 넣어주는 로직.
 * 매 요청마다 동일한 코드를 작성해야 하는 것은 힘들지만 스프링의 HandlerInterceptor
 * 를 이용하면 이 비용을 줄일 수 잇음. 스프링의 HandlerInterceptor에는
 * 매 요청의 처리 전/후에 할 일을 정의할 수 있음. 이를 이용해 요청 처리 전에
 * 토큰을 검증하고 처리 후에는 ThreadLocal의 사용자 데이터를 초기화해주도록 함.
 */

@Component // 이 클래스가 스프링이  관리하는 빈 클래스임을 나타냄. @Service와는 기술적으로 동일하지만 의미상으로는 비즈니스 로직을 처리하는 클래스가 아니라는 점에서 다름.
// 스프링의 HandlerInterceptor 클래스를 상속받아 TokenValidationInerceptor를 만들고 앞서 만들었던 UserContextHolder를 사용하기 위해 생성자에게 주입 받음.
class TokenValidationInterceptor @Autowired constructor(
    private val userContextHolder: UserContextHolder
) : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(this::class.java) // 서버 사이드 로깅을 위해 로거 객체를 프로퍼티로 선언.

    companion object {
        private const val AUTHORIZATION = "Authorization" // AUTHORIZATION은 Authorization 토큰이 포함된 헤더 값을 가져오기 위한 상수.
        private  const val BEARER = "Bearer" // BEARER는 Authorization 헤더에 JWT 토큰을 전달할 때 사용되는 인증 방법을 나타내는 스키마. 실제 토큰을 사용하려면 헤더 값에서 이 문자열을 제거한 후 사용해야 함.
        private const val GRANT_TYPE = "grant_type" // GRANT_TYPE과 GRANT_TYPE_REFRESH는 토큰 재발행을 요청할 때 사용될 파라미터의 키와 값.
        const val GRANT_TYPE_REFRESH = "refresh_token"

        // DEFAULT_ALLOWED_API_URLS는 토큰 인증 없이 사용할 수 있는 URL들을 정의하기 위해 선언한 리스트. Spring Security나 여타 다른 라이브러리들을 사용한다면 더 쉽게 할 수 있음.
        private val DEFAULT_ALLOWED_API_URLS = listOf(
            "POST" to "/api/v1/signin",
            "POST" to "/api/v1/users"
        )
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val authHeader = request.getHeader(AUTHORIZATION) // request.getHeader(AUTHORIZATION)는 HttpServletRequest에 포함된 Authorization 헤더를 반환.

        // Authorization 헤더 값이 없는 경우는 허용된 URL인지를 검사해 진입 가능 여부를 결정하고, 값이 있는 경우에는 토큰을 추출해 유효성을 검증하고 사용자 정보를 세팅해주어야 함. 토큰이 유효하지 않거나 허용되지 않은 URL이라면 response.sendError(401)로 HTTP의 401 응답을 반환해 클라이언트에 권한이 없다는 것을 알려야 함.
        if (authHeader.isNullOrBlank()) {
            val pair = request.method to request.servletPath
            if (!DEFAULT_ALLOWED_API_URLS.contains(pair)) {
                response.sendError(401)
                return false // HandlerInterceptor는 여러 개 존재할 수 있기 때문에 다음 체인이 실행될지 말지를 결정해주기 위해 boolean 값을 반환합니다. 여기에서는 401 응답을 반환하는 경우 다음 체인이 실행될 필요가 없기 때문에 false를 반환하도록 했음.
            }
            return true
        } else {
            val grantType = request.getParameter("grant_type")
            val token = extractToken(authHeader)
            return handleToken(grantType, token, response)
        }
    }

    // handleToken() 함수에서는 앞서 준비된 JWTUtil의 함수를 이용해 토큰을 검중함. grant_type이 refresh_token인 경우 JWTUtil.verifyRefresh()를, 그렇지 않은 경우 JWTUtil.verify() 함수를 사용. 검증된 토큰에 있는 email 클레임을 이용해 UserContextHolder에 사용자 정보를 설정해주는데 토큰 검증에 실패한다면 앞에서와 마찬가지로 401을 반환.
    private fun handleToken(
        grantType: String,
        token: String,
        response: HttpServletResponse
    ) = try {
        val jwt = when(grantType) {
            GRANT_TYPE_REFRESH -> JWTUtil.verifyRefresh(token)
            else -> JWTUtil.verify(token)
        }
        val email = JWTUtil.extractEmail(jwt)
        userContextHolder.set(email)
        true
    } catch (e: Exception) {
        logger.error("토큰 검증 실패. token = $token", e)
        response.sendError(401)
        false
    }

    private fun extractToken(token: String) =
        token.replace(BEARER, "").trim()

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        userContextHolder.clear()
    }
}


