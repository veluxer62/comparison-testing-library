package com.example.demo.kotlintest

import com.example.demo.Person
import com.example.demo.PersonRepository
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.specs.StringSpec
import io.kotlintest.spring.SpringListener
import io.kotlintest.tables.Row2
import io.kotlintest.tables.forAll
import io.kotlintest.tables.row
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.*

@DataJpaTest
class PersonRepositoryTest : StringSpec() {

    override fun listeners() = listOf(SpringListener)

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    init {
        "findByName will return persons correctly" {

            val case1Given = listOf(
                getPerson("홍길동"),
                getPerson("홍길동"),
                getPerson("홍길동"),
                getPerson("아무개")
            )
            val case1Expected = case1Given.filter { it.name == "홍길동" }
            val rows = row(case1Given, case1Expected)

            val case2Given = listOf(
                getPerson("홍길동"),
                getPerson("홍길동"),
                getPerson("아무개"),
                getPerson("김삿갓")
            )

            val case2Expected = case2Given.filter { it.name == "홍길동" }


            val rows1 = row(case2Given, case2Expected)
            forall(
                rows,
                rows1
            ) { given, expected ->
                given.forEach {
                    testEntityManager.persistAndFlush(it)
                }

                val actual = personRepository.findByName("홍길동")

                actual shouldContainAll expected
            }
        }

    }

    private fun getPerson(name: String) =
        Person(
            id = UUID.randomUUID().toString(),
            name = name,
            email = "test@gmail.com",
            mobile = "01011112222",
            age = 30
        )

}