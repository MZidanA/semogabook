package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.*
import com.insfinal.bookdforall.network.RetrofitInstance

class AdminRepository {
    private val api = RetrofitInstance.api

    suspend fun getAll() = api.getAdmins()
    suspend fun getOne(id: Int) = api.getAdmin(id)
    suspend fun create(req: CreateAdminRequest) = api.createAdmin(req)
    suspend fun update(id: Int, req: CreateAdminRequest) = api.updateAdmin(id, req)
    suspend fun delete(id: Int) = api.deleteAdmin(id)
}
