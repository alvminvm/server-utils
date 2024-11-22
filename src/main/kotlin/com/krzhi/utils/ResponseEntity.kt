package com.krzhi.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> HttpStatus.toEntity() = ResponseEntity.status(this).build<T>()