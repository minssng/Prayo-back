package com.example.parayo.domain.inquiry

import com.example.parayo.common.ParayoException
import com.example.parayo.domain.fcm.NotificationService
import com.example.parayo.domain.product.Product
import com.example.parayo.domain.product.ProductService
import com.example.parayo.domain.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InquiryService @Autowired constructor(
    private val inquiryRepository: InquiryRepository,
    private val productService: ProductService,
    private val userService: UserService,
    private val notificationService: NotificationService
) {
    fun register(request: InquiryRequest, userId: Long) {
        val product = productService.get(request.productId)
            ?: throw ParayoException("상품 정보를 찾을 수 없습니다.")

        val inquiry = saveInquiry(request, userId, product) // 문의 내용 저장
        sendNotification(request, inquiry) // 알람 보내기
    }

    // 상품 문의를 저장하는 함수. InquiryRequest의 Inquirytype이 QUESTION이면
    // 문의를 생성 및 저장하고 ANSWER이면 기존 문의를 조회해 answer 필드에 답변을
    // 추가해 업데이트.
    private fun saveInquiry(
        request: InquiryRequest,
        userId: Long,
        product: Product
    ) = if (request.type == InquiryType.QUESTION) {
        if (userId == product.userId) {
            throw ParayoException("자신의 상품에는 질문할 수 없습니다.")
        }
        val inquiry = Inquiry(
            request.productId,
            userId,
            product.userId,
            request.content
        )
        inquiryRepository.save(inquiry)
    } else {
        request.inquiryId
            ?.let(inquiryRepository::findByIdOrNull)
            ?.apply {
                // require는 인자의 값이 false일 때 InllegalargumentException을 던져주는
                // 함수. 비슷한 것으로는 IllegalStateException을 던지는 check() 함수와
                // AssertionError를 던지는 assert() 함수가 있음.
                require(productId == request.productId) { "답변 데이터 오류."}
                if (productOwnerId != userId) {
                    throw ParayoException("자신의 상품에만 답변할 수 있습니다.")
                }
                answer = request.content
                inquiryRepository.save(this)
            } ?: throw ParayoException("질문 데이터를 찾을 수 없습니다.")
    }

    // 알림을 받아야 할 대상에게 알림을 발송하는 함수. 문의인 경우에는
    // 상품 등록자에게 알림을 발송하고 답변인 경우에는 문의자에게 알림을
    // 발송.
    private fun sendNotification(
        request: InquiryRequest,
        inquiry: Inquiry
    ) {
        val targetUser = if (request.type == InquiryType.QUESTION) {
            userService.find(inquiry.productOwnerId)
        } else {
            userService.find(inquiry.requestUserId)
        }

        targetUser?.run {
            notificationService.sendToUser(this, "상품문의", request.content)
        }
    }
}
