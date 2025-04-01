package com.example.spring_practice

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringPracticeApplication

fun main(args: Array<String>) {
	runApplication<SpringPracticeApplication>(*args)
}
