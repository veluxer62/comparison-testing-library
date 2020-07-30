package com.example.demo.bdd

import com.example.demo.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*
import javax.persistence.EntityNotFoundException

@SpringBootTest(classes = [SimplePersonService::class])
@DisplayName("PersonService 클래스")
class PersonServiceTest {

    @Autowired
    private lateinit var personService: PersonService

    @MockBean
    private lateinit var personRepository: PersonRepository

    @Nested
    @DisplayName("create 함수는")
    inner class DescribeOfCreate {
        @Nested
        @DisplayName("CreationPersonData가 주어진다면")
        inner class ContextWithCreationPersonData {
            private val given = CreationPersonData("test-id", "홍길동", "test@gmail.com", "01011112222", 30)

            @Test
            @DisplayName("Person을 저장한다.")
            fun `it will save person correctly`() {
                val expected = Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30)
                personService.create(given)
                verify(personRepository).save(expected)
            }
        }
    }

    @Nested
    @DisplayName("update 함수는")
    inner class DescribeOfUpdate {
        @Nested
        @DisplayName("ID와 UpdatePersonData가 주어진다면")
        inner class ContextWithIdAndUpdatePersonData {
            private val id = "test-id"
            private val given = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

            @BeforeEach
            fun setUp() {
                `when`(personRepository.findById(id))
                    .thenReturn(
                        Optional.of(Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30))
                    )
            }

            @Test
            @DisplayName("Person을 저장한다.")
            fun `it will update person correctly`() {
                val expected = Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
                personService.update(id, given)
                verify(personRepository).save(expected)
            }
        }

        @Nested
        @DisplayName("존재하지 않는 ID가 주어진다면")
        inner class ContextWithNotExistId {
            private val id = "test-id"
            private val given = UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

            @BeforeEach
            fun setUp() {
                `when`(personRepository.findById(id))
                    .thenThrow(EntityNotFoundException())
            }

            @Test
            @DisplayName("EntityNotFoundException을 반환한다.")
            fun `it will throw correctly`() {
                assertThrows(EntityNotFoundException::class.java) {
                    personService.update(id, given)
                }
            }
        }
    }

    @Nested
    @DisplayName("findById 함수는")
    inner class DescribeOfFindById {
        @Nested
        @DisplayName("존재하는 ID가 주어진다면")
        inner class ContextWithId {
            private val id = "test-id2"

            @BeforeEach
            fun setUp() {
                `when`(personRepository.findById(id))
                    .thenReturn(
                        Optional.of(Person("test-id2", "김삿갓", "foo@gmail.com", "01022221111", 40))
                    )
            }

            @Test
            @DisplayName("Person을 반환한다.")
            fun `it will save person correctly`() {
                val expected = FetchPersonData("test-id2", "김삿갓", "foo@gmail.com", "01022221111", 40)
                val actual = personService.fetchById(id)
                assertThat(actual).isEqualTo(expected)
            }
        }
    }

    @Nested
    @DisplayName("fetchByName 함수는")
    inner class DescribeOfFetchByName {
        @Nested
        @DisplayName("이름을 가진 Query가 주어진다면")
        inner class ContextWithCreationPersonData {
            private val query = FetchPersonByNameQuery("홍길동")

            @BeforeEach
            fun setUp() {
                `when`(personRepository.findByName("홍길동"))
                    .thenReturn(
                        listOf(Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))
                    )
            }

            @Test
            @DisplayName("Query와 일치하는 Person목록을 반환한다.")
            fun `it will save person correctly`() {
                val expected = listOf(
                    FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
                )
                val actual = personService.fetchByName(query)
                assertThat(actual).isEqualTo(expected)
            }
        }
    }

}