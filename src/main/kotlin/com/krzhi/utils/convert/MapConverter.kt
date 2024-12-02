package com.krzhi.utils.convert

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.slf4j.LoggerFactory

@Converter
class MapConverter: AttributeConverter<Map<String, String>, String> {
    companion object {
        private val gson = Gson()
    }

    private val logger = LoggerFactory.getLogger(MapConverter::class.java)

    override fun convertToDatabaseColumn(specs: Map<String, String>): String {
        return try {
            gson.toJson(specs)
        } catch (t: Throwable) {
            logger.error("convert map to database fail", t)
            ""
        }
    }

    override fun convertToEntityAttribute(json: String): Map<String, String> {
        return try {
            val type = object : TypeToken<Map<String, String>>(){}.type
            gson.fromJson(json, type)
        } catch (t: Throwable) {
            logger.error("convert json to map fail", t)
            mapOf()
        }
    }
}