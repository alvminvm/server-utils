package com.krzhi.utils

import com.krzhi.utils.convert.SoftDeleteConverter
import jakarta.persistence.*
import org.hibernate.annotations.SoftDelete
import org.hibernate.annotations.SoftDeleteType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
@SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.DELETED, converter = SoftDeleteConverter::class)
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @CreatedDate
    @Column(updatable = false, length = 13)
    var createdAt: Long = System.currentTimeMillis()

    @LastModifiedDate
    @Column(length = 13)
    var updatedAt: Long = System.currentTimeMillis()
}

@MappedSuperclass
abstract class UserBaseEntity: BaseEntity() {
    @Column(updatable = false)
    var uid = 0L
}