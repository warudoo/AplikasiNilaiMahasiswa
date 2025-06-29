package com.nilai.view;

import com.nilai.model.User;
import java.io.PrintWriter;
import javax.servlet.http.HttpSession;

public class Layout {

    public static void printHeader(PrintWriter out, HttpSession session, String title) {
        User user = (User) session.getAttribute("user");
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>" + title + " - Aplikasi Nilai</title>");
        // Link ke CSS Bootstrap via CDN
        out.println("    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
        // Link ke Ikon Bootstrap
        out.println("    <link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css'>");
        out.println("</head>");
        out.println("<body>");

        // Navbar Bootstrap
        out.println("<nav class='navbar navbar-expand-lg navbar-dark bg-primary shadow-sm'>");
        out.println("  <div class='container-fluid'>");
        out.println("    <a class='navbar-brand' href='mahasiswa'>Aplikasi Nilai Muhamad Salwarud</a>");
        out.println("    <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav'>");
        out.println("      <span class='navbar-toggler-icon'></span>");
        out.println("    </button>");
        out.println("    <div class='collapse navbar-collapse' id='navbarNav'>");
        out.println("      <ul class='navbar-nav me-auto mb-2 mb-lg-0'>");
        out.println("        <li class='nav-item'><a class='nav-link' href='mahasiswa'>Mahasiswa</a></li>");
        out.println("        <li class='nav-item'><a class='nav-link' href='matakuliah'>Mata Kuliah</a></li>");
        out.println("        <li class='nav-item'><a class='nav-link' href='nilai'>Input Nilai</a></li>");
        out.println("        <li class='nav-item'><a class='nav-link' href='admin'>Kelola Admin</a></li>");
        out.println("      </ul>");
        // Info User dan Tombol Logout
        if (user != null) {
            out.println("      <span class='navbar-text me-3'>Halo, " + user.getNamaLengkap() + "</span>");
            out.println("      <a href='logout' class='btn btn-danger'>Logout <i class='bi bi-box-arrow-right'></i></a>");
        }
        out.println("    </div>");
        out.println("  </div>");
        out.println("</nav>");

        // Container Utama
        out.println("<div class='container mt-4'>");
    }

    public static void printFooter(PrintWriter out) {
        // Penutup Container Utama
        out.println("</div>");

        // Script Bootstrap JS Bundle via CDN (wajib untuk beberapa komponen)
        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>");
        out.println("</body>");
        out.println("</html>");
    }
}