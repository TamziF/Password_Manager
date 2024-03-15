package com.example.passwordmanager.data.repositories

import com.example.passwordmanager.data.model.IconResponse
import com.example.passwordmanager.data.network.ApiService
import retrofit2.Response

interface NetworkRepositoryInterface {

    suspend fun loadPhoto(url: String): Response<IconResponse>

}

class NetworkRepository(
    private val api: ApiService
) : NetworkRepositoryInterface {

    override suspend fun loadPhoto(url: String): Response<IconResponse> {
        return api.downloadIcon(url)
    }
}