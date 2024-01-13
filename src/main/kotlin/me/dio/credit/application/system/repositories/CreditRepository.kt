package me.dio.credit.application.system.repositories

import me.dio.credit.application.system.entities.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CreditRepository : JpaRepository<Credit, Long> {
    fun findByCreditCode(creditCode: UUID): Credit?

    @Query(value = "SELECT * FROM CREDIT WHERE CUSTOMER_ID = :customerId", nativeQuery = true)
    fun findAllByCustomerId(@Param("customerId") customerId: Long): List<Credit>
}