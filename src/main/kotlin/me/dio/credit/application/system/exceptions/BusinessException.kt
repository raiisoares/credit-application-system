package me.dio.credit.application.system.exceptions

data class BusinessException(override val message: String) : RuntimeException(message)


