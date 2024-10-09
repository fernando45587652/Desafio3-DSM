package com.udb.recursodeaprendizaje

import retrofit2.Call
import retrofit2.http.*

interface RecursoApi {

    @GET("recursos")
    fun obtenerRecursos(): Call<List<Recurso>>

    @GET("recursos/{id}")
    fun obtenerRecurso(@Path("id") id: String): Call<Recurso>

    @POST("recursos")
    fun crearRecurso(@Body recurso: Recurso): Call<Recurso>

    @PUT("recursos/{id}")
    fun actualizarRecurso(@Path("id") id: Int, @Body recurso: Recurso): Call<Recurso>

    @DELETE("recursos/{id}")
    fun eliminarRecurso(@Path("id") id: Int): Call<Void>
}
