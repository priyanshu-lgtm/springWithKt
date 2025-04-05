package com.example.spring_practice.sequrity

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date

//{
//
//    Dependency Injection:
//
//    JwtService is a Spring @Service that takes jwtSecret from application properties.
//
//    This secret is decoded into a signing key.
//
//    Token Configuration:
//
//    Access Token: Valid for 15 minutes.
//
//    Refresh Token: Valid for 30 days.
//
//    Token Generation (generateToken):
//
//    Creates a JWT with a subject (userId), type (auth_token or refresh_token), issued date, and expiration date.
//
//    Signs the token using HMAC SHA-256.
//
//    Generating Tokens:
//
//    generateAccessToken(userId): Creates an auth token.
//
//    generateRefreshToken(userId): Creates a refresh token.
//
//    Extracting User ID (getUserIdFromToken):
//
//    Removes "Bearer " prefix if present.
//
//    Parses the token to extract the subject (user ID).
//
//    Token Validation:
//
//    validateAuthToken(token): Checks if the token type is "auth_token".
//
//    validateRefreshToken(token): Checks if the token type is "refresh_token".
//
//    Parsing Claims (parseAllClaims):
//
//    Attempts to decode and verify the JWT.
//
//    Returns the token payload (claims) or null if invalid.
//}

@Service
class JwtService(@Value("\${jwt.secret}") private val jwtSecret : String){

    //creating a secret key to get auth token
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15L * 60L * 1000L
    val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000L

    private fun generateToken(      //creating the token
        userId : String,
        type : String,
        expiry : Long
    ) : String{
        val now = Date()
        val expiryDate = Date(now.time + expiry)

        return Jwts.builder()
            .subject(userId) //the one owns the token
            .claim("type" ,type) //included with the token
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey,Jwts.SIG.HS256) //signing with our key above and signin type
            .compact()
    }

    fun generateAccessToken(userId: String): String
    {
        return generateToken(userId,"auth_token",accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String): String
    {
        return generateToken(userId,"refresh_token",refreshTokenValidityMs)
    }

    fun getUserIdFromToken(token : String) : String{
        val claims = parseAllClaims(token)  ?: throw (IllegalArgumentException("Invalid token"))
        return claims.subject
    }

    fun validateAuthToken(token : String) : Boolean{
        val claims = parseAllClaims(token) ?: return false
        val tokenType =claims["type"] as? String ?: false
        return tokenType == "auth_token"
    }

    fun validateRefreshToken(token : String) : Boolean{
        val claims = parseAllClaims(token) ?: return false
        val tokenType =claims["type"] as? String ?: false
        return tokenType == "refresh_token"
    }

    private fun parseAllClaims(token: String) : Claims? {
        val rawToken = if (token.startsWith("Bearer ")){
            token.removePrefix("Bearer ")
        } else token
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        }catch (e :Exception){
            null
        }
    }

}