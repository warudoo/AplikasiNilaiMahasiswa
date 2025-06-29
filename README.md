# 🎓 Aplikasi Nilai Mahasiswa

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/NetBeans%20IDE-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white" alt="NetBeans">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap 5">
  <img src="https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge" alt="MVC Architecture">
</p>

Aplikasi web untuk manajemen nilai akademik mahasiswa, dibangun sepenuhnya dengan **Java Servlet** menggunakan **NetBeans IDE**. Proyek ini menerapkan pola desain **Model-View-Controller (MVC)** untuk memastikan kode yang terstruktur, terorganisir, dan mudah dikelola.

---

## ✨ Fitur Utama

-   **🔐 Sistem Otentikasi:** Sistem login dan logout yang aman untuk admin.
-   **👤 Manajemen Admin:** Operasi CRUD (Create, Read, Update, Delete) penuh untuk mengelola pengguna/admin aplikasi.
-   **🧑‍🎓 Manajemen Mahasiswa:** Mengelola data master mahasiswa (NIM, Nama, Jurusan).
-   **📚 Manajemen Mata Kuliah:** Mengelola data master mata kuliah (Kode MK, Nama, SKS).
-   **📝 Transaksi Nilai:** Fungsionalitas inti untuk menambah, mengedit, dan menghapus nilai mahasiswa, dengan perhitungan nilai akhir otomatis.
-   **🔒 Keamanan Password:** Enkripsi password menggunakan algoritma **SHA-256** untuk keamanan data login.
-   **🎨 Antarmuka Modern:** Tampilan antarmuka yang bersih dan responsif dibangun dengan **Bootstrap 5**.

---

## 🏗️ Arsitektur & Teknologi

Aplikasi ini dibangun di atas tumpukan teknologi Java EE klasik dan mengikuti arsitektur MVC yang ketat untuk memisahkan antara data (Model), tampilan (View), dan kontrol alur aplikasi (Controller).

| Kategori       | Teknologi / Pustaka                                    |
| :------------- | :----------------------------------------------------- |
| **Bahasa** | `Java`                                                 |
| **IDE** | `NetBeans IDE 8.2+`                                    |
| **Server** | `GlassFish Server` / `Apache Tomcat`                   |
| **Database** | `MySQL` (terhubung via JDBC)                           |
| **Frontend** | `Bootstrap 5` (via CDN)                                |
| **Dependensi** | `MySQL Connector/J`                                    |

<br>

<details>
<summary>📂 Struktur Proyek yang Disarankan</summary>

<pre>
AplikasiNilaiMahasiswa/
├── src/
│   └── java/
│       └── com/
│           └── nilai/
│               ├── <strong>controller/</strong>  (Logika & Alur Aplikasi)
│               │   ├── AdminController.java
│               │   ├── LoginController.java
│               │   ├── LogoutController.java
│               │   ├── MahasiswaController.java
│               │   ├── MataKuliahController.java
│               │   ├── NilaiController.java
│               │   └── LoginFilter.java
│               │
│               ├── <strong>model/</strong>         (Logika Bisnis & Database)
│               │   ├── Enkripsi.java
│               │   ├── Koneksi.java
│               │   ├── Mahasiswa.java
│               │   ├── MataKuliah.java
│               │   ├── Nilai.java
│               │   └── User.java
│               │
│               └── <strong>view/</strong>          (Komponen Tampilan)
│                   └── Layout.java
│
├── web/
│   └── WEB-INF/
│       ├── lib/
│       │   └── mysql-connector-j-x.x.x.jar
│       └── web.xml
│
└── build.xml
</pre>
</details>

---

## 🚀 Panduan Instalasi & Penggunaan

### **Prasyarat**
1.  **JDK (Java Development Kit)** versi 8 atau lebih tinggi.
2.  **IDE NetBeans** (disarankan versi yang mendukung Java EE).
3.  **Server Web** (Apache Tomcat atau GlassFish).
4.  **Database MySQL** (disarankan menggunakan XAMPP).

### **Langkah-langkah Instalasi**

1.  **Setup Database**
    - Buka **phpMyAdmin** dan buat database baru bernama `db_nilai_mahasiswa`.
    - Impor atau jalankan query dari file `database.sql` untuk membuat semua tabel (`t_users`, `t_mahasiswa`, `t_matakuliah`, `t_nilai`) dan mengisi data admin awal.

2.  **Buka Proyek**
    - Buka NetBeans, pilih `File > Open Project`, dan arahkan ke folder proyek ini.

3.  **Konfigurasi Koneksi**
    - Pastikan driver `mysql-connector-j-x.x.x.jar` sudah ada di folder `web/WEB-INF/lib/`.
    - Jika perlu, sesuaikan `USER` dan `PASSWORD` database di file `src/java/com/nilai/model/Koneksi.java`.

4.  **Jalankan Aplikasi**
    - Klik kanan pada proyek, pilih **Clean and Build**.
    - Setelah selesai, klik kanan lagi dan pilih **Run**.

### **Akses dan Penggunaan Aplikasi**
- Buka browser dan akses aplikasi yang berjalan (biasanya `http://localhost:8080/NamaProyekAnda/`).
- Anda akan diarahkan ke halaman login. Gunakan kredensial default:
  - **Username**: `admin`
  - **Password**: `admin`
- Setelah login, Anda dapat mulai mengelola data melalui menu navigasi yang tersedia.

---

<details>
<summary>📦 Contoh Kode Model (Koneksi.java)</summary>

```java
// src/java/com/nilai/model/Koneksi.java
package com.nilai.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static final String URL = "jdbc:mysql://localhost:3306/db_nilai_mahasiswa";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); // Untuk Connector/J v5
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi ke database GAGAL: " + e.getMessage());
        }
        return connection;
    }
}
```
</details>

<details>
<summary>🎮 Contoh Kode Controller (MahasiswaController.java)</summary>

```java
// src/java/com/nilai/controller/MahasiswaController.java
package com.nilai.controller;

import com.nilai.model.Mahasiswa;
import com.nilai.view.Layout;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
// ... import lainnya

@WebServlet(name = "MahasiswaController", urlPatterns = {"/mahasiswa"})
public class MahasiswaController extends HttpServlet {
    
    private final Mahasiswa mahasiswaModel = new Mahasiswa();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Logika untuk menampilkan halaman atau data edit
        tampilkanHalaman(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Logika untuk menyimpan atau mengupdate data
        // ...
        response.sendRedirect("mahasiswa");
    }

    private void tampilkanHalaman(HttpServletRequest request, HttpServletResponse response, Mahasiswa mhsEdit) 
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            
            // Memanggil layout header
            Layout.printHeader(out, session, "Kelola Mahasiswa");

            // Konten halaman (form, tabel, dll.)
            // ...

            // Memanggil layout footer
            Layout.printFooter(out);
        }
    }
}
```
</details>
<br>
<div align="center">
  Dibuat dengan Java Servlet & Bootstrap
</div>
```
