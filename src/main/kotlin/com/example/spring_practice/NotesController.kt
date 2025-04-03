package com.example.spring_practice

import com.example.spring_practice.NotesController.NoteResponse
import com.example.spring_practice.model.Note
import com.example.spring_practice.repositories.NoteRepo
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
        val response =  noteRepo.save(
            Note(
                id = body.id?.let { ObjectId(it)} ?: ObjectId.get(),
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Instant.now(),
                ownerId = ObjectId()
//                ownerId = ObjectId(body.ownerId)
            )
        )
        return response.toResponse()
    }

    @GetMapping
    fun findByOwnerId(
        @RequestParam(required = true) ownerId: String
    ) : List<NoteResponse>{
        return noteRepo.findByOwnerId(ownerId = ObjectId(ownerId)).map {
            it.toResponse()
        }

    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String?){
        val response = noteRepo.deleteById(ObjectId(id))
    }
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