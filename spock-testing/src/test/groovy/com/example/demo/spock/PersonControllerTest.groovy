package com.example.demo.spock

import com.example.demo.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

@WebMvcTest(PersonController.class)
class PersonControllerTest extends Specification {

    @SpringBean
    private PersonService personService = Mock()

    @Autowired
    private MockMvc mvc

    private ObjectMapper mapper = new ObjectMapper().registerModule(new KotlinModule())

    def "api will return persons correctly if given name"() {
        given:
        def persons = [
                new FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
        ]

        personService.fetchByName(new FetchPersonByNameQuery("홍길동")) >> persons

        def expected = new ListItem(persons)

        when:
        def response = mvc
                .perform(get("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "홍길동"))
                .andReturn().response

        def body = mapper
                .readValue(response.contentAsString, new TypeReference<ListItem<FetchPersonData>>() {})

        then:
        response.status == HttpStatus.OK.value()
        body == expected
    }

    def "api will return persons correctly if given id"() {
        given:
        def expected = new FetchPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)

        personService.fetchById("test-id") >> expected

        when:
        def response = mvc
                .perform(get("/persons/test-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().response

        def body = mapper
                .readValue(response.contentAsString, new TypeReference<FetchPersonData>() {})

        then:
        response.status == HttpStatus.OK.value()
        body == expected
    }

    def "api will create person correctly"() {
        given:
        def expected = new CreationPersonData("test-id", "김삿갓", "foo@gmail.com", "01022221111", 40)
        def content = """
            {
                "id": "test-id",
                "name": "김삿갓",
                "email": "foo@gmail.com",
                "mobile": "01022221111",
                "age": 40
            }
        """

        when:
        def response = mvc
                .perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn().response

        then:
        response.status == HttpStatus.CREATED.value()
        1 * personService.create(expected)
    }

    def "api will update person correctly"() {
        given:
        def id = "test-id"
        def expected = new UpdatePersonData("김삿갓", "foo@gmail.com", "01022221111", 40)

        def content = """
            {
                "id": "test-id",
                "name": "김삿갓",
                "email": "foo@gmail.com",
                "mobile": "01022221111",
                "age": 40
            }
        """

        when:
        def response = mvc
                .perform(put("/persons/test-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn().response

        then:
        response.status == HttpStatus.OK.value()
        1 * personService.update(id, expected)
    }
}
