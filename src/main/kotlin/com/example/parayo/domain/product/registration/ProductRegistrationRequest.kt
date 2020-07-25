package com.example.parayo.domain.product.registration

data class ProductRegistrationRequest (
    val name: String,
    val description: String,
    val price: Int,
    val categoryId: Int,
    val imageIds: List<Long?> // 상품의 기본 정보 외에 미리 등록된 이미지들의 id들을 리스트로 받기 위해 Long 타입의 리스트를 프로퍼티로 추가했음. 이를 통해 데이터베이스로부터 이미지 정보를 읽어오기 위해 ProductImageRepository에 다음 함수를 추가해줌.
)
