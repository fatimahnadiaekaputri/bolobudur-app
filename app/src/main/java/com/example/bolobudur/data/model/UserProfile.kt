package com.example.bolobudur.data.model

data class ProfileResponse(
    val status: Boolean,
    val data: UserProfile?
)

data class UserProfile(
    val uuid: String,
    val name: String,
    val email: String,
    val image_profile: String?,
    val created_at: String?
)

data class UploadImageResponse(
    val message: String,
    val url: String,
    val public_id: String
)

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val image_profile: String?
)





