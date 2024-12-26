package com.krzhi.utils

import com.krzhi.utils.convert.SoftDeleteConverter
import jakarta.persistence.*
import org.hibernate.annotations.SoftDelete
import org.hibernate.annotations.SoftDeleteType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue
    var id: Long = 0

    @CreatedDate
    @Column(updatable = false)
    var createdAt: Long = System.currentTimeMillis()

    @LastModifiedDate
    var updatedAt: Long = System.currentTimeMillis()

    @SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.DELETED, converter = SoftDeleteConverter::class)
    @Column(nullable = true)
    var deleted: Boolean = false
}
