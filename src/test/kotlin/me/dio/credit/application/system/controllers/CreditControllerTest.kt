package me.dio.credit.application.system.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.dto.CreditViewList
import me.dio.credit.application.system.dto.CustomerDto
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.enummerations.Status
import me.dio.credit.application.system.repositories.CreditRepository
import me.dio.credit.application.system.repositories.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Random
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    private fun buildCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(2000.0),
        dayFirstInstallment: LocalDate = LocalDate.now().plusDays(60),
        numberOfInstallments: Int = 10,
        customerId: Long = 1L
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

    private fun buildCustomerDto(
        firstName: String = "Test",
        lastName: String = "LastName",
        cpf: String = "65717209061",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        email: String = "test@email.com",
        password: String = "123456",
        zipCode: String = "321654987",
        street: String = "Some street",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        income = income,
        email = email,
        password = password,
        zipCode = zipCode,
        street = street
    )

    private fun buildCreditViewList(
        creditCode: UUID,
        creditValue: BigDecimal,
        numberOfInstallments: Int,
    ) = CreditViewList(
        creditCode = creditCode,
        creditValue = creditValue,
        numberOfInstallments = numberOfInstallments,
    )


    @BeforeEach
    fun setUp() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun `should save credit`() {
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit: CreditDto = buildCreditDto(customerId = customer.id!!)
        val valueAsString: String = objectMapper.writeValueAsString(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value("2000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value("10"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(customer.income))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.IN_PROGRESS.toString()))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit when id is invalid`() {
        val invalidId: Long = Random().nextLong()
        val credit: CreditDto = buildCreditDto(customerId = invalidId)
        val valueAsString: String = objectMapper.writeValueAsString(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title")
                    .value("Bad Request! Consult the documentation!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exceptions.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit when day of first installment is in the past`() {
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit: CreditDto = buildCreditDto(
            customerId = customer.id!!,
            dayFirstInstallment = LocalDate.now().minusYears(2)
        )
        val valueAsString: String = objectMapper.writeValueAsString(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title")
                    .value("Bad Request! Consult the documentation!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details.dayFirstInstallment")
                    .value("must be a future date")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit when day of first installment is after of max date`() {
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit: CreditDto = buildCreditDto(
            customerId = customer.id!!,
            dayFirstInstallment = LocalDate.now().plusYears(2)
        )
        val valueAsString: String = objectMapper.writeValueAsString(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title")
                    .value("Bad Request! Consult the documentation!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exceptions.BusinessException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details[*]")
                    .value("ERROR: Date of the first installment is invalid.")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @ParameterizedTest
    @CsvSource(value = ["2, must be greater than or equal to 3", "50, must be less than or equal to 48"])
    fun `should not save a credit when number of installments is out of range`(
        installments: Int,
        errorMessage: String
    ) {
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit: CreditDto = buildCreditDto(
            customerId = customer.id!!,
            numberOfInstallments = installments
        )
        val valueAsString: String = objectMapper.writeValueAsString(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title")
                    .value("Bad Request! Consult the documentation!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details.numberOfInstallments").value(errorMessage)
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find credit by credit code`() {
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit: Credit = creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)

        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value("2000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value("10"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(customer.income))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.IN_PROGRESS.toString()))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credit by credit code when credit code is invalid `() {
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val invalidCreditCode: UUID = UUID.randomUUID()

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidCreditCode?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)

        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title")
                    .value("Bad Request! Consult the documentation!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exceptions.BusinessException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details[*]")
                    .value("ERROR: Credit code $invalidCreditCode not found")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credit by credit code when id is invalid`() {
        val invalidId: Long = Random().nextLong()
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit: Credit = creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=$invalidId")
                .contentType(MediaType.APPLICATION_JSON)

        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title")
                    .value("Bad Request! Consult the documentation!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class java.lang.IllegalArgumentException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details[*]")
                    .value("ERROR: Contact admin")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find all credits by customer id`() {
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())
        creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk).andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find any credits by customer id when no customer id is found`() {
        val invalidId = Random().nextLong()

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=$invalidId")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("[]"))
            .andDo(MockMvcResultHandlers.print())
    }


}