package com.example.demo.spock

import com.example.demo.Person
import com.example.demo.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import spock.lang.Specification

@DataJpaTest(showSql = true)
class PersonRepositoryTest extends Specification {

    @Autowired
    private PersonRepository personRepository

    @Autowired
    private TestEntityManager testEntityManager

    def "api will return persons correctly if given name"() {
        given:
        given.each {
            testEntityManager.persistAndFlush(it)
        }

        when:
        def actual = personRepository.findByName("홍길동")

        then:
        actual == expected

        where:
        given << [
                getPersons("홍길동", "홍길동", "홍길동", "아무개"),
                getPersons("홍길동", "홍길동", "아무개", "김삿갓")
        ]
        expected << [
                getPersons("홍길동", "홍길동", "홍길동"),
                getPersons("홍길동", "홍길동")
        ]
    }

    private static def getPersons(String... names) {
        names.toList().withIndex().collect {
            new Person(
                    it.second.toString(),
                    it.first,
                    "test@gmail.com",
                    "01011112222",
                    30
            )
        }
    }

}
