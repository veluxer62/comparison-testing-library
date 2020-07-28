package com.example.demo.kotlintest

import com.example.demo.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.kotlintest.spring.SpringListener
import org.mockito.Mockito
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

            val data = CreationPersonData(
                id = "test-id",
                name = "홍길동",
                email = "test@gmail.com",
                mobile = "01011112222",
                age = 30
            )

            val expected = Person(
                id = "test-id",
                name = "홍길동",
                email = "test@gmail.com",
                mobile = "01011112222",
                age = 30
            )

            personService.create(data)

            Mockito.verify(personRepository).save(expected)

        }

        "update will update person correctly" {

            val id = "test-id"
            val data = UpdatePersonData(
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )

            Mockito.`when`(personRepository.findById(id))
                .thenReturn(
                    Optional.of(Person(
                    id = "test-id",
                    name = "홍길동",
                    email = "test@gmail.com",
                    mobile = "01011112222",
                    age = 30
                )))

            val expected = Person(
                id = "test-id",
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )

            personService.update(id, data)

            Mockito.verify(personRepository).save(expected)

        }

        "update will throw EntityNotFoundException if person is not exists" {
            val id = "test-id"
            val data = UpdatePersonData(
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )

            Mockito.`when`(personRepository.findById(id))
                .thenThrow(EntityNotFoundException())

            shouldThrow<EntityNotFoundException> {
                personService.update(id, data)
            }
        }

        "fetchById will return person correctly" {

            val id = "test-id"

            Mockito.`when`(personRepository.findById(id))
                .thenReturn(Optional.of(Person(
                    id = "test-id",
                    name = "김삿갓",
                    email = "foo@gmail.com",
                    mobile = "01022221111",
                    age = 40
                )))

            val expected = FetchPersonData(
                id = "test-id",
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            )

            val actual = personService.fetchById(id)

            actual shouldBe expected

        }

        "fetchByName will return list correctly" {

            Mockito.`when`(personRepository.findByName("홍길동"))
                .thenReturn(listOf(Person(
                    id = "test-id",
                    name = "김삿갓",
                    email = "foo@gmail.com",
                    mobile = "01022221111",
                    age = 40
                )))

            val expected = listOf(FetchPersonData(
                id = "test-id",
                name = "김삿갓",
                email = "foo@gmail.com",
                mobile = "01022221111",
                age = 40
            ))

            val query = FetchPersonByNameQuery("홍길동")

            val actual = personService.fetchByName(query)

            actual shouldBe expected

        }

    }
}