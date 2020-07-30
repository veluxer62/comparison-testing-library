package com.example.demo.spock

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class TestHelper {
    fun getObjectMapper() = jacksonObjectMapper()
}