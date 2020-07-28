package com.example.demo.junit

import com.example.demo.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PersonController::class)
class PersonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var personService: PersonService

    @Test
    fun `api will return persons correctly if given name`() {

        val persons = listOf(
            FetchPersonData(
                id = "test-id",
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )
        )
        Mockito.`when`(personService.fetchByName(FetchPersonByNameQuery("홍길동")))
            .thenReturn(persons)

        val expected = ListItem(persons)

        mvc.perform(get("/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", "홍길동"))
            .andExpect(status().isOk)
            .andExpect {
                val actual = jacksonObjectMapper()
                    .readValue(it.response.contentAsString, jacksonTypeRef<ListItem<FetchPersonData>>())

                Assertions.assertThat(actual).isEqualTo(expected)
            }

    }

    @Test
    fun `api will return persons correctly if given id`() {

        val expected = FetchPersonData(
            id = "test-id",
            name = "김삿갓",
            email = "foo@gmail.com",
            mobile = "01022221111",
            age = 40
        )
        Mockito.`when`(personService.fetchById("test-id"))
            .thenReturn(expected)

        mvc.perform(get("/persons/test-id")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect {
                val actual = jacksonObjectMapper()
                    .readValue(it.response.contentAsString, jacksonTypeRef<FetchPersonData>())

                Assertions.assertThat(actual).isEqualTo(expected)
            }

    }

    @Test
    fun `api will create person correctly`() {

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

        mvc.perform(post("/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated)

        Mockito.verify(personService).create(expected)
    }

    @Test
    fun `api will update person correctly`() {

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

        mvc.perform(put("/persons/test-id")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk)

        Mockito.verify(personService).update(id, expected)
    }

}