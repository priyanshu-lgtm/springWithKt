package com.example.spring_practice.repositories

import com.example.spring_practice.model.RefreshTokenCollection
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepo : MongoRepository<RefreshTokenCollection,ObjectId> {

    fun findByUserIdAndHashedToken(userId: ObjectId,hashedToken : String) : RefreshTokenCollection?
    fun deleteByUserIdAndHashedToken(userId: ObjectId,hashedToken : String)

}