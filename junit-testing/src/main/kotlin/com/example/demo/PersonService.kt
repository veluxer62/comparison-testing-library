package com.example.demo

interface PersonService {
    fun create(data: CreationPersonData)
    fun update(id: String, data: UpdatePersonData)
    fun fetchById(id: String): FetchPersonData
    fun fetchByName(query: FetchPersonByNameQuery): List<FetchPersonData>
}