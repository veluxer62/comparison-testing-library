package com.example.demo

import org.springframework.data.repository.CrudRepository

interface PersonRepository : CrudRepository<Person, String> {
    fun findByName(name: String): List<Person>
}