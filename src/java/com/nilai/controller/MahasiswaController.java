package com.nilai.controller;

import com.nilai.model.Mahasiswa;
import com.nilai.view.Layout; // Import kelas Layout
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MahasiswaController", urlPatterns = {"/mahasiswa"})
public class MahasiswaController extends HttpServlet {
    
    private final Mahasiswa mahasiswaModel = new Mahasiswa();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null) {
            switch(action) {
                case "edit":
                    handleEdit(request, response);
                    return;
                case "delete":
                    handleDelete(request, response);
                    return;
            }
        }
        tampilkanHalaman(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nimLama = request.getParameter("nimLama");
        
        mahasiswaModel.setNim(request.getParameter("nim"));
        mahasiswaModel.setNama(request.getParameter("nama"));
        mahasiswaModel.setJurusan(request.getParameter("jurusan"));

        boolean sukses;
        if (nimLama != null && !nimLama.isEmpty()) {
            sukses = mahasiswaModel.update(nimLama);
        } else {
            sukses = mahasiswaModel.simpan();
        }
        
        if (sukses) {
            request.getSession().setAttribute("message", "Data mahasiswa berhasil disimpan!");
        } else {
            request.getSession().setAttribute("error", mahasiswaModel.getPesan());
        }
        response.sendRedirect("mahasiswa");
    }
    
    private void handleEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nim = request.getParameter("nim");
        Mahasiswa mhsEdit = new Mahasiswa();
        if(mhsEdit.baca(nim)){
            tampilkanHalaman(request, response, mhsEdit);
        } else {
             request.getSession().setAttribute("error", "Data tidak ditemukan!");
             response.sendRedirect("mahasiswa");
        }
    }
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
         String nim = request.getParameter("nim");
         if(mahasiswaModel.hapus(nim)){
             request.getSession().setAttribute("message", "Data berhasil dihapus!");
         } else {
             request.getSession().setAttribute("error", "Gagal menghapus: " + mahasiswaModel.getPesan());
         }
         response.sendRedirect("mahasiswa");
    }

    private void tampilkanHalaman(HttpServletRequest request, HttpServletResponse response, Mahasiswa mhsEdit) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            
            // Panggil Header
            Layout.printHeader(out, session, "Kelola Mahasiswa");

            out.println("<h1><i class='bi bi-people-fill'></i> Kelola Data Mahasiswa</h1>");

            if (session.getAttribute("message") != null) {
                out.println("<div class='alert alert-success'>" + session.getAttribute("message") + "</div>");
                session.removeAttribute("message");
            }
            if (session.getAttribute("error") != null) {
                out.println("<div class='alert alert-danger'>" + session.getAttribute("error") + "</div>");
                session.removeAttribute("error");
            }

            // Gunakan Card untuk Form
            out.println("<div class='card mb-4'>");
            out.println("  <div class='card-header'>" + (mhsEdit != null ? "Edit" : "Tambah") + " Mahasiswa</div>");
            out.println("  <div class='card-body'>");
            out.println("    <form method='post' action='mahasiswa'>");
            if (mhsEdit != null) {
                out.println("      <input type='hidden' name='nimLama' value='" + mhsEdit.getNim() + "'>");
            }
            out.println("      <div class='mb-3'><label for='nim' class='form-label'>NIM</label><input type='text' class='form-control' id='nim' name='nim' value='" + (mhsEdit != null ? mhsEdit.getNim() : "") + "' required></div>");
            out.println("      <div class='mb-3'><label for='nama' class='form-label'>Nama Lengkap</label><input type='text' class='form-control' id='nama' name='nama' value='" + (mhsEdit != null ? mhsEdit.getNama() : "") + "' required></div>");
            out.println("      <div class='mb-3'><label for='jurusan' class='form-label'>Jurusan</label><input type='text' class='form-control' id='jurusan' name='jurusan' value='" + (mhsEdit != null ? mhsEdit.getJurusan() : "") + "' required></div>");
            out.println("      <button type='submit' class='btn btn-primary'>" + (mhsEdit != null ? "Update Data" : "Simpan") + "</button>");
            if (mhsEdit != null) {
                out.println("      <a href='mahasiswa' class='btn btn-secondary ms-2'>Batal</a>");
            }
            out.println("    </form>");
            out.println("  </div>");
            out.println("</div>");
            
            // Gunakan Card untuk Tabel
            out.println("<div class='card'>");
            out.println("  <div class='card-header'>Daftar Mahasiswa</div>");
            out.println("  <div class='card-body'>");
            out.println("    <div class='table-responsive'>");
            out.println("      <table class='table table-striped table-hover'>");
            out.println("        <thead><tr><th>NIM</th><th>Nama</th><th>Jurusan</th><th>Aksi</th></tr></thead><tbody>");
            if (mahasiswaModel.bacaData()) {
                for (Object[] row : mahasiswaModel.getList()) {
                    out.println("<tr>");
                    out.println("<td>" + row[0] + "</td><td>" + row[1] + "</td><td>" + row[2] + "</td>");
                    out.println("<td><a href='mahasiswa?action=edit&nim=" + row[0] + "' class='btn btn-sm btn-warning'><i class='bi bi-pencil-square'></i> Edit</a> ");
                    out.println("<a href='mahasiswa?action=delete&nim=" + row[0] + "' class='btn btn-sm btn-danger' onclick='return confirm(\"Yakin ingin menghapus? Semua nilai yang terhubung akan ikut terhapus.\")'><i class='bi bi-trash-fill'></i> Hapus</a></td>");
                    out.println("</tr>");
                }
            }
            out.println("</tbody></table></div></div></div>");

            // Panggil Footer
            Layout.printFooter(out);
        }
    }
}