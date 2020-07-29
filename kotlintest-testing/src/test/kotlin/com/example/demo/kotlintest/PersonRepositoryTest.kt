package com.example.demo.kotlintest

import com.example.demo.Person
import com.example.demo.PersonRepository
import io.kotlintest.IsolationMode
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.specs.StringSpec
import io.kotlintest.spring.SpringListener
import io.kotlintest.tables.row
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class PersonRepositoryTest : StringSpec() {

    override fun listeners() = listOf(SpringListener)

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    init {
        "findByName will return persons correctly" {
            forall(
                row(getPersons("홍길동", "홍길동", "홍길동", "아무개"),
                    getPersons("홍길동", "홍길동", "홍길동")),
                row(getPersons("홍길동", "홍길동", "아무개", "아무개"),
                    getPersons("홍길동", "홍길동"))
            ) { given, expected ->
                // given
                personRepository.deleteAll()

                given.forEach {
                    testEntityManager.persistAndFlush(it)
                }

                // when
                val actual = personRepository.findByName("홍길동")

                // then
                actual shouldContainAll expected
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