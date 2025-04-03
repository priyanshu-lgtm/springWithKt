package com.example.spring_practice.repositories

import com.example.spring_practice.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepo : MongoRepository<User,ObjectId> {

    fun findByEmail(email : String) : User?
}