package com.example.demo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*
import javax.persistence.EntityNotFoundException

@SpringBootTest(classes = [SimplePersonService::class])
class PersonServiceTest {

    @Autowired
    private lateinit var personService: PersonService

    @MockBean
    private lateinit var personRepository: PersonRepository

    @Test
    fun `create will create person correctly`() {
        // given
        val given = CreationPersonData("test-id", "홍길동", "test@gmail.com", "01011112222", 30)
        val expected = Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30)

        // when
        personService.create(given)

        // then
        verify(personRepository).save(expected)
    }

    @Test
    fun `update will update person correctly`() {
        // given
        val id = "test-id"
        val given = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

        `when`(personRepository.findById(id))
            .thenReturn(
                Optional.of(Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30))
            )

        val expected = Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

        // when
        personService.update(id, given)

        // then
        verify(personRepository).save(expected)
    }

    @Test
    fun `update will throw EntityNotFoundException if person is not exists`() {
        // given
        val id = "test-id"
        val given = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

        // when
        `when`(personRepository.findById(id))
            .thenThrow(EntityNotFoundException())

        // then
        assertThrows(EntityNotFoundException::class.java) {
            personService.update(id, given)
        }
    }

    @Test
    fun `fethById will return person correctly`() {
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
        assertEquals(expected, actual)
    }

    @Test
    fun `fetchByName will return list correctly`() {
        // given
        `when`(personRepository.findByName("홍길동"))
            .thenReturn(
                listOf(Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
            )

        val expected = listOf(
            FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
        )

        val query = FetchPersonByNameQuery("홍길동")

        // when
        val actual = personService.fetchByName(query)

        // then
        assertEquals(expected, actual)
    }

}