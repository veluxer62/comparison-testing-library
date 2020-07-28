package com.example.demo.kotlintest

import com.example.demo.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.spring.SpringListener
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(PersonController::class)
class PersonControllerTest : StringSpec() {

    override fun listeners() = listOf(SpringListener)

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean(relaxed = true)
    private lateinit var personService: PersonService

    init {

        "api will return persons correctly if given name" {

            val persons = listOf(
                FetchPersonData(
                    id = "test-id",
                    name = "김삿갓",
                    email = "foo@gmail.com",
                    mobile = "01022221111",
                    age = 40
                )
            )

            every { personService.fetchByName(FetchPersonByNameQuery("홍길동")) } returns persons

            val expected = ListItem(persons)

            mvc.perform(
                MockMvcRequestBuilders.get("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", "홍길동"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect {
                    val actual = jacksonObjectMapper()
                        .readValue(it.response.contentAsString, jacksonTypeRef<ListItem<FetchPersonData>>())

                    actual shouldBe expected
                }

        }

        "api will return persons correctly if given id" {

            val expected = FetchPersonData(
                id = "test-id",
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )

            every { personService.fetchById("test-id") } returns expected

            mvc.perform(
                MockMvcRequestBuilders.get("/persons/test-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect {
                    val actual = jacksonObjectMapper()
                        .readValue(it.response.contentAsString, jacksonTypeRef<FetchPersonData>())

                    actual shouldBe expected
                }

        }

        "api will create person correctly" {

            val expected = CreationPersonData(
                id = "test-id",
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )

            val content = """
                {
                    "id": "test-id",
                    "name": "김삿갓",
                    "email": "foo@gmail.com",
                    "mobile": "01022221111",
                    "age": 40
                }
            """.trimIndent()

            mvc.perform(
                MockMvcRequestBuilders.post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isCreated)

            verify { personService.create(expected) }

        }

        "api will update person correctly" {

            val id = "test-id"
            val expected = UpdatePersonData(
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )

            val content = """
            {
                "name": "김삿갓",
                "email": "foo@gmail.com",
                "mobile": "01022221111",
                "age": 40
            }
        """.trimIndent()

            mvc.perform(
                MockMvcRequestBuilders.put("/persons/test-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk)

            verify { personService.update(id, expected) }

        }

    }

}