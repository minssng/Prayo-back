package com.example.parayo.domain.product

import com.example.parayo.domain.jpa.BaseEntity
import java.util.*
import javax.persistence.*

@Entity(name = "product")
class Product(
    @Column(length = 40) // @Column 은 해당 프로퍼티가 데이터베이스의 컬럼에 맵핑될 때의 속성들을 지정. 이 애노테이션을 생략하게 되면 프러퍼티명이 컬럼명이 되고 티폴트 옵션들을 사용하게 됨. 컬럼의 이름, 최대 길이, 유니크 키 여부 등 여러 옵션도 있음.
    var name: String,
    @Column(length = 500)
    var description: String,
    var price: Int,
    var categoryId: Int,
    @Enumerated(EnumType.STRING) // enum 클래스가 데이터베이스에 어떤 형식으로 저장되어야 할지를 지정. 디폴트는 ORDINAL로 정수 형태로 저장되지만 가독성 및 순서의 변경을 고려해 코드 상의 문자 그대로 저장되도록 EnumType.STRING을 지정.
    var status: ProductStatus,
    @OneToMany // Product 하나에 ProductImage 여러 개가 맵핑될 수 있다는 것을 나타냄. 때문에 images 프로퍼티는 MutableList 타입으로 선언되어야 함. 이 상황에서 HIbernate가 Product와 ProductImage를 맵픙해주기 위해 선택하는 기본 전략은 product와 product_image 테이블 사이에 product_product_image라는 관계 테이블을 하나 더 생성하는 것. 이때는 테이블도 지저분해지고 쿼리도 비효율적일 수 있기 때문에 다음에 설명하는 @JoinColumn을 추가해 FK를 이용한 조인 쿼리를 사용하는 것이 더 나은 방법.
    @JoinColumn(name = "productId") // 해당 엔티티의 PK와 맵핑되는 타겟 엔티티의 어떤 컬럼을 통해 두 테이블을 조인할 것인가를 지정. 앞의 코드에서는 ProductImage 엔티티에 productId 프로퍼티를 선언했고 @JoinColumn에 "productId"를 지정했으므로 ProductImage에 엔티티에 productId 프로퍼티를 선언했고 @JoinColumn에 "productId"를 지정했으므로 ProductImage에 productId라는 FK가 생성되고 이 값을 이용해 두 테이블 간 조인이 일어나게 됨.
    var images: MutableList<ProductImage>,
    val userId: Long
) : BaseEntity() {
}
