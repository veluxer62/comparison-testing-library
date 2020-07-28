package com.example.demo

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Person(
    @Id
    val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val age: Int
)