# 🏯 Bolobudur Android Application

Bolobudur merupakan aplikasi Android yang dirancang untuk membantu wisatawan dalam mengeksplorasi kawasan Candi Borobudur secara interaktif dan informatif. Aplikasi ini terintegrasi dengan perangkat wearable **Bolotooth** untuk memperoleh posisi dan orientasi pengguna secara real-time, serta menyediakan navigasi rute tercepat, pencarian cagar budaya terdekat, dan informasi sejarah yang valid. Aplikasi ini dikembangkan oleh tim F04 untuk memenuhi Capstone Project 2025.

---

## 📌 System Overview

Aplikasi Bolobudur menerapkan pendekatan **client-server** yang terdiri dari perangkat wearable, aplikasi Android, backend server, dan basis data spasial. Sistem ini dirancang secara modular untuk mendukung pengembangan, pemeliharaan, dan perluasan fitur di masa mendatang.

---

## 🧠 Application Architecture (MVVM)

Aplikasi Android dikembangkan dengan arsitektur **Model–View–ViewModel (MVVM)** untuk memisahkan tanggung jawab antara tampilan antarmuka, logika aplikasi, dan pengelolaan data.

- **Model**  
  Bertanggung jawab terhadap pengelolaan data yang berasal dari berbagai sumber, seperti Bluetooth (Bolotooth), REST API backend, dan penyimpanan lokal.

- **View**  
  Merepresentasikan antarmuka pengguna yang dibangun menggunakan **Jetpack Compose** dan bersifat reaktif terhadap perubahan state.

- **ViewModel**  
  Mengelola state UI serta logika bisnis, dan berperan sebagai penghubung antara View dan Model tanpa ketergantungan langsung.

Pendekatan ini meningkatkan keterbacaan kode, skalabilitas aplikasi, serta kemudahan dalam pengujian dan pemeliharaan sistem.

---

## 📂 Project Structure

```text
java/com/example/bolobudur
│
├── data
│   ├── bluetooth        # Komunikasi Bluetooth dengan perangkat Bolotooth
│   ├── local            # Pengelolaan data lokal
│   ├── remote           # Komunikasi dengan backend server (REST API)
│   ├── model            # Data model / DTO
│   └── repository       # Abstraksi akses data
│
├── di                   # Dependency Injection
│
├── ui
│   ├── auth             # Autentikasi pengguna
│   ├── components       # Reusable UI components
│   ├── model            # UI state dan UI model
│   ├── navigation       # Pengelolaan navigasi aplikasi
│   ├── screen
│   │   ├── bluetooth
│   │   ├── bolofind
│   │   ├── bolomaps
│   │   ├── borobudurpedia
│   │   ├── connection
│   │   ├── home
│   │   ├── login
│   │   ├── profile
│   │   ├── register
│   │   └── splash
│   └── theme            # Tema, warna, dan typography
│
├── utils                # Helper dan utility
│
├── BolobudurApp.kt      # Inisialisasi aplikasi
└── MainActivity.kt      # Entry point aplikasi


```
## Main Features
### 🗺️ BoloMaps
BoloMaps merupakan fitur navigasi utama yang menampilkan peta kawasan Candi Borobudur serta visualisasi rute tercepat menuju lokasi tujuan. Perhitungan rute dilakukan menggunakan algoritma Dijkstra berdasarkan data graf yang tersedia di backend server. Visualisasi peta memanfaatkan Mapbox SDK yang mendukung tampilan peta 2D, 3D, citra satelit, jalan, serta Point of Interest (POI). Posisi pengguna diperbarui secara berkala berdasarkan data dari perangkat Bolotooth.

### 🔍 BoloFind
BoloFind berfungsi untuk mendeteksi dan menampilkan daftar cagar budaya di sekitar lokasi pengguna dalam bentuk Point of Interest (POI). Data POI diperoleh dari backend server menggunakan query spasial PostGIS berdasarkan radius dan koordinat latitude-longitude pengguna. Setiap POI dilengkapi dengan informasi nama, deskripsi, gambar, serta lokasi, sehingga pengguna dapat mengenali dan mempelajari objek budaya di sekitarnya.

### 📚 Borobudurpedia
Borobudurpedia menyediakan informasi budaya dan sejarah terkait cagar budaya di kawasan Candi Borobudur. Konten yang disajikan berupa deskripsi dan gambar yang diperoleh dari sumber Borobudurpedia resmi milik pihak konservasi cagar budaya, sehingga informasi yang ditampilkan bersifat valid dan terpercaya.

### 🔐 Authentication & Profile
Fitur autentikasi digunakan untuk mengidentifikasi pengguna yang mengakses aplikasi serta mengamankan data pengguna selama penggunaan sistem. Melalui fitur profil, pengguna dapat menyimpan dan mengelola informasi akun yang digunakan dalam aplikasi.

## 🛠️ Tech Stack
### 📱 Android

- Kotlin 
- Android Studio 
- Jetpack Compose 
- MVVM Architecture

### 🗺️ Mapping & Navigation

- Mapbox SDK 
- Dijkstra Algorithm

### 🔌 Connectivity

- Bluetooth Classic

### 🌐 Backend & Database

- RESTful API
- Express.js
- PostgreSQL
- PostGIS

## ▶️ How to Run the Project
### Prerequisites
- Android Studio (latest version)
- Android SDK 
- Android device dengan dukungan Bluetooth (Android 12++)
- Mapbox Access Token

### Steps
1. **Clone repository**
   ```bash
   git clone https://github.com/username/bolobudur-app.git
   ```
2. **Buka project**

   Buka folder project menggunakan Android Studio.
3. **Tambahkan Mapbox Access Token**

   Tambahkan token Mapbox ke dalam file res/values/developer-config.xml
```bash
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="mapbox_access_token">your_mapbox_token</string>
</resources>
```
4. **Jalankan aplikasi**

   Jalankan aplikasi pada perangkat Android atau emulator yang mendukung Bluetooth (disarankan Android 12++).

## ⚠️ Notes

- Aplikasi memerlukan izin Location dan Bluetooth 
- Beberapa fitur membutuhkan koneksi ke backend server 
- Penggunaan Mapbox bersifat gratis dengan batasan jumlah perangkat