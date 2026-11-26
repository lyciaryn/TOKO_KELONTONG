package com.example.toko_kelontong.data.api

import com.example.toko_kelontong.data.model.ApiResponse
import com.example.toko_kelontong.data.model.AuthData
import com.example.toko_kelontong.data.model.Category
import com.example.toko_kelontong.data.model.CategoryRequest
import com.example.toko_kelontong.data.model.ImportResult
import com.example.toko_kelontong.data.model.LoginRequest
import com.example.toko_kelontong.data.model.Product
import com.example.toko_kelontong.data.model.ProductRequest
import com.example.toko_kelontong.data.model.RegisterRequest
import com.example.toko_kelontong.data.model.ReportSummary
import com.example.toko_kelontong.data.model.Transaction
import com.example.toko_kelontong.data.model.TransactionRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Kontrak API sesuai PRD C10. Sesuaikan path/parameter dengan backend Anda.
 */
interface ApiService {

    // ----- Auth -----
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<ApiResponse<AuthData>>

    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // ----- Kategori -----
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    @POST("categories")
    suspend fun createCategory(@Body body: CategoryRequest): Response<ApiResponse<Category>>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body body: CategoryRequest,
    ): Response<ApiResponse<Category>>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ----- Produk -----
    @GET("products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("page") page: Int = 1,
    ): Response<ApiResponse<List<Product>>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ApiResponse<Product>>

    @POST("products")
    suspend fun createProduct(@Body body: ProductRequest): Response<ApiResponse<Product>>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body body: ProductRequest,
    ): Response<ApiResponse<Product>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @Multipart
    @POST("products/{id}/image")
    suspend fun uploadProductImage(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part,
    ): Response<ApiResponse<Product>>

    // ----- Impor / Ekspor -----
    @Multipart
    @POST("products/import")
    suspend fun importProducts(
        @Part file: MultipartBody.Part,
    ): Response<ApiResponse<ImportResult>>

    @Streaming
    @GET("reports/export")
    suspend fun exportReport(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("format") format: String = "csv",
    ): Response<ResponseBody>

    // ----- Transaksi -----
    @GET("transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
    ): Response<ApiResponse<List<Transaction>>>

    @POST("transactions")
    suspend fun createTransaction(
        @Body body: TransactionRequest,
    ): Response<ApiResponse<Transaction>>

    @GET("transactions/{id}")
    suspend fun getTransaction(@Path("id") id: Int): Response<ApiResponse<Transaction>>

    // ----- Laporan -----
    @GET("reports/summary")
    suspend fun getReportSummary(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
    ): Response<ApiResponse<ReportSummary>>
}
