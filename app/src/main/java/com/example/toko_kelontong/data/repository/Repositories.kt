package com.example.toko_kelontong.data.repository

import com.example.toko_kelontong.data.api.ApiService
import com.example.toko_kelontong.data.local.SessionManager
import com.example.toko_kelontong.data.model.AuthData
import com.example.toko_kelontong.data.model.Category
import com.example.toko_kelontong.data.model.CategoryRequest
import com.example.toko_kelontong.data.model.LoginRequest
import com.example.toko_kelontong.data.model.Product
import com.example.toko_kelontong.data.model.ProductRequest
import com.example.toko_kelontong.data.model.RegisterRequest
import com.example.toko_kelontong.data.model.ReportSummary
import com.example.toko_kelontong.data.model.Transaction
import com.example.toko_kelontong.data.model.TransactionRequest

class AuthRepository(
    private val api: ApiService,
    private val session: SessionManager,
) {
    suspend fun login(email: String, password: String): ApiResult<AuthData> {
        val result = safeApiCall { api.login(LoginRequest(email, password)) }
        return saveIfSuccess(result)
    }

    suspend fun register(
        nama: String,
        email: String,
        password: String,
    ): ApiResult<AuthData> {
        val result = safeApiCall {
            api.register(RegisterRequest(nama, email, password, password))
        }
        return saveIfSuccess(result)
    }

    /** Sesi lokal selalu dihapus, walaupun panggilan logout ke server gagal. */
    suspend fun logout() {
        safeApiCall { api.logout() }
        session.clear()
    }

    private suspend fun saveIfSuccess(result: ApiResult<AuthData>): ApiResult<AuthData> {
        if (result is ApiResult.Success) {
            val data = result.data
                ?: return ApiResult.Error("Respons server tidak berisi data login.")
            session.save(data.token, data.user)
        }
        return result
    }
}

class CategoryRepository(private val api: ApiService) {
    suspend fun getAll(): ApiResult<List<Category>> = safeApiCall { api.getCategories() }
    suspend fun create(nama: String) = safeApiCall { api.createCategory(CategoryRequest(nama)) }
    suspend fun update(id: Int, nama: String) =
        safeApiCall { api.updateCategory(id, CategoryRequest(nama)) }

    suspend fun delete(id: Int) = safeApiCall { api.deleteCategory(id) }
}

class ProductRepository(private val api: ApiService) {
    suspend fun getProducts(
        search: String? = null,
        categoryId: Int? = null,
        page: Int = 1,
    ): ApiResult<List<Product>> =
        safeApiCall { api.getProducts(search?.ifBlank { null }, categoryId, page) }

    suspend fun getProduct(id: Int) = safeApiCall { api.getProduct(id) }
    suspend fun create(body: ProductRequest) = safeApiCall { api.createProduct(body) }
    suspend fun update(id: Int, body: ProductRequest) = safeApiCall { api.updateProduct(id, body) }
    suspend fun delete(id: Int) = safeApiCall { api.deleteProduct(id) }
}

class TransactionRepository(private val api: ApiService) {
    suspend fun getAll(page: Int = 1): ApiResult<List<Transaction>> =
        safeApiCall { api.getTransactions(page) }

    suspend fun getDetail(id: Int) = safeApiCall { api.getTransaction(id) }
    suspend fun create(body: TransactionRequest) = safeApiCall { api.createTransaction(body) }
}

class ReportRepository(private val api: ApiService) {
    suspend fun getSummary(from: String? = null, to: String? = null): ApiResult<ReportSummary> =
        safeApiCall { api.getReportSummary(from, to) }
}
