package me.dio.credit.application.system.repositories

import me.dio.credit.application.system.entities.Address
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired
    lateinit var creditRepository: CreditRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customerTest: Customer
    private lateinit var creditTest01: Credit
    private lateinit var creditTest02: Credit
    private lateinit var creditCode01: UUID
    private lateinit var creditCode02: UUID

    private fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(2000.0),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 10,
        customer: Customer
    ) = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = customer
    )

    private fun buildCustomer(
        firstName: String = "Test",
        lastName: String = "LastName",
        cpf: String = "65717209061",
        email: String = "maria@email.com",
        password: String = "123456",
        zipCode: String = "321654987",
        street: String = "Some street",
        income: BigDecimal = BigDecimal.valueOf(1000.0)
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        income = income,
        address = Address(
            zipCode = zipCode,
            street = street
        )
    )

    @BeforeEach
    fun setUp() {
        customerTest = testEntityManager.persist(buildCustomer())
        creditTest01 = testEntityManager.persist(buildCredit(customer = customerTest))
        creditTest02 = testEntityManager.persist(buildCredit(customer = customerTest))
        creditCode01 = creditTest01.creditCode
        creditCode02 = creditTest02.creditCode
    }

    @Test
    fun `should not return null if credit exists`() {
        val credit: Credit = creditRepository.findByCreditCode(creditCode01)!!
        Assertions.assertNotNull(credit)
    }

    @Test
    fun `should find credit by credit code 01`() {
        val expected = creditTest01
        val actual: Credit = creditRepository.findByCreditCode(creditCode01)!!
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should find credit by credit code 02`() {
        val expected = creditTest02
        val actual: Credit = creditRepository.findByCreditCode(creditCode02)!!
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should have the expected customer`() {
        val expected = creditTest01
        val actual: Credit = creditRepository.findByCreditCode(creditCode01)!!
        Assertions.assertEquals(expected.customer, actual.customer)
    }

    @Test
    fun `should not be empty when customer has credits`() {
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerTest.id!!)
        Assertions.assertFalse(creditList.isEmpty())
    }

    @Test
    fun `should find all credits by customer id`() {
        val expected: List<Credit> = listOf(creditTest01, creditTest02)
        val actual: List<Credit>? = customerTest.id?.let { creditRepository.findAllByCustomerId(it) }
        Assertions.assertEquals(expected, actual)
    }

}