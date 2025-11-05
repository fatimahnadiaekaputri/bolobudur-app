package com.example.bolobudur.data.model

data class AuthResponse(
    val message: String? = null,
    val token: String? = null,
    val valid: Boolean? = null,
    val user: UserResponse? = null
)

data class UserResponse(
    val uuid: String? = null,
    val name: String? = null,
    val email: String? = null
)
