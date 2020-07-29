package com.example.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.stream.Stream

@DataJpaTest(showSql = true)
class PersonRepositoryTest {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @ParameterizedTest
    @ArgumentsSource(FindByNameTestArguments::class)
    fun `findByName will return persons correctly`(given: List<Person>, expected: List<Person>) {
        // given
        given.forEach {
            testEntityManager.persistAndFlush(it)
        }

        // when
        val actual = personRepository.findByName("홍길동")

        // then
        assertThat(actual).containsAll(expected)
    }
}

class FindByNameTestArguments : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<Arguments> =
        Stream.of(
            Arguments.of(
                getPersons("홍길동", "홍길동", "홍길동", "아무개"),
                getPersons("홍길동", "홍길동", "홍길동")
            ),
            Arguments.of(
                getPersons("홍길동", "홍길동", "아무개", "아무개"),
                getPersons("홍길동", "홍길동")
            )
        )

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

