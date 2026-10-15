# Auripa Store — Backend REST API

> **Platform E-Commerce Modern — MVP (Backend & Admin Area)**
> Lead Engineer: **Kemip (Miftah Aufar)** · Studio: **Vroxilabs** · Status: *Dalam Pengembangan*

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-orange)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-private-lightgrey)](#)

---

## Tentang Aplikasi

**Auripa Store** adalah platform e-commerce yang dirancang untuk memberikan pengalaman berbelanja yang cepat, aman, dan responsif. Arsitektur sistem memisahkan **Backend (REST API)** dari **Frontend (SPA)** — dokumen ini mencakup bagian backend yang saat ini sedang dibangun.

Fokus MVP tahap ini:

- **Keamanan tingkat tinggi** — JWT (Auth0 Java-JWT), BCrypt hashing, stateless session.
- **Manajemen pengguna** — CRUD pengguna dengan role-based access control (`ADMIN` / `CUSTOMER`).
- **Manajemen kategori & katalog produk** — data master yang solid dengan relasi Many-to-One.
- **Standar respons seragam** — seluruh endpoint mengembalikan format `ResData` (`status`, `message`, `data`) agar mudah diparsing di sisi Vue 3.
- **Soft-delete** — penghapusan data tidak menghapus baris fisik (kolom `deleted_at`), menjaga integritas data jangka panjang.

## Fitur Utama (MVP Saat Ini)

| Modul | Fitur |
|---|---|
| **Autentikasi** | `POST /api/auth/login` → token JWT (HMAC256, issuer `AuripaStore`, TTL via env), filter `Authorization: Bearer <token>` |
| **Manajemen Pengguna** | Profil mandiri (`/me`), list, detail, create, update, delete — khusus role `ADMIN` |
| **Kategori Produk** | CRUD + soft delete (`deleted_at`), hanya kategori aktif yang ditampilkan |
| **Katalog Produk** | CRUD + soft delete + flag `is_active` (menyembunyikan produk dari etalase tanpa menghapus), relasi ke kategori valid |
| **Error Handling** | `GlobalExceptionHandler` terpusat: 401, 403, 404, 405, 422, 500 dalam format JSON standar |
| **Validasi** | Jakarta Validation (`@NotBlank`, `@NotNull`, `@Min`) pada semua DTO masukan |

## Tech Stack

| Lapisan | Teknologi |
|---|---|
| Backend API | **Java 25**, **Spring Boot 3.5.0** (Web, Data JPA, Security, Validation, DevTools) |
| Database | **PostgreSQL** — Spring Data JPA (Hibernate) + HikariCP |
| Keamanan | Spring Security · JWT (Auth0 java-jwt 4.4.0) · BCrypt · Stateless session |
| Build | Maven (wrapper `./mvnw`) · Lombok |
| Frontend *(direncanakan)* | Vue 3 (Composition API), Vite, Tailwind CSS, Pinia, Axios |

## Struktur Project

```
auripa/
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md                  ← pengenalan aplikasi (dokumen ini)
├── DOCUMENTATION.md           ← dokumentasi lengkap + flow/alur kerja
└── src/
    ├── main/
    │   ├── java/com/project/auripa/
    │   │   ├── AuripaApplication.java        # Main entry point
    │   │   ├── config/SecurityConfig.java    # Filter chain, BCrypt, seed akun default
    │   │   ├── security/JwtAuthenticationFilter.java
    │   │   ├── controller/                   # Auth, User, Category, Product
    │   │   ├── service/                      # AuthService, UserService, CategoryService, ProductService
    │   │   ├── repository/                   # Spring Data JPA repositories
    │   │   ├── entity/                       # User, Category, Product
    │   │   ├── dto/                          # request/, response/, data/ (ErrorData, ResData)
    │   │   ├── handler/                      # GlobalExceptionHandler, ResourceNotFoundException
    │   │   └── util/                         # ResponseConstant, ResponseBuilder
    │   └── resources/application.properties.example
    └── test/java/com/project/auripa/AuripaApplicationTests.java
```

## Memulai (Getting Started)

### Prasyarat

- JDK **25** (`java -version`)
- **PostgreSQL** berjalan (default `localhost:5432`)
- Maven tidak wajib terpasang — gunakan wrapper `./mvnw`

### 1. Konfigurasi Database & JWT

Salin template konfigurasi, lalu isi sesuai lingkungan Anda:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

```properties
# application.properties
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/auripa_store
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# ⚠️ WAJIB — tidak ada di file example, dibutuhkan oleh AuthService & JwtAuthenticationFilter
app.jwt.secret=RAHASIA_SUPER_AMAN_YANG_PANJANG minimal 32 karakter!!
app.jwt.access-token-expiration=86400000   # 24 jam dalam milidetik
```

> Kedua properti `app.jwt.*` **wajib diisi**, aplikasi tidak akan start tanpanya.
> Sebaiknya isi nilai rahasia lewat environment variable (`APP_JWT_SECRET`, dll) di production.

### 2. Jalankan Aplikasi

```bash
./mvnw spring-boot:run
```

Aplikasi hidup di `http://localhost:8080`. Skema tabel dibuat otomatis oleh Hibernate (`ddl-auto=update`).

### 3. Login & Dapatkan Token

Saat database **kosong**, sistem otomatis membuat 2 akun default (`SecurityConfig.initUsers`):

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `kemip` | `kemip123` | `CUSTOMER` |

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

```json
{
  "status": "success",
  "message": "Login berhasil",
  "data": { "token": "eyJhbGciOiJIUzI1NiIs..." }
}
```

Gunakan token tersebut pada header request selanjutnya:

```bash
curl http://localhost:8080/api/categories \
  -H "Authorization: Bearer <token>"
```

## Ringkasan Endpoint API

| Method | Endpoint | Auth | Keterangan |
|---|---|---|---|
| POST | `/api/auth/login` | Publik | Login → JWT token |
| GET | `/api/users/me` | `ADMIN` | Profil dari klaim token |
| GET | `/api/users` | `ADMIN` | Daftar semua user |
| GET | `/api/users/{id}` | `ADMIN` | Detail user |
| POST | `/api/users` | `ADMIN` | Buat user baru |
| PUT | `/api/users/{id}` | `ADMIN` | Update username/role |
| DELETE | `/api/users/{id}` | `ADMIN` | Hapus user (hard delete) |
| GET | `/api/categories` | Terotentikasi | List kategori aktif |
| GET | `/api/categories/{id}` | Terotentikasi | Detail kategori |
| POST | `/api/categories` | Terotentikasi | Buat kategori |
| PUT | `/api/categories/{id}` | Terotentikasi | Update kategori |
| DELETE | `/api/categories/{id}` | Terotentikasi | Soft delete kategori |
| GET | `/api/products` | Terotentikasi | List produk aktif |
| GET | `/api/products/{id}` | Terotentikasi | Detail produk |
| POST | `/api/products` | Terotentikasi | Buat produk |
| PUT | `/api/products/{id}` | Terotentikasi | Update produk |
| DELETE | `/api/products/{id}` | Terotentikasi | Soft delete produk |

> Detail payload, format respons, dan catatan penting ada di **[DOCUMENTATION.md](DOCUMENTATION.md)**.

## Alur Singkat Aplikasi

```
Client (Vue 3 / Swagger / curl)
        │  POST /api/auth/login
        ▼
 AuthController ──► AuthService ──► UserRepository
        │              │ BCrypt matches + cek role
        │              ▼
        │         JWT (HMAC256, iss=AuripaStore, exp=env)
        ▼
 { token }
        │
        │  Request berikutnya + Authorization: Bearer <token>
        ▼
 JwtAuthenticationFilter ── verifikasi signature & expiry ── invalid? 401
        │ valid
        ▼
 SecurityConfig (route rule: /api/users/** → ROLE_ADMIN)
        │ lolos
        ▼
 Controller ──► Service (validasi, business rule, soft-delete) ──► Repository ──► PostgreSQL
        │
        ▼
 ResData { status, message, data }   ← error dilayani GlobalExceptionHandler
```

## Build & Test

```bash
./mvnw clean package      # build JAR -> target/auripa-0.0.1-SNAPSHOT.jar
./mvnw test               # menjalankan test (butuh PostgreSQL aktif untuk contextLoads)
```

## Dokumen Pendukung

- **[DOCUMENTATION.md](DOCUMENTATION.md)** — dokumentasi teknis lengkap: konfigurasi, model data, referensi API, standar respons, error handling, dan **flow/alur kerja aplikasi** (diagram sekuens).
- PRD internal — *Product Requirements Document v1.0 (MVP)*, Lead Engineer: Kemip (Miftah Aufar), Vroxilabs.

## Roadmap

- ✅ Backend MVP: Auth JWT, User/Category/Product CRUD, soft delete, exception handling terpusat.
- ⏳ Frontend Admin Area: Vue 3 + Vite + Tailwind + Pinia.
- ⏳ Modul CUSTOMER: katalog publik, keranjang (cart), checkout/transaksi.

---

<div align="center">
  <b>Vroxilabs · Auripa Store</b> — dibangun dengan Spring Boot
</div>
