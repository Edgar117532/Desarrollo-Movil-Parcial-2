package com.example.notasapp
import com.google.gson.annotations.SerializedName
data class LoginRequest(
    val email: String,
    val password: String
)

data class Usuario(
    val id: Int,
    @SerializedName("name")
    val nombre: String,
    val email: String
)

data class LoginResponse(
    val token: String,
    val user: Usuario
)

data class Nota(
    val id: Int,
    val usuario_id: Int,
    val titulo: String,
    val contenido: String,
    val fecha_creacion: String? = null
)

data class CrearNotaRequest(
    val titulo: String,
    val contenido: String
)