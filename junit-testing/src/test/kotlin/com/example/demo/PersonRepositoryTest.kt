package com.example.demo

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import java.util.stream.Stream

@ExtendWith(SpringExtension::class)
@DataJpaTest(showSql = true)
class PersonRepositoryTest {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @ParameterizedTest
    @ArgumentsSource(FindByNameTestArguments::class)
    fun `findByName will return persons correctly`(
        given: List<Person>, expected: List<Person>
    ) {
        given.forEach {
            testEntityManager.persistAndFlush(it)
        }

        val actual = personRepository.findByName("홍길동")

        Assertions.assertThat(actual).containsAll(expected)
    }
}

class FindByNameTestArguments : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<Arguments> =
        Stream.of(
            Arguments {
                val given = listOf(
                    getPerson("홍길동"),
                    getPerson("홍길동"),
                    getPerson("홍길동"),
                    getPerson("아무개")
                )

                val expected = given.filter { it.name == "홍길동" }

                listOf(given, expected).toTypedArray()
            },

            Arguments {
                val given = listOf(
                    getPerson("홍길동"),
                    getPerson("홍길동"),
                    getPerson("아무개"),
                    getPerson("김삿갓")
                )

                val expected = given.filter { it.name == "홍길동" }

                listOf(given, expected).toTypedArray()
            }
        )

    private fun getPerson(name: String) =
        Person(
            id = UUID.randomUUID().toString(),
            name = name,
            email = "test@gmail.com",
            mobile = "01011112222",
            age = 30
        )
}

