package com.example.demo.kotlintest.bdd

import com.example.demo.*
import io.kotlintest.DisplayName
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.kotlintest.spring.SpringListener
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*
import javax.persistence.EntityNotFoundException

@SpringBootTest(classes = [SimplePersonService::class])
@DisplayName("PersonRepository 클래스")
class PersonServiceTest : WordSpec() {

    override fun listeners() = listOf(SpringListener)

    @MockBean
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var personService: PersonService

    init {

        "create 함수는" `when` {
            "CreationPersonData가 주어진다면" should {
                val given = CreationPersonData("test-id", "홍길동", "test@gmail.com", "01011112222", 30)

                "Person을 저장한다." {
                    val expected = Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30)
                    personService.create(given)
                    verify(personRepository).save(expected)
                }
            }
        }

        "update 함수는" `when` {
            "ID와 UpdatePersonData가 주어진다면" should {
                val id = "test-id"
                val given = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

                `when`(personRepository.findById(id))
                    .thenReturn(
                        Optional.of(Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30))
                    )

                "Person을 저장한다." {
                    val expected = Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
                    personService.update(id, given)

                    verify(personRepository).save(expected)
                }
            }

            "존재하지 않는 ID가 주어진다면" should {
                val id = "test-id"
                val given = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

                `when`(personRepository.findById(id))
                    .thenThrow(EntityNotFoundException())

                "EntityNotFoundException을 반환한다." {
                    shouldThrow<EntityNotFoundException> {
                        personService.update(id, given)
                    }
                }
            }
        }

        "findById 함수는" `when` {
            "존재하는 ID가 주어진다면" should {
                val id = "test-id"

                `when`(personRepository.findById(id))
                    .thenReturn(
                        Optional.of(Person("test-id2", "김삿갓", "foo@gmail.com", "01022221111", 40))
                    )

                "Person을 반환한다." {
                    val expected = FetchPersonData("test-id2", "김삿갓", "foo@gmail.com", "01022221111", 40)
                    val actual = personService.fetchById(id)

                    actual shouldBe expected
                }
            }
        }

        "fetchByName 함수는" `when` {
            "이름을 가진 Query가 주어진다면" should {
                val query = FetchPersonByNameQuery("홍길동")

                `when`(personRepository.findByName("홍길동"))
                    .thenReturn(
                        listOf(Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
                    )

                "Query와 일치하는 Person목록을 반환한다." {
                    val expected = listOf(
                        FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
                    )
                    val actual = personService.fetchByName(query)

                    actual shouldBe expected
                }
            }
        }

    }
}