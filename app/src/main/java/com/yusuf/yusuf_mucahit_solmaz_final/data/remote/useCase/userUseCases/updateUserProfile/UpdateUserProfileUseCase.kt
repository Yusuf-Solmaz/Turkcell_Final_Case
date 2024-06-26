package com.yusuf.yusuf_mucahit_solmaz_final.data.remote.useCase.userUseCases.updateUserProfile

import com.yusuf.yusuf_mucahit_solmaz_final.core.rootFlow.rootFlow
import com.yusuf.yusuf_mucahit_solmaz_final.data.remote.network.ApiService
import com.yusuf.yusuf_mucahit_solmaz_final.data.remote.responses.profile.UpdateUserProfileRequest
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(private val apiService: ApiService){

    fun updateUserProfile(userId: Int, request: UpdateUserProfileRequest) = rootFlow {
        apiService.updateUserProfile(userId, request)
    }

}