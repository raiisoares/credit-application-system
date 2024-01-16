package me.dio.credit.application.system.exceptions

import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import java.util.HashMap

@RestControllerAdvice
class RestExceptionHandler {
    private val badRequestMessage: String = "Bad Request! Consult the documentation!"
    private val conflictMessage: String = "Conflict! Consult the documentation!"

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerValidException(exceptions: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails> {
        val errors: MutableMap<String, String?> = HashMap()
        exceptions.bindingResult.allErrors.stream().forEach { error: ObjectError ->
            val fieldName: String = (error as FieldError).field
            val messageError: String? = error.defaultMessage
            errors[fieldName] = messageError
        }

        return ResponseEntity(
            ExceptionDetails(
                title = badRequestMessage,
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = exceptions.javaClass.toString(),
                details = errors
            ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(DataAccessException::class)
    fun handlerDataException(exception: DataAccessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
            ExceptionDetails(
                title = conflictMessage,
                timestamp = LocalDateTime.now(),
                status = HttpStatus.CONFLICT.value(),
                exception = exception.javaClass.toString(),
                details = mutableMapOf(exception.cause.toString() to exception.message)
            ), HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(BusinessException::class)
    fun handlerBusinessException(exception: BusinessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
            ExceptionDetails(
                title = badRequestMessage,
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = exception.javaClass.toString(),
                details = mutableMapOf(exception.cause.toString() to exception.message)
            ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handlerBusinessException(exception: IllegalArgumentException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
            ExceptionDetails(
                title = badRequestMessage,
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = exception.javaClass.toString(),
                details = mutableMapOf(exception.cause.toString() to exception.message)
            ), HttpStatus.BAD_REQUEST
        )
    }

}