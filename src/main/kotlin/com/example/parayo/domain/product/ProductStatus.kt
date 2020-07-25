package com.example.parayo.domain.product

/**
 * 상품의 판매 상태를 나타낼 enum 클래스
 **/
enum class ProductStatus(private val status: String) {
    SELLABLE("판매중"),
    SOLD_OUT("품절")
}
