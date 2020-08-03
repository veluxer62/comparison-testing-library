package com.example.demo.kotlintest.bdd

import com.example.demo.Person
import com.example.demo.PersonRepository
import io.kotlintest.IsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.spring.SpringListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class PersonRepositoryTest : WordSpec() {

    override fun listeners() = listOf(SpringListener)
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    @Autowired
    private lateinit var personRepository: PersonRepository

    init {
        "findByName 함수는" `when` {
            "3개의 '홍길동'이름을 가진 Person과 1개의 '아무개'이름을 가진 Person이 저장되어 있고 '홍길동'을 매개변수로 주어진다면" should {
                val queryName = "홍길동"
                val given = getPersons("홍길동", "홍길동", "홍길동", "아무개")

                personRepository.saveAll(given)

                "3명의 홍길동을 가진 Person 목록을 반환한다." {
                    val expected = getPersons("홍길동", "홍길동", "홍길동")
                    val actual = personRepository.findByName(queryName)
                    actual shouldBe expected
                }
            }

            "2개의 '홍길동'이름을 가진 Person과 2개의 '아무개'이름을 가진 Person이 저장되어 있고 '홍길동'을 매개변수로 주어진다면" should {
                val queryName = "홍길동"
                val given = getPersons("홍길동", "홍길동", "아무개", "아무개")

                personRepository.saveAll(given)

                "2명의 홍길동을 가진 Person 목록을 반환한다." {
                    val expected = getPersons("홍길동", "홍길동")
                    val actual = personRepository.findByName(queryName)

                    actual shouldBe expected
                }
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