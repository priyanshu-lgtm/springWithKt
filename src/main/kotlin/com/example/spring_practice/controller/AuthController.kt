package com.example.spring_practice.controller

import com.example.spring_practice.model.TokenPair
import com.example.spring_practice.sequrity.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController (private val authService: AuthService){

    data class AuthRequest(
        val email : String,
        val password: String
    )

    data class RefreshRequest(
       val refreshToken : String
    )

    @PostMapping("/register")
    fun register(
        @RequestBody body : AuthRequest
    ){
        authService.register(body.email,body.password)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body : AuthRequest
    ): TokenPair{
        return authService.login(email = body.email,password = body.password)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body : RefreshRequest
    ): TokenPair{
        return authService.refresh(body.refreshToken)
    }

}