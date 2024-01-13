package me.dio.credit.application.system.services

import me.dio.credit.application.system.entities.Customer

interface CustomerService {
    fun save(cutomer: Customer): Customer

    fun findById(id: Long): Customer

    fun delete(id: Long): Customer
}