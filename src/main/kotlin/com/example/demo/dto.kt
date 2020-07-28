package com.example.demo

data class CreationPersonData(
    val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val age: Int
)

data class UpdatePersonData(
    val name: String,
    val email: String,
    val mobile: String,
    val age: Int
)

data class FetchPersonData(
    val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val age: Int
)

data class FetchPersonByNameQuery(
    val name: String
)

data class ListItem<T>(
    val items: List<T>
)