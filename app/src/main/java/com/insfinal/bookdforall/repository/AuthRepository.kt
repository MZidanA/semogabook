package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.ForgotPasswordRequest
import com.insfinal.bookdforall.model.SetNewPasswordRequest
import com.insfinal.bookdforall.network.RetrofitInstance
import retrofit2.Response

class AuthRepository {
    suspend fun forgotPassword(email: String): Response<Unit> {
        return RetrofitInstance.api.forgotPassword(ForgotPasswordRequest(email))
    }
    suspend fun setNewPassword(email: String, newPassword: String): Response<Unit> {
        return RetrofitInstance.api.setNewPassword(SetNewPasswordRequest(email, newPassword))
    }
}