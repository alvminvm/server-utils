package com.krzhi.utils.convert

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SoftDeleteConverter: AttributeConverter<Boolean, Long> {
    override fun convertToDatabaseColumn(deleted: Boolean): Long {
        return if (deleted) System.currentTimeMillis() else 0
    }

    override fun convertToEntityAttribute(deletedAt: Long): Boolean {
        return deletedAt > 0
    }
}