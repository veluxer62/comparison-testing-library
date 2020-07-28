package com.example.demo

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/persons", produces = ["application/json; charset=UTF-8"])
class PersonController(private val personService: PersonService) {

    @GetMapping
    fun fetchByName(@RequestParam("name") name: String): ListItem<FetchPersonData> {
        val persons = personService.fetchByName(FetchPersonByNameQuery(name))
        return ListItem(persons)
    }

    @GetMapping("/{id}")
    fun fetchById(@PathVariable id: String): FetchPersonData {
        return personService.fetchById(id)
    }

    @PostMapping
    fun create(@RequestBody data: CreationPersonData): ResponseEntity<Unit> {
        personService.create(data)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/{id}")
    fun create(@PathVariable id: String, @RequestBody data: UpdatePersonData): ResponseEntity<Unit> {
        personService.update(id, data)
        return ResponseEntity.ok().build()
    }

}