package com.example.spring_practice.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("refresh_token")
data class RefreshTokenCollection(
    val userId: ObjectId,
    val hashedToken: String,
    @Indexed(expireAfter = "0s")
    val expireAt: Instant,
    val createdAt: Instant = Instant.now()
)