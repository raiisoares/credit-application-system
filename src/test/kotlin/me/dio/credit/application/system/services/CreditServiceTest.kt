package me.dio.credit.application.system.services

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.enummerations.Status
import me.dio.credit.application.system.exceptions.BusinessException
import me.dio.credit.application.system.repositories.CreditRepository
import me.dio.credit.application.system.services.impl.CreditService
import me.dio.credit.application.system.services.impl.CustomerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*


@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {

    @MockK
    private lateinit var creditRepository: CreditRepository

    @MockK
    private lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService

    private lateinit var testCredit: Credit

    private var testCustomer: Customer = Customer()

    private fun buildCredit(
        id: Long = 1L,
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(2000.0),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 10,
        status: Status = Status.IN_PROGRESS,
        customerId: Long = 1L
    ) = Credit(
        id = id,
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = Customer(id = customerId)
    )

    @BeforeEach
    fun setUp() {
        testCredit = buildCredit()
        every { creditRepository.save(any()) } returns testCredit
        every { customerService.findById(any()) } returns testCustomer
        every { creditService.findAllByCustomer(any()) } returns listOf(testCredit)
    }

    @Test
    fun `test should save credit`() {
        val actual: Credit = creditService.save(testCredit)
        Assertions.assertNotNull(actual)
    }

    @Test
    fun `test created credit should be equals testCredit`() {
        val actual: Credit = creditService.save(testCredit)
        Assertions.assertEquals(testCredit, actual)
    }

    @Test
    fun `test find all credits by customer`() {
        val expected: List<Credit> = listOf(testCredit)
        val actual: List<Credit>? = testCredit.customer?.id?.let { creditService.findAllByCustomer(it) }
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `test find credit by credit code`() {
        every { creditRepository.findByCreditCode(any()) } returns testCredit
        val expected: Credit = testCredit
        val actual: Credit? = testCredit.customer?.id?.let { creditService.findByCreditCode(it, testCredit.creditCode) }
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `test throws when id is invalid`() {
        every { creditRepository.findByCreditCode(any()) } returns testCredit
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            creditService.findByCreditCode(0, testCredit.creditCode)
        }
    }

    @Test
    fun `test assert exception message when id is invalid`() {
        every { creditRepository.findByCreditCode(any()) } returns testCredit
        val expected = "ERROR: Contact admin"
        val actual = Assertions.assertThrows(IllegalArgumentException::class.java) {
            creditService.findByCreditCode(0, testCredit.creditCode)
        }
        Assertions.assertEquals(expected, actual.message)
    }

    @Test
    fun `test throws when credit code is not found`() {
        every { creditRepository.findByCreditCode(any()) } returns null
        Assertions.assertThrows(BusinessException::class.java) {
            creditService.findByCreditCode(testCredit.customer?.id ?: 0, UUID.randomUUID())
        }
    }

    @Test
    fun `test assert exception message when credit code is invalid`() {
        val testCreditCode: UUID = UUID.randomUUID()
        val expected = "ERROR: Credit code $testCreditCode not found"
        every { creditRepository.findByCreditCode(any()) } returns null
        val actual = Assertions.assertThrows(BusinessException::class.java) {
            creditService.findByCreditCode(testCredit.customer?.id ?: 0, testCreditCode)
        }
        Assertions.assertEquals(expected, actual.message)
    }

}