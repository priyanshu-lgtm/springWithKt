package com.example.spring_practice

import com.example.spring_practice.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepo : MongoRepository<Note,ObjectId>{

    fun findByOwnerId(ownerId : ObjectId) : List<Note>

}