package com.example.passwordmanager.data.network

import com.example.passwordmanager.data.model.IconResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("allicons.json?")
    suspend fun downloadIcon(@Query("url") url: String): Response<IconResponse>


}