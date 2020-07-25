package com.example.parayo.domain.product

import com.example.parayo.common.ParayoException
import com.example.parayo.domain.product.registration.ProductImageUploadResponse
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.geometry.Positions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Service
class ProductImageService @Autowired constructor(
    private val productImageRepository: ProductImageRepository
) {
    @Value("\${parayo.file-upload.default-dir}") // application.yml에 기입한 파일 업로드 디렉토리 설정을 읽어 애노테이션이 붙은 변수에 대입해주는 역할을 함.
    var uploadPath: String? = ""

    fun uploadImage(image: MultipartFile) // MultipartFile은 파일 업로드를 할 때 컨트롤러로 이입되는 업로드 파일 데이터 객체. 여기에는 파일의 정보들과 파일을 저장할 때 사용되는 함수 등이 있음.
        : ProductImageUploadResponse {
        val filePath = saveImageFile(image)
        val productImage = saveImageData(filePath)

        return productImage.id?.let {
            ProductImageUploadResponse(it, filePath)
        } ?: throw ParayoException("이미지 저장 실패. 다시 시도해주세요.")
    }

    private fun saveImageFile(image: MultipartFile): String {
        val extension = image.originalFilename
            ?.takeLastWhile { it != '.'} // (Char) -> Boolean 타입의 함수를 인자로 받으며, 함수의 반환값이 true가 되기 전까지의 마지막 문자열을 반환. 여기서는 '.'을 기준으로 파일의 확장자를 구하는 용도.
            ?: throw ParayoException("다른 이미지로 다시 시도해주세요.")

        val uuid = UUID.randomUUID().toString() // 파일명을 그대로 저장한다면 서로 다른 사용자가 같은 이름을 가진 파일을 업로드했을 때 그대로 덮어쓸 위험이 있기 때문에 날짜로 된 디렉토리 안에 UUID.randomUUID()로 생성한 랜덤한 문자여ㅕㄹ을 파일명으로 사용해 저장함.
        val date = SimpleDateFormat("yyyyMMdd").format(Date())

        val filePath = "/images/$date/$uuid.$extension"
        val targetFile = File("$uploadPath/$filePath")
        val thumbnail = targetFile.absolutePath
            .replace(uuid, "$uuid-thumb")
            .let(::File)

        targetFile.parentFile.mkdirs() // targetFile.parentFile.mkdirs()는 파일이 저장될 디렉토리를 생성하는 함수. 이 부분에서 오류가 발생한다면 디렉토리에 쓰기 권한이 있는지를 먼저 확인하고 쓰기 권한이 없다면 앞서 언급한 방법대로 셸의 chmod 커맨드를 이용해 권한을 추가해주어야 함.
        image.transferTo(targetFile) // image.transferTo(...) 함수는 MultipartFile 클래스에 선언된 함수로 업로드 파일을 파라미터로 지정된 파일 경로에 저장해주는 함수. 예제 코드에서 사용된 targetFile 변수는 /parayo/images/{년월일}/uuid.ext 형태의 경로를 가진 파일이므로 이 위치에 이미지가 저장되게 됩니다.

        // Thumbnails.of(...) 체인은 앞서 추가한 thumbnailator 라이브러리에서 제공하는 이미지 리사이징 함수를 사용한 것. uuid 앞에 thumb-를 추가해 섬네일 파일로 저장함.
        Thumbnails.of(targetFile)
            .crop(Positions.CENTER)
            .size(300, 300)
            .outputFormat("jpg")
            .outputQuality(0.8f)
            .toFile(thumbnail)

        return filePath
    }

    private fun saveImageData(filePath: String): ProductImage {
        val productImage = ProductImage(filePath)
        return productImageRepository.save(productImage)
    }
}
