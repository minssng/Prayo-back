package com.example.parayo.domain.product

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ProductService @Autowired constructor(
    private val productRepository: ProductRepository
) {
    fun search(
        categoryId: Int?,
        productId: Long,
        direction: String,
        limit: Int
    ): List<Product> {
        // 각 조건에 맞는 0페이지를 limit 수만큼 가져오기 위한 Pageable을 상속받은 객체.
        val pageable = PageRequest.of(0, limit)
        // 상품의 검색 조건을 표현하기 위한 객체. 상품 검색에서는 특정 카테고리에 제한해서만
        // 상품을 검색하지 않을 것이고, 이 search 함수는 삼풍 검색 부분에서도 쓰일 함수이기
        // 때문에 카테고리의 id가 null 인지 여부를 조건을 추가.
        val condition = ProductSearchCondition(
            categoryId != null,
            direction
        )

        // 코틀린의 when 절에서는 객체의 비교도 허용하기 때문에 이와 같이 사용할 수가 있음.
        // 이는 코틀린 when 절의 강력한 기능 중 하나로 더욱 간결한 코드를 작성할 수 있도록
        // 도와줌. when 절에 쓰인 객체는 4번 항목의 객체로 5번의 companion object {} 블록
        // 안에 정의.
        return when(condition) {
            NEXT_IN_CATEGORY -> productRepository
                .findByCategoryIdAndIdLessThanOrderByIdDesc(
                    categoryId, productId, pageable)
            PREV_IN_CATEGORY -> productRepository
                .findByCategoryIdAndIdGreaterThanOrderByIdDesc(
                    categoryId, productId, pageable)
            else -> throw IllegalArgumentException("상품 검색 조건 오류")
        }
    }

    // 검색 조건을 표현하기 위한 클래스 정의.
    data class ProductSearchCondition(
        val categoryIdIsNotnull: Boolean,
        val direction: String
    )

    // ProductSearchCondition 클래스를 이용해 검색 조건들을 미리 정의.
    companion object {
        val NEXT_IN_CATEGORY = ProductSearchCondition(true, "next")
        val PREV_IN_CATEGORY = ProductSearchCondition(true, "prev")
    }
}
