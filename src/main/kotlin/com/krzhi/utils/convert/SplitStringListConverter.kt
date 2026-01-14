package com.krzhi.utils.convert

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SplitStringListConverter: AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(list: List<String>): String {
        return list.joinToString(",")
    }

    override fun convertToEntityAttribute(value: String): List<String> {
        return value.split(",")
    }
}