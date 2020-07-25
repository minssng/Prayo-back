package com.example.parayo.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository // 1: 이 인터페이스가 스프링이 관리하는 레파지토리 빈으로서 동작한다는 것을 나타냄. 레파지토리는 의미적으로 이 클래스가 데이터의 읽기/쓰기 등을 담당한다는 것을 표시.
interface UserRepository : JpaRepository<User, Long> { // 2: JpaRepository를 상속받으면 레파지토리를 JPA 스펙에 맞게 확장하면서 기본적인 CRUD 함수를 제공할 수 있게 됨.
    fun findByEmail(email: String): User? // 이메일로 검색했을 때에는 한 명 혹은 0명의 유저만 존재해야 하기 때문에 User?를 반환타입으로 사용했음.
}
