package com.example.notasapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    fun iniciarSesion(
        @Body datos: LoginRequest
    ): Call<LoginResponse>

    @GET("api/notas")
    fun obtenerNotas(
        @Header("Authorization") token: String
    ): Call<List<Nota>>

    @POST("api/notas")
    fun crearNota(
        @Header("Authorization") token: String,
        @Body nota: CrearNotaRequest
    ): Call<Nota>
}