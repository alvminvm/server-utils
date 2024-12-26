package com.krzhi.utils.convert

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SoftDeleteConverter: AttributeConverter<Boolean, Long?> {
    override fun convertToDatabaseColumn(attribute: Boolean?): Long? {
        return if (attribute == true) System.currentTimeMillis() else null
    }

    override fun convertToEntityAttribute(dbData: Long?): Boolean {
        return (dbData ?: 0) > 0
    }
}