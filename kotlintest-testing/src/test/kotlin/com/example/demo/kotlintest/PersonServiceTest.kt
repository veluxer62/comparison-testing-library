package com.example.demo.kotlintest

import com.example.demo.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.kotlintest.spring.SpringListener
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*
import javax.persistence.EntityNotFoundException

@SpringBootTest(classes = [SimplePersonService::class])
class PersonServiceTest : StringSpec() {

    override fun listeners() = listOf(SpringListener)

    @MockBean
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var personService: PersonService

    init {

        "create will create person correctly" {
            // given
            val data = CreationPersonData("test-id", "홍길동", "test@gmail.com", "01011112222", 30)
            val expected = Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30)

            // when
            personService.create(data)

            // then
            verify(personRepository).save(expected)
        }

        "update will update person correctly" {
            // given
            val id = "test-id"
            val data = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

            `when`(personRepository.findById(id))
                .thenReturn(
                    Optional.of(Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30))
                )

            val expected = Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

            // when
            personService.update(id, data)

            // then
            verify(personRepository).save(expected)
        }

        "update will throw EntityNotFoundException if person is not exists" {
            // given
            val id = "test-id"
            val data = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

            `when`(personRepository.findById(id))
                .thenThrow(EntityNotFoundException())

            // when && then
            shouldThrow<EntityNotFoundException> {
                personService.update(id, data)
            }
        }

        "fetchById will return person correctly" {
            // given
            val id = "test-id"

            `when`(personRepository.findById(id))
                .thenReturn(
                    Optional.of(Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
                )

            val expected = FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

            // when
            val actual = personService.fetchById(id)

            // then
            actual shouldBe expected
        }

        "fetchByName will return list correctly" {
            // given
            `when`(personRepository.findByName("홍길동"))
                .thenReturn(
                    listOf(Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
                )

            val expected = listOf(
                FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
            )

            // when
            val actual = personService.fetchByName(FetchPersonByNameQuery("홍길동"))

            // then
            actual shouldBe expected
        }

    }
}