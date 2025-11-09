package com.example.bolobudur.data.model

data class UserProfile(
    val uuid: String,
    val name: String,
    val email: String,
    val created_at: String?,
    val updated_at: String?
)
