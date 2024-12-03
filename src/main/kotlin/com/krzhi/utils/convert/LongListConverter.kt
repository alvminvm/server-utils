package com.krzhi.utils.convert

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.slf4j.LoggerFactory

@Converter
class LongListConverter: AttributeConverter<List<Long>, String> {
    companion object {
        private val gson = Gson()
    }

    private val logger = LoggerFactory.getLogger(LongListConverter::class.java)

    override fun convertToDatabaseColumn(list: List<Long>): String {
        return try {
            gson.toJson(list)
        } catch (t: Throwable) {
            logger.error("convert map to database fail", t)
            ""
        }
    }

    override fun convertToEntityAttribute(json: String): List<Long> {
        return try {
            val type = object : TypeToken<List<Long>>(){}.type
            gson.fromJson(json, type)
        } catch (t: Throwable) {
            logger.error("convert json to map fail", t)
            listOf()
        }
    }
}