package com.example.demo.bdd

import com.example.demo.Person
import com.example.demo.PersonRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest(showSql = true)
@DisplayName("PersonRepository 클래스")
class PersonRepositoryTest {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Nested
    @DisplayName("findByName 함수는")
    inner class DescribeOfFindByName {

        @BeforeEach
        fun setUp() {
            personRepository.deleteAll()
        }

        @Nested
        @DisplayName("3개의 '홍길동'이름을 가진 Person과 1개의 '아무개'이름을 가진 Person이 저장되어 있고 '홍길동'을 매개변수로 주어진다면")
        inner class ContextWithTreeOfExpectedPersons {

            private val queryName = "홍길동"
            private val given = getPersons("홍길동", "홍길동", "홍길동", "아무개")

            @BeforeEach
            fun setUp() {
                personRepository.saveAll(given)
            }

            @Test
            @DisplayName("3명의 홍길동을 가진 Person 목록을 반환한다.")
            fun `it returns persons correctly`() {
                val expected = getPersons("홍길동", "홍길동", "홍길동")
                val actual = personRepository.findByName(queryName)
                assertThat(actual).containsAll(expected)
            }

        }

        @Nested
        @DisplayName("2개의 '홍길동'이름을 가진 Person과 2개의 '아무개'이름을 가진 Person이 저장되어 있고 '홍길동'을 매개변수로 주어진다면")
        inner class ContextWithTwoOfExpectedPersons {

            private val queryName = "홍길동"
            private val given = getPersons("홍길동", "홍길동", "아무개", "아무개")

            @BeforeEach
            fun setUp() {
                personRepository.saveAll(given)
            }

            @Test
            @DisplayName("2명의 홍길동을 가진 Person 목록을 반환한다.")
            fun `it returns persons correctly`() {
                val expected = getPersons("홍길동", "홍길동")
                val actual = personRepository.findByName(queryName)
                assertThat(actual).containsAll(expected)
            }

        }

    }

    private fun getPersons(vararg names: String) =
        names.mapIndexed { index, name ->
            Person(
                id = index.toString(),
                name = name,
                email = "test@gmail.com",
                mobile = "01011112222",
                age = 30
            )
        }
}


