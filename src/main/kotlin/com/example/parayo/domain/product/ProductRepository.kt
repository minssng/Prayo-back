package com.example.parayo.domain.product

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    // 상품 리스트가 위쪽으로 스크롤될 때 호출되는 함수. 위쪽으로 올라가면서는
    // 최신 데이터를 보여주어야 하기 때문에 id 값이 보다 큰(GreaterThan) 상품들을
    // 내림차순(Desc)으로 읽어오도록 했음. 마지막 파라미터인 Pageable은 검색 조건을 기준으로
    // 페이지당 몇 개의 값을 읽을 것인지 설정하고 몇 번째 페이지의 값들을 읽어올지를
    // 설정 해줄 수 있는 객체. 즉 SQL에서의 "limit offset, rowCount"의 값인데
    // 여기에서는 페이지당 상품 개수를 설정하는 용도로만 사용할 것.
    fun findByCategoryIdAndIdGreaterThanOrderByIdDesc(
        categoryId: Int?, id: Long, pageable: Pageable
    ): List<Product>

    // 상품 리스트가 아래쪽으로 스크롤될 때 호출되는 함수. 이 경우에는 현재 로드된 것보다
    // 예전 데이터를 읽어야 하므로 id 값이 보다 작은(LessThan) 상품들을 내림차순(Desc)으로
    // 읽어오도록 했음.
    fun findByCategoryIdAndIdLessThanOrderByIdDesc(
        categoryId: Int?, id: Long, pageable: Pageable
    ): List<Product>

    fun findByIdGreaterThanAndNameLikeOrderByIdDesc(
        id: Long, keyword: String, pageable: Pageable
    ): List<Product>

    fun findByIdLessThanAndNameLikeOrderByIdDesc(
        id:  Long,  keywork:String, pageable:Pageable
    ): List<Product>
}
