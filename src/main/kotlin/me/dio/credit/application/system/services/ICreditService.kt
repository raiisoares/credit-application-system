package me.dio.credit.application.system.services

import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import java.util.UUID

interface ICreditService {
    fun save(credit: Credit): Credit

    fun findAllByCustomer(customerId: Long): List<Credit>

    fun findByCreditCode(customerId: Long, creditCode: UUID): Credit
}