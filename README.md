# Toko Kelontong UMKM

Aplikasi Android untuk mendigitalkan operasional toko kelontong: mencatat produk, stok, dan penjualan secara terpusat melalui REST API, menggantikan pencatatan manual di Excel.

Tugas Besar Pengembangan Aplikasi Bergerak (PAB) 2026.

> Status: **Draf awal.** Kerangka proyek dan layar Login, Register, Home, serta Daftar Produk sudah ada. Fitur lain masih dikerjakan (lihat [Progres](#progres)).

## Fitur (sesuai PRD)

| Kelompok | Fitur | Prioritas |
|---|---|---|
| Akun | Registrasi, login, logout (token), peran Pemilik dan Kasir, profil | Wajib |
| Produk | CRUD, unggah gambar, pencarian, validasi input | Wajib |
| Kategori | CRUD kategori, filter produk per kategori | Wajib |
| Transaksi | Keranjang kasir, total otomatis, stok berkurang otomatis, riwayat dan detail | Wajib |
| Umum | Indikator loading dan pesan error di setiap layar | Wajib |
| Pemilik | Kelola akun kasir, dashboard, laporan per periode | Tinggi |
| Excel | Impor produk dari Excel/CSV, ekspor laporan | Tinggi |
| Inovasi | Pilih 1-2: scan barcode, kasbon, grafik penjualan, peringatan stok | Opsional |

## Teknologi

- **Bahasa:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arsitektur:** MVVM (ViewModel, StateFlow, Repository)
- **Jaringan:** Retrofit + OkHttp (interceptor token) + Gson
- **Gambar:** Coil 3
- **Penyimpanan token:** DataStore Preferences
- **Navigasi:** Navigation Compose
- **Asynchronous:** Kotlin Coroutines / Flow
- **Backend:** bebas (Laravel atau Express) + MySQL, dengan JWT atau Sanctum

Versi pustaka ada di [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Prasyarat

- Android Studio versi terbaru yang mendukung AGP 9.5
- Android SDK Platform **37** (`compileSdk` 37)
- JDK 11 atau lebih baru (bawaan Android Studio sudah cukup)
- Backend REST API berjalan di laptop (lihat [Menjalankan](#menjalankan))

## Menjalankan

1. Buka folder proyek di Android Studio, lalu tunggu Gradle sync selesai.
2. Jalankan backend lokal Anda, misalnya di port `8000`.
3. Atur alamat server di [`app/build.gradle.kts`](app/build.gradle.kts):

   ```kotlin
   buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/api/\"")
   ```

   | Perangkat | Alamat |
   |---|---|
   | Emulator Android | `http://10.0.2.2:PORT/api/` |
   | HP fisik | `http://<IP-laptop>:PORT/api/` (satu jaringan Wi-Fi) |

   Alamat harus diakhiri `/`.

4. Untuk HP fisik, daftarkan IP laptop di [`network_security_config.xml`](app/src/main/res/xml/network_security_config.xml), karena HTTP biasa hanya diizinkan untuk alamat yang terdaftar:

   ```xml
   <domain includeSubdomains="false">192.168.1.10</domain>
   ```

5. Jalankan konfigurasi `app` pada emulator atau HP.

Build lewat terminal:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

## Struktur Proyek

```
app/src/main/java/com/example/toko_kelontong/
├── MainActivity.kt          # Titik masuk, memasang tema dan TokoApp
├── TokoApplication.kt       # Membuat AppContainer sekali
├── AppContainer.kt          # DI manual: OkHttp, Retrofit, repository
├── data/
│   ├── api/                 # ApiService (endpoint), AuthInterceptor (token + 401)
│   ├── local/               # SessionManager (DataStore: token, role, nama)
│   ├── model/               # Model data dan request/response
│   └── repository/          # Repository, ApiResult, safeApiCall
└── ui/
    ├── TokoApp.kt           # NavHost dan rute
    ├── theme/               # Tema Material 3
    ├── common/              # UiState, Validators, komponen umum
    ├── auth/                # Login dan Register
    ├── home/                # Beranda
    └── product/             # Daftar produk + pencarian
```

### Pola MVVM

Setiap layar punya `ViewModel` yang mengeluarkan `StateFlow`. Layar Compose hanya merender state tersebut:

```
Screen (Compose) ──► ViewModel ──► Repository ──► ApiService (Retrofit)
        ▲                │
        └── UiState ◄────┘   (Loading / Success / Empty / Error)
```

Untuk layar baru, tiru pola di `ui/product/ProductViewModel.kt` dan `ProductListScreen.kt`.

## Kontrak API

Format respons seragam:

```json
{ "success": true, "message": "...", "data": {} }
```

Kode status: 200, 201, 400, 401, 403, 404, 422.

| Kelompok | Endpoint | Akses |
|---|---|---|
| Auth | `POST /register`, `POST /login`, `POST /logout` | Publik / token |
| Kategori | `GET, POST /categories`, `PUT, DELETE /categories/{id}` | Token; tulis: Pemilik |
| Produk | `GET /products?search=&category_id=&page=`, `GET /products/{id}`, `POST /products`, `PUT /products/{id}`, `DELETE /products/{id}`, `POST /products/{id}/image` | Token; tulis: Pemilik |
| Transaksi | `GET /transactions`, `POST /transactions`, `GET /transactions/{id}` | Token |
| Impor/Ekspor | `POST /products/import`, `GET /reports/export` | Pemilik |
| Laporan | `GET /reports/summary` | Pemilik |

Definisi lengkap ada di [`ApiService.kt`](app/src/main/java/com/example/toko_kelontong/data/api/ApiService.kt).

### Asumsi terhadap backend

Sesuaikan backend atau kode di bawah ini bila berbeda:

- Role pemilik bernilai `"pemilik"` (lihat `User.isOwner`).
- Respons login berisi `data: { token, user: { id, nama, email, role } }`.
- Nama field JSON mengikuti PRD: `category_id`, `harga_beli`, `harga_jual`, `stok`, `satuan`, `gambar`.
- Field `gambar` berisi URL gambar penuh.
- Endpoint daftar menerima parameter `page` untuk pagination.
- Endpoint kelola kasir belum ada di kontrak PRD dan perlu ditambahkan.

## Progres

- [x] Setup proyek: Compose, Retrofit, DataStore, Coil, navigasi
- [x] Lapisan data: API, interceptor token, repository, penanganan error
- [x] Login, Register, logout, auto-logout saat 401
- [x] Daftar produk + pencarian
- [ ] Kategori dan form tambah/ubah produk (unggah gambar)
- [ ] Layar kasir/keranjang dan transaksi
- [ ] Riwayat dan detail transaksi
- [ ] Dashboard dan laporan
- [ ] Kelola akun kasir
- [ ] Impor/ekspor Excel/CSV
- [ ] Fitur inovasi (pilih 1-2)
- [ ] Pembatasan menu berdasarkan peran

## Catatan Keamanan

- Token disimpan di DataStore tanpa enkripsi. Cukup untuk tugas, bukan untuk produksi.
- HTTP biasa hanya diizinkan ke alamat development yang terdaftar di `network_security_config.xml`. Jangan memakainya untuk server produksi.
- Validasi di aplikasi hanya membantu pengguna. Validasi sebenarnya tetap harus ada di server.

## Etika dan Atribusi

Penggunaan AI dan pustaka pihak ketiga wajib dicantumkan di laporan dan dipahami saat tanya jawab. Pustaka yang dipakai tercantum di bagian [Teknologi](#teknologi).
