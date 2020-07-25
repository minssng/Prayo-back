package com.example.parayo.domain.product

data class ProductListItemResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val description: String,
    val status: String,
    val sellerId: Long,
    val imagePaths: List<String>
)

// 확장함수를 이용해 Product 객체를 ProductListItemResponse 로 변환하는 함수
fun Product.toProductListItemResponse() = id?.let { // 레파지토리로부터 정상적으로 데이터를 읽어왔다면 Product의 id가 null인 경우는 발생하지 않겠지만 방어를 위해 id가 null인 경우 정상적이지 않은 데이터이므로 null을 반환
    ProductListItemResponse(
        it,
        name,
        price,
        description,
        status.name,
        userId,
        images.map { it.toThumbs() } // 아래에 ProductImage에 toThumbs()라는 확장 함수를 정의해 ProductImage를 섬네일 주소로 변경.
    )
}

// ProductImage의 확장 함수인 tothumbs(). 기본적으로는 파일명 뒤에 -thumb를 붙여주는 로직이지만
// 상품 이미지 등록 시 썸네일을 생성할 때 원본 파일의 확장자가 jpg가 아닌 경우 uuid-thumb.png.jpg
// 등의 형태로 원본 확장자 뒤에 .jpg가 붙도록 구현되어 있으므로 원본 확장자 뒤에 .jpg를 붙여주는
// 부가적인 로직도 포함되어 있음.
fun ProductImage.toThumbs(): String {
    val ext = path.takeLastWhile { it != '.' }
    val fileName = path.takeWhile { it != '.' }
    val thumbnailPath = "$fileName-thumb.$ext"

    return if (ext == "jpg") thumbnailPath else "$thumbnailPath.jpg"
}
