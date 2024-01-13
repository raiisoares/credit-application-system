package me.dio.credit.application.system.services

import me.dio.credit.application.system.entities.Credit
import java.util.UUID

interface CreditService {
    fun save(credit: Credit): Credit

    fun findAllByCustomer(customerId: Long): List<Credit>

    fun findByCreditCode(creditCode: UUID): Credit
}