package me.dio.credit.application.system.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "Invalid Input!")
    val creditValue: BigDecimal,

    @field:Future
    val dayFirstInstallment: LocalDate,

    @field:Min(3)
    @field:Max(48)
    val numberOfInstallments: Int,

    @field:NotNull(message = "Invalid Input!")
    val customerId: Long
) {

    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
