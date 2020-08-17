package com.example.parayo.domain.user

import java.util.*
import javax.persistence.*

// 이 클래스가 데이터베이스 테이블에 맵핑된 정보를 가지고 있음을 의미.
// 이 클래스의 객체가 가진 데이터는 데이터베이스에 저장된 호긍 저장될
// 사용자 한 명의 정보를 대변.
@Entity(name = "users")
class User (
    // 2: email, password, name 값들은 필수이며, 그에 따라 null 값을 허용하지 않으려고
    // 생성자에 위치시키고 id 필드는 우리가 입력하지 않고 MySQL이 생성해준 값을 JPA가 입력
    // 해주기 때문에 null로 두었음. createdAt과 updatedAt은 JPA의 라이프사이클 훅을
    //    // 이용해 자동으로 입력해주기 위해 null로 둠.
    var email: String,
    var password: String,
    var name: String,
    var fcmToken: String?
) {
    @Id // 해당 필드가 이 테이블의 PK라는 것을 명시해줌.
    // MySQL에 PK 생성을 위임해 테이블에 새 데이터가 저장될 때 해당 필드가 자동으로 1씩 증가하여
    // 유니크한 값을 넣어주도록 합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt: Date? = null
    var updatedAt: Date? = null

    // 5. 데이터베이스에 새 데이터가 저장되기 전에 자동으로 호출되며
    // 여기에서는 데이터 저장 전에 가입일(데이터 생성일)을 현재 날짜로 지정해주도록 함.
    // 이렇게 하면 새 데이터를 저장할 때 매번 user.createdAt = Date()와 같은 코드를
    // 실행해야하는 번거로움이 사라지게 되며, 잘못된 날짜나 날짜를 누락시키는 실수 또한 방지할 수 있음.
    @PrePersist
    fun prePersist()  {
        createdAt = Date()
        updatedAt = Date()
    }

    // 6. @PrePersist와 마찬가지로 JPA의 라이프사이클 훅을 지정함.
    // 이 애노테이션이 붙은 함수는 데이터베이스에 데이터 업데이트 명령을 날리기 전에 실행 됨.
    @PreUpdate
    fun preUpdate() {
        updatedAt = Date()
    }
}



