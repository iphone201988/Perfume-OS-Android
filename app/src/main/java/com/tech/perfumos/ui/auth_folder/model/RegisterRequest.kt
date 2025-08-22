package com.tech.perfumos.ui.auth_folder.model

data class RegisterRequest(
    val email: String?,
    val password: String?,
    val username: String?,
    val fullName: String?,
    val deviceToken: String?,
    val deviceType: Int?
)
