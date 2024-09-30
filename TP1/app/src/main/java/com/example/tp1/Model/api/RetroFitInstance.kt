package com.example.tp1.Model.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetroFitInstance {

    private const val BASE_URL = "https://420c56.drynish.synology.me/"

    private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val getDeckService: GetDeckService.GetPaquetCartesService by lazy {
            retrofit.create(GetDeckService.GetPaquetCartesService::class.java)
        }

}