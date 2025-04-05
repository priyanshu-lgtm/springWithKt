package com.example.spring_practice.sequrity

import com.example.spring_practice.model.RefreshTokenCollection
import com.example.spring_practice.model.TokenPair
import com.example.spring_practice.model.User
import com.example.spring_practice.repositories.RefreshTokenRepo
import com.example.spring_practice.repositories.UserRepo
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepo: UserRepo,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepo: RefreshTokenRepo
) {

    fun register(email:String,password: String) : User{
        val user = userRepo.findByEmail(email.trim())
        if(user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists.")
        }
        return userRepo.save(
            User(
                email,
                hashedPassword = hashEncoder.encode(password)
            )
        )
    }

    fun login(password: String,email: String):TokenPair{
        val user = userRepo.findByEmail(email) ?: throw BadCredentialsException("Invalid Credentials")

        if (!hashEncoder.matches(password,user.hashedPassword)){
            throw BadCredentialsException("Invalid Credentials")
        }

        val accessToken = jwtService.generateAccessToken(user.id.toHexString())
        val refreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id,refreshToken)

        return TokenPair(accessToken, refreshToken)

    }

    @Transactional
    fun refresh(refreshToken : String): TokenPair{
        if(!jwtService.validateRefreshToken(refreshToken)) {
            throw IllegalArgumentException("Invalid refreshToken")
        }
        val userId  = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepo.findById(ObjectId(userId)).orElseThrow{
            IllegalArgumentException("Invalid or expired token")
        }

        val hashedRefreshToken = hashToken(refreshToken)

        refreshTokenRepo.findByUserIdAndHashedToken(user.id,hashedRefreshToken) ?: throw
            IllegalArgumentException("Token Expired or Invalid")

        refreshTokenRepo.deleteByUserIdAndHashedToken(user.id,hashedRefreshToken)

        val newRefreshToken = jwtService.generateRefreshToken(userId)
        val newAccessToken =  jwtService.generateAccessToken(userId)

        storeRefreshToken(user.id,newRefreshToken)

        return TokenPair(
            newAccessToken,newRefreshToken
        )

    }

    private fun storeRefreshToken(userId : ObjectId, rawRefreshToken : String){

        val hashedToken = hashToken(rawRefreshToken)
        val expireAt = Instant.now().plusMillis(jwtService.refreshTokenValidityMs)
        refreshTokenRepo.save(
            RefreshTokenCollection(
                hashedToken = hashedToken ,
                userId = userId,
                expireAt = expireAt

            )
        )
    }

    private fun hashToken(rawRefreshToken : String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawRefreshToken.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

}