package me.dio.credit.application.system.services.impl

import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.repositories.CustomerRepository
import me.dio.credit.application.system.services.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(private val customerRepository: CustomerRepository) : ICustomerService {
    override fun save(cutomer: Customer): Customer = this.customerRepository.save(cutomer)

    override fun findById(id: Long): Customer {
        return this.customerRepository.findById(id).orElseThrow {
            throw RuntimeException("ERROR: Id $id not found.")
        }
    }

    override fun delete(id: Long) = this.customerRepository.deleteById(id)

}