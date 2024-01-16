package me.dio.credit.application.system.services

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.application.system.entities.Address
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.exceptions.BusinessException
import me.dio.credit.application.system.repositories.CustomerRepository
import me.dio.credit.application.system.services.impl.CustomerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerService: CustomerService

    private lateinit var testCustomer: Customer
    private var testId: Long = 0

    private fun buildCustomer(
        id: Long = 1L,
        firstName: String = "Test",
        lastName: String = "LastName",
        cpf: String = "65717209061",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        email: String = "maria@email.com",
        password: String = "123456",
        zipCode: String = "321654987",
        street: String = "Some street"
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        income = income,
        email = email,
        password = password,
        id = id,
        address = Address(
            zipCode = zipCode,
            street = street
        )
    )

    @BeforeEach
    fun setup() {
        testCustomer = buildCustomer()
        testId = Random().nextLong()
        every { customerRepository.save(any()) } returns testCustomer
    }

    @Test
    fun `test should create customer`() {
        val actual: Customer = customerService.save(testCustomer)
        Assertions.assertNotNull(actual)
    }

    @Test
    fun `test created customer should be equals testCustomer`() {
        val actual: Customer = customerService.save(testCustomer)
        Assertions.assertEquals(testCustomer, actual)
    }

    @Test
    fun `test should find customer by id`() {
        testCustomer = buildCustomer(id = testId)
        every { customerRepository.findById(testId) } returns Optional.of(testCustomer)
        val actual: Customer = customerService.findById(testId)
        Assertions.assertEquals(testCustomer, actual)
    }

    @Test
    fun `test customer should not be null`() {
        testCustomer = buildCustomer(id = testId)
        every { customerRepository.findById(testId) } returns Optional.of(testCustomer)
        val actual: Customer = customerService.findById(testId)
        Assertions.assertNotNull(actual)
    }

    @Test
    fun `test throws exception when findById receives invalid Id`() {
        every { customerRepository.findById(testId) } returns Optional.empty()
        Assertions.assertThrows(BusinessException::class.java) { customerService.findById(testId) }
    }

    @Test
    fun `test exception has the expected message`() {
        every { customerRepository.findById(testId) } returns Optional.empty()
        val expected = "ERROR: Id $testId not found."
        val actual = Assertions.assertThrows(BusinessException::class.java) { customerService.findById(testId) }
        Assertions.assertEquals(expected, actual.message)
    }

    @Test
    fun `test should delete customer`() {
        testCustomer = buildCustomer(id = testId)
        every { customerRepository.findById(testId) } returns Optional.of(testCustomer)
        every { customerRepository.delete(testCustomer) } just runs
        customerService.delete(testId)
        verify(exactly = 1) { customerRepository.delete(any()) }
    }
}


