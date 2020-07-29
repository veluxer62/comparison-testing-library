package com.example.demo.spock

import com.example.demo.*
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.persistence.EntityNotFoundException

@SpringBootTest(classes = [SimplePersonService.class])
class PersonServiceTest extends Specification {

    @SpringBean
    private PersonRepository personRepository = Mock()

    @Autowired
    private PersonService personService

    def "create will create person correctly"() {
        given:
        def data = new CreationPersonData("test-id", "홍길동", "test@gmail.com", "01011112222", 30)
        def expected = new Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30)

        when:
        personService.create(data)

        then:
        1 * personRepository.save(expected)
    }

    def "update will update person correctly"() {
        given:
        def id = "test-id"
        def data = new UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

        personRepository.findById(id) >>
                Optional.of(new Person("test-id", "홍길동", "test@gmail.com", "01011112222", 30))

        def expected = new Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

        when:
        personService.update(id, data)

        then:
        1 * personRepository.save(expected)
    }

    def "update will throw EntityNotFoundException if person is not exists"() {
        given:
        def id = "test-id"
        def data = new UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

        personRepository.findById(id) >> {
            throw new EntityNotFoundException()
        }

        when:
        personService.update(id, data)

        then:
        thrown(EntityNotFoundException)
    }

    def "fetchById will return person correctly"() {
        given:
        def id = "test-id"

        personRepository.findById(id) >>
                Optional.of(new Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40))

        def expected = new FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

        when:
        def actual = personService.fetchById(id)

        then:
        actual == expected
    }

    def "fetchByName will return list correctly"() {
        given:
        personRepository.findByName("홍길동") >>
                [new Person("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)]

        def expected = [
                new FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
        ]

        when:
        def actual = personService.fetchByName(new FetchPersonByNameQuery("홍길동"))

        then:
        actual == expected
    }

}
