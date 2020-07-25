package com.example.parayo.domain.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JWTUtil {

    private const val ISSUER = "Parayo"
    private const val SUBJECT = "Auth"
    private const val EXPIRE_TIME = 60L * 60 * 2 * 1000 // 2시간
    private const val REFRESH_EXPIRE_TIME = 60L * 60 *24 * 30 * 1000 // 30일

    private val SECRET = "your-secret"
    private val algorithm: Algorithm = Algorithm.HMAC256(SECRET)

    private val refreshSecret = "your-refresh-secret"
    private val refreshAlgorithm:  Algorithm = Algorithm.HMAC256(refreshSecret)

    fun createToken(email: String) = JWT.create()
        .withIssuer(ISSUER)
        .withSubject(SUBJECT)
        .withIssuedAt(Date())
        .withExpiresAt(Date(Date().time + EXPIRE_TIME))
        .withClaim(JWTClaims.EMAIL, email)
        .sign(algorithm)

    fun createRefreshToken(email: String) = JWT.create()
        .withIssuer(ISSUER)
        .withSubject(SUBJECT)
        .withIssuedAt(Date())
        .withExpiresAt(Date(Date().time + REFRESH_EXPIRE_TIME))
        .withClaim(JWTClaims.EMAIL, email)
        .sign(refreshAlgorithm)

    fun verify(token: String): DecodedJWT =
        JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(token) // JWT 객체의 verify()는 토큰의 유효성을 검증하고 DecodedJWT 객체를 반환. 만일 토큰이 유효하지 않으면 예외를 던짐.

    fun verifyRefresh(token: String): DecodedJWT =
        JWT.require(refreshAlgorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(token)

    fun extractEmail(jwt: DecodedJWT): String =
        jwt.getClaim(JWTClaims.EMAIL).asString() // DecodedJWT 객체의 getClaim()은 앞서 설명했던 토큰의 클레임을 반환. 여기에서 로그인 시 토큰을 생성하며 저장한 email 클레임을 읽음.

    object JWTClaims {
        const val EMAIL = "email"
    }
}
