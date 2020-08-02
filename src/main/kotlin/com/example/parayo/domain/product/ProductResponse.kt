package com.example.parayo.domain.product

import com.example.parayo.common.ParayoException

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Int,
    val status: String,
    val sellerId: Long,
    val imagePaths: List<String>
)

fun Product.toProductResponse() = id?.let { // Product의 id는 널이 될 수 있기 때문에 널이 아닌 경우에만 정상적인 응답을 반환합니다.
    ProductResponse(
        it,
        name,
        description,
        price,
        status.name,
        userId,
        images.map { it.path }
    )
} ?: throw ParayoException("상품 정보를 찾을 수 없습니다.") // Product의 id가 널인 경우에는 예외를 던지도록 함.
