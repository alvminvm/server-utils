package com.krzhi.utils.convert

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.slf4j.LoggerFactory

@Converter
class StringListConverter: AttributeConverter<List<String>, String> {
    companion object {
        private val gson = Gson()
    }

    private val logger = LoggerFactory.getLogger(StringListConverter::class.java)

    override fun convertToDatabaseColumn(list: List<String>): String {
        return try {
            gson.toJson(list)
        } catch (t: Throwable) {
            logger.error("convert list to database fail", t)
            ""
        }
    }

    override fun convertToEntityAttribute(json: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>(){}.type
            gson.fromJson(json, type)
        } catch (t: Throwable) {
            logger.error("convert json to list fail", t)
            listOf()
        }
    }
}