package com.example.spring_practice.controller

import com.example.spring_practice.model.Note
import com.example.spring_practice.repositories.NoteRepo
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.Instant


// post http://localhost:8085/notes
//get http://localhost:8085/notes?ownerId
//delete http://localhost:8085/notes/123


@RestController
@RequestMapping("/notes")
class NotesController(private val noteRepo: NoteRepo) {

    data class NoteRequest(
        val id : String?,
        val title : String,
        val color : Long,
        val content : String,
//        val ownerId : String
    )

    data class CommonResponse(
        val responseCode : Int,
        val message : String
    )

    data class NoteResponse(
        val id : String?,
        val title : String,
        val color : Long,
        val content : String,
        val createdAt : Instant
    )


    @PostMapping
    fun save(
        @RequestBody body : NoteRequest):NoteResponse{
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val response =  noteRepo.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Instant.now(),
                ownerId = ObjectId(ownerId)
//                ownerId = ObjectId(body.ownerId)
            )
        )
        return response.toResponse()
    }

    @GetMapping
    fun findByOwnerId(
    ) : List<NoteResponse>{
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return noteRepo.findByOwnerId(ownerId = ObjectId(ownerId)).map {
            it.toResponse()
        }

    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String?){
        val note= noteRepo.findById(ObjectId(id)).orElseThrow{
            IllegalArgumentException("wrong id")
        } // id is being used to get the ownerid from db

        val ownerId = SecurityContextHolder.getContext().authentication.principal as String // ownweid from the system when user was created

        if(note.ownerId.toHexString() == ownerId)
        noteRepo.deleteById(ObjectId(id))
    }

    private fun Note.toResponse(): NotesController.NoteResponse{
        return NoteResponse(
            id = id.toHexString(),
            title = title,
            color = color,
            content = content,
            createdAt = createdAt
        )
    }

}