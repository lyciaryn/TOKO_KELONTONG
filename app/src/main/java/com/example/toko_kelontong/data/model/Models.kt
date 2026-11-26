package com.example.toko_kelontong.data.model

import com.google.gson.annotations.SerializedName

/**
 * Format respons seragam dari server (PRD C6/C10): {success, message, data}.
 */
data class ApiResponse<T>(
    val success: Boolean = false,
    val message: String? = null,
    val data: T? = null,
)

// ---------- Auth ----------

data class User(
    val id: Int = 0,
    val nama: String = "",
    val email: String = "",
    val role: String = "",
) {
    /** Sesuaikan string role dengan yang dikirim backend. */
    val isOwner: Boolean get() = role.equals("pemilik", ignoreCase = true)
}

data class AuthData(
    val token: String = "",
    val user: User = User(),
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RegisterRequest(
    val nama: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
)

// ---------- Kategori & Produk ----------

data class Category(
    val id: Int = 0,
    val nama: String = "",
)

data class CategoryRequest(val nama: String)

data class Product(
    val id: Int = 0,
    @SerializedName("category_id") val categoryId: Int? = null,
    val nama: String = "",
    @SerializedName("harga_beli") val hargaBeli: Double = 0.0,
    @SerializedName("harga_jual") val hargaJual: Double = 0.0,
    val stok: Int = 0,
    val satuan: String = "",
    /** URL gambar penuh dari server (atau null kalau belum ada). */
    val gambar: String? = null,
    val category: Category? = null,
)

data class ProductRequest(
    @SerializedName("category_id") val categoryId: Int?,
    val nama: String,
    @SerializedName("harga_beli") val hargaBeli: Double,
    @SerializedName("harga_jual") val hargaJual: Double,
    val stok: Int,
    val satuan: String,
)

// ---------- Transaksi ----------

data class Transaction(
    val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    val tanggal: String = "",
    val total: Double = 0.0,
    @SerializedName("metode_bayar") val metodeBayar: String = "tunai",
    val items: List<TransactionItem> = emptyList(),
)

data class TransactionItem(
    val id: Int = 0,
    @SerializedName("product_id") val productId: Int = 0,
    @SerializedName("nama_produk") val namaProduk: String? = null,
    val qty: Int = 0,
    @SerializedName("harga_satuan") val hargaSatuan: Double = 0.0,
)

data class TransactionRequest(
    @SerializedName("metode_bayar") val metodeBayar: String,
    val items: List<TransactionItemRequest>,
)

data class TransactionItemRequest(
    @SerializedName("product_id") val productId: Int,
    val qty: Int,
)

// ---------- Laporan ----------

data class ReportSummary(
    @SerializedName("omzet_harian") val omzetHarian: Double = 0.0,
    @SerializedName("jumlah_transaksi") val jumlahTransaksi: Int = 0,
    @SerializedName("stok_menipis") val stokMenipis: List<Product> = emptyList(),
)

/** Hasil impor produk: jumlah baris berhasil/gagal beserta alasannya (FR-13). */
data class ImportResult(
    val berhasil: Int = 0,
    val gagal: Int = 0,
    val errors: List<ImportRowError> = emptyList(),
)

data class ImportRowError(
    val baris: Int = 0,
    val alasan: String = "",
)
