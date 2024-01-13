package me.dio.credit.application.system.services.impl

import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.repositories.CreditRepository
import me.dio.credit.application.system.services.ICreditService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
) : ICreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = credit.customer?.id?.let { customerService.findById(it) }
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> =
        this.creditRepository.findAllByCustomerId(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw RuntimeException("ERROR: Credit code $creditCode not found")

        return if (credit.customer?.id == customerId) credit else throw RuntimeException("ERROR: Contact admin")
    }
}