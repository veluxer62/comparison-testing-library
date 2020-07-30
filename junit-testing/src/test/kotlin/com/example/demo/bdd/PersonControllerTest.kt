package com.example.demo.bdd

import com.example.demo.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PersonController::class)
@DisplayName("Person API")
class PersonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var personService: PersonService

    @Nested
    @DisplayName("/persons 는")
    inner class DescribeOfPersons {

        private val uri = "/persons"


        @Nested
        @DisplayName("GET 요청에 name 쿼리 파라미터가 주어진다면")
        inner class ContextWithGetAndNameQuery {
            private val requestBuilder = get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", "홍길동")

            @BeforeEach
            fun setUp() {
                `when`(personService.fetchByName(FetchPersonByNameQuery("홍길동")))
                    .thenReturn(
                        listOf(FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
                    )
            }

            @Test
            @DisplayName("OK status와 ListItem body를 반환한다.")
            fun `it will return ok status and listItem`() {
                val expected = ListItem(
                    listOf(FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
                )

                mvc.perform(requestBuilder)
                    .andExpect(status().isOk)
                    .andExpect {
                        val actual = jacksonObjectMapper()
                            .readValue(it.response.contentAsString, jacksonTypeRef<ListItem<FetchPersonData>>())

                        assertThat(actual).isEqualTo(expected)
                    }
            }
        }

        @Nested
        @DisplayName("POST 요청에 Content 주어진다면")
        inner class ContextWithPostAndContent {

            private val requestBuilder = post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "id": "test-id",
                            "name": "김삿갓",
                            "email": "foo@gmail.com",
                            "mobile": "01022221111",
                            "age": 40
                        }
                    """.trimIndent()
                )

            @Test
            @DisplayName("Created status를 반환하고 PersonService.create함수를 호출한다.")
            fun `it will return ok status and listItem`() {
                val expected = CreationPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

                mvc.perform(requestBuilder)
                    .andExpect(status().isCreated)

                verify(personService).create(expected)
            }
        }
    }

    @Nested
    @DisplayName("/persons/{id} 는")
    inner class DescribeOfPersonsWithId {

        private val uri = "/persons/"

        @Nested
        @DisplayName("GET 요청에 id 경로변수가 주어진다면")
        inner class ContextWithGetAndId {
            private val id = "test-id"

            private val requestBuilder = get(uri + id)
                .contentType(MediaType.APPLICATION_JSON)

            @BeforeEach
            fun setUp() {
                `when`(personService.fetchById(id))
                    .thenReturn(
                        FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
                    )
            }

            @Test
            @DisplayName("OK status와 FetchPersonData body를 반환한다.")
            fun `it will return ok status and listItem`() {
                val expected = FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

                mvc.perform(requestBuilder)
                    .andExpect(status().isOk)
                    .andExpect {
                        val actual = jacksonObjectMapper()
                            .readValue(it.response.contentAsString, jacksonTypeRef<FetchPersonData>())

                        assertThat(actual).isEqualTo(expected)
                    }
            }
        }

        @Nested
        @DisplayName("PUT 요청에 id 경로변수와 Content가 주어진다면")
        inner class ContextWithPutAndIdAndContent {
            private val id = "test-id"
            private val requestBuilder = put(uri + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "id": "test-id",
                            "name": "김삿갓",
                            "email": "foo@gmail.com",
                            "mobile": "01022221111",
                            "age": 40
                        }
                    """.trimIndent()
                )

            @Test
            @DisplayName("ok status를 반환하고 PersonService.update함수를 호출한다.")
            fun `it will return ok status and listItem`() {
                val expected = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

                mvc.perform(requestBuilder)
                    .andExpect(status().isOk)

                verify(personService).update(id, expected)
            }
        }

    }

}