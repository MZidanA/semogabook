package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.Publisher
import com.insfinal.bookdforall.network.RetrofitInstance
import retrofit2.Response

class PublisherRepository {
    suspend fun getAllPublishers(): Response<List<Publisher>> {
        return RetrofitInstance.api.getAllPublishers()
    }

    suspend fun createPublisher(publisher: Publisher): Response<Unit> {
        return RetrofitInstance.api.createPublisher(publisher)
    }

    suspend fun updatePublisher(id: Int, publisher: Publisher): Response<Unit> {
        return RetrofitInstance.api.updatePublisher(id, publisher)
    }

    suspend fun deletePublisher(id: Int): Response<Unit> {
        return RetrofitInstance.api.deletePublisher(id)
    }
}
