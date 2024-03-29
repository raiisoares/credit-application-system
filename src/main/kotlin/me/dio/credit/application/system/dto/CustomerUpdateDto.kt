package me.dio.credit.application.system.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entities.Customer
import java.math.BigDecimal

data class CustomerUpdateDto(
    @field:NotEmpty(message = "Name cannot be empty.")
    val firstName: String,

    @field:NotEmpty(message = "Last Name cannot be empty.")
    val lastName: String,

    @field:NotNull(message = "Invalid input.")
    val income: BigDecimal,

    @field:NotEmpty(message = "Zip Code cannot be empty.")
    val zipCode: String,

    @field:NotEmpty(message = "Street cannot be empty.")
    val street: String,
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street

        return customer
    }
}
