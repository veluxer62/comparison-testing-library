package com.example.demo

import org.springframework.stereotype.Service

@Service
class SimplePersonService(private val personRepository: PersonRepository) : PersonService {
    override fun create(data: CreationPersonData) {
        personRepository.save(
            Person(
                id = data.id,
                name = data.name,
                email = data.email,
                mobile = data.mobile,
                age = data.age
            )
        )
    }

    override fun update(id: String, data: UpdatePersonData) {
        personRepository.findById(id).ifPresent {
            val entity = it.copy(
                name = data.name,
                email = data.email,
                mobile = data.mobile,
                age = data.age
            )
            personRepository.save(entity)
        }
    }

    override fun fetchById(id: String): FetchPersonData {
        return personRepository.findById(id)
            .map {
                FetchPersonData(
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    mobile = it.mobile,
                    age = it.age
                )
            }
            .orElseThrow()
    }

    override fun fetchByName(query: FetchPersonByNameQuery): List<FetchPersonData> {
        return personRepository.findByName(query.name)
            .map { FetchPersonData(
                id = it.id,
                name = it.name,
                email = it.email,
                mobile = it.mobile,
                age = it.age
            ) }
    }
}