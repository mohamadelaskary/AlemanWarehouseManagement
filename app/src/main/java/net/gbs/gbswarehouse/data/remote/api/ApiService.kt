package net.gbs.gbswarehouse.data.remote.api

import net.gbs.gbswarehouse.data.remote.dto.request.PurchaseOrderRequestDto
import net.gbs.gbswarehouse.data.remote.dto.request.ReceiveVendorSparePartsRequestDto
import net.gbs.gbswarehouse.data.remote.dto.response.LoginResponse
import net.gbs.gbswarehouse.data.remote.dto.response.MobileResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.PlantWarehousesResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.PurchaseOrderResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.RefreshTokenResponse
import net.gbs.gbswarehouse.data.remote.dto.response.WorkerGroupsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/api/AccountHH/Login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<LoginResponse>
    @POST("/api/AccountHH/RefreshToken")
    suspend fun refreshToken(
        @Query("refreshToken") refreshToken: String,
    ): Response<RefreshTokenResponse>

    @GET("api/ReceivingHH/GetPlantWarehouses")
    suspend fun getPlantWarehouses(
        @Query("plantCode") plantCode: String
    ):Response<PlantWarehousesResponseDto>


    @GET("api/ReceivingHH/GetAllWorkerGroups")
    suspend fun getAllWorkerGroups():
            Response<WorkerGroupsResponseDto>


    @POST("api/ReceivingHH/GetPurchaseOrderData")
    suspend fun getPurchaseOrderData(
        @Body request: PurchaseOrderRequestDto
    ): Response<PurchaseOrderResponseDto>


    @POST("api/ReceivingHH/ReceiveVendorSpareParts")
    suspend fun receiveVendorSpareParts(
        @Body request: ReceiveVendorSparePartsRequestDto
    ): Response<MobileResponseDto>
}