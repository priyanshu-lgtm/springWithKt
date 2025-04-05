package com.example.spring_practice.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    val email : String,
    val hashedPassword: String,
    @Id val id: ObjectId = ObjectId()

)
