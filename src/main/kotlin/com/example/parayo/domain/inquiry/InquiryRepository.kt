package com.example.parayo.domain.inquiry

import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryRepository : JpaRepository<Inquiry, Long> {
    fun findByProductIdAndIdLessThanOrderByIdDesc(
        productId: Long?,
        inquiryId: Long,
        pageable: PageRequest
    ) : List<Inquiry>

    fun findByProductIdAndIdGreaterThanOrderByIdDesc(
        productId: Long?,
        inquiryId: Long,
        pageable: PageRequest
    ) : List<Inquiry>

    fun findByRequestUserIdAndIdLessThanOrderByIdDesc(
        requestUserId: Long?,
        inquiryId: Long,
        pageable: PageRequest
    ) : List<Inquiry>

    fun findByRequestUserIdAndIdGreaterThanOrderByIdDesc(
        requestUserId: Long?,
        inquiryId: Long,
        pageable: PageRequest
    ) : List<Inquiry>

    fun findByProductOwnerIdAndIdLessThanOrderByIdDesc(
        productOwnerId: Long?,
        inquiryId: Long,
        pageable: PageRequest
    ) : List<Inquiry>

    fun findByProductOwnerIdAndIdGreaterThanOrderByIdDesc(
        productOwnerId: Long?,
        inquiryId: Long,
        pageable: PageRequest
    ) : List<Inquiry>
}
