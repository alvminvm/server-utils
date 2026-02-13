package com.krzhi.utils.dto

data class PageableData<T>(
    val page: Int = 0,
    val pageSize: Int = 15,
    val totalPage: Int = 0,
    val items: List<T> = emptyList(),
    val cursor: String? = null,
)
