package com.example.demo.kotlintest.bdd

import com.example.demo.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.spring.SpringListener
import io.mockk.every
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PersonController::class)
class PersonControllerTest : WordSpec() {

    override fun listeners() = listOf(SpringListener)

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean(relaxed = true)
    private lateinit var personService: PersonService

    init {

        "/persons 는" `when` {

            val uri = "/persons"

            "GET 요청에 name 쿼리 파라미터가 주어진다면" should {

                val requestBuilder = MockMvcRequestBuilders.get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("name", "홍길동")

                every {
                    personService.fetchByName(FetchPersonByNameQuery("홍길동"))
                } returns listOf(
                    FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
                )

                "OK status와 ListItem body를 반환한다." {
                    val expected = ListItem(
                        listOf(FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
                    )

                    mvc.perform(requestBuilder)
                        .andExpect(status().isOk)
                        .andExpect {
                            val actual = jacksonObjectMapper()
                                .readValue(it.response.contentAsString, jacksonTypeRef<ListItem<FetchPersonData>>())

                            actual shouldBe expected
                        }
                }

            }

            "POST 요청에 Content 주어진다면" should {
                val requestBuilder = MockMvcRequestBuilders.post(uri)
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

                "Created status를 반환하고 PersonService.create함수를 호출한다." {
                    val expected = CreationPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

                    mvc.perform(requestBuilder)
                        .andExpect(status().isCreated)

                    verify { personService.create(expected) }
                }
            }

        }

        "/persons/{id} 는" `when` {

            val uri = "/persons/"

            "GET 요청에 id 경로변수가 주어진다면" should {

                val id = "test-id"
                val requestBuilder = MockMvcRequestBuilders.get(uri + id)
                    .contentType(MediaType.APPLICATION_JSON)

                every {
                    personService.fetchById(id)
                } returns FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

                "OK status와 FetchPersonData body를 반환한다." {
                    val expected = FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

                    mvc.perform(requestBuilder)
                        .andExpect(status().isOk)
                        .andExpect {
                            val actual = jacksonObjectMapper()
                                .readValue(it.response.contentAsString, jacksonTypeRef<FetchPersonData>())

                            actual shouldBe expected
                        }
                }

            }

            "PUT 요청에 id 경로변수와 Content가 주어진다면" should {

                val id = "test-id"
                val requestBuilder = MockMvcRequestBuilders.put(uri + id)
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

                "ok status를 반환하고 PersonService.update함수를 호출한다." {
                    val expected = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

                    mvc.perform(requestBuilder)
                        .andExpect(status().isOk)
                    
                    verify { personService.update(id, expected) }
                }
            }

        }

    }

}