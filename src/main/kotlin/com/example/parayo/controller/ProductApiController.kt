package com.example.parayo.controller

import com.example.parayo.common.ApiResponse
import com.example.parayo.common.ParayoException
import com.example.parayo.domain.product.*
import com.example.parayo.domain.product.registration.ProductRegistrationRequest
import com.example.parayo.domain.product.registration.ProductRegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1")
class ProductApiController @Autowired constructor(
    private val productImageService: ProductImageService,
    private val productRegistration: ProductRegistrationService,
    private val productService: ProductService
) {
    @PostMapping("/product_images")
    fun uploadImage(image: MultipartFile) = ApiResponse.ok(
        productImageService.uploadImage(image)
    )

    @PostMapping("/products")
    fun register(
        @RequestBody request: ProductRegistrationRequest
    ) = ApiResponse.ok(
        productRegistration.register(request)
    )

    @GetMapping("/products")
    fun search(
        @RequestParam productId: Long,
        @RequestParam(required = false) categoryId: Int?,
        @RequestParam direction: String,
        @RequestParam(required = false) keyword: String,
        @RequestParam(required = false) limit: Int?
    ) = productService
        .search(categoryId, productId, direction, keyword, limit ?: 10)
        .mapNotNull(Product::toProductListItemResponse) // mapNotNull() 함수는 toProductListItemResponse()의 결과값이 null일 경우 결과 리스트에서 걸러줌. toProductListItemResponse()에서 Product의 id 값이 null일 경우 null을 반환했기 대문에 이 null 값을 건너뛰기 위해 사용.
        .let { ApiResponse.ok(it) }

    @GetMapping("/products/{id}")
    fun get(@PathVariable id: Long) = productService.get(id)?.let { // 상품 정보를 조회했을 때 정보가 있는 경우만 정상적인 응답을 반환
        ApiResponse.ok(it.toProductResponse())
    } ?: throw ParayoException("상품 정보를 찾을 수 없습니다.") // 상품 정보가 존재하지 않는다면 예외를 던짐
}
