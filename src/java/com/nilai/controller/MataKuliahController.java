package com.nilai.controller;

import com.nilai.model.MataKuliah;
import com.nilai.view.Layout; // Import kelas Layout
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MataKuliahController", urlPatterns = {"/matakuliah"})
public class MataKuliahController extends HttpServlet {
    private final MataKuliah matkulModel = new MataKuliah();

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
        String kodeMKLama = request.getParameter("kodeMKLama");
        
        matkulModel.setKodeMk(request.getParameter("kode_mk"));
        matkulModel.setNamaMk(request.getParameter("nama_mk"));
        matkulModel.setSks(Integer.parseInt(request.getParameter("sks")));

        boolean sukses;
        if (kodeMKLama != null && !kodeMKLama.isEmpty()) {
            sukses = matkulModel.update(kodeMKLama);
        } else {
            sukses = matkulModel.simpan();
        }
        
        if (sukses) {
            request.getSession().setAttribute("message", "Data mata kuliah berhasil disimpan!");
        } else {
            request.getSession().setAttribute("error", matkulModel.getPesan());
        }
        response.sendRedirect("matakuliah");
    }
    
    private void handleEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String kodeMk = request.getParameter("kode_mk");
        MataKuliah mkEdit = new MataKuliah();
        if(mkEdit.baca(kodeMk)){
            tampilkanHalaman(request, response, mkEdit);
        } else {
             request.getSession().setAttribute("error", "Data tidak ditemukan!");
             response.sendRedirect("matakuliah");
        }
    }
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
         String kodeMk = request.getParameter("kode_mk");
         if(matkulModel.hapus(kodeMk)){
             request.getSession().setAttribute("message", "Data berhasil dihapus!");
         } else {
             request.getSession().setAttribute("error", "Gagal menghapus: " + matkulModel.getPesan());
         }
         response.sendRedirect("matakuliah");
    }

    private void tampilkanHalaman(HttpServletRequest request, HttpServletResponse response, MataKuliah mkEdit) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            
            // Panggil Header
            Layout.printHeader(out, session, "Kelola Mata Kuliah");

            out.println("<h1><i class='bi bi-book-half'></i> Kelola Data Mata Kuliah</h1>");

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
            out.println("  <div class='card-header'>" + (mkEdit != null ? "Edit" : "Tambah") + " Mata Kuliah</div>");
            out.println("  <div class='card-body'>");
            out.println("    <form method='post' action='matakuliah'>");
            if (mkEdit != null) {
                out.println("      <input type='hidden' name='kodeMKLama' value='" + mkEdit.getKodeMk() + "'>");
            }
            out.println("      <div class='mb-3'><label for='kode_mk' class='form-label'>Kode MK</label><input type='text' class='form-control' id='kode_mk' name='kode_mk' value='" + (mkEdit != null ? mkEdit.getKodeMk() : "") + "' required></div>");
            out.println("      <div class='mb-3'><label for='nama_mk' class='form-label'>Nama Mata Kuliah</label><input type='text' class='form-control' id='nama_mk' name='nama_mk' value='" + (mkEdit != null ? mkEdit.getNamaMk() : "") + "' required></div>");
            out.println("      <div class='mb-3'><label for='sks' class='form-label'>SKS</label><input type='number' class='form-control' id='sks' name='sks' value='" + (mkEdit != null ? mkEdit.getSks() : 2) + "' required></div>");
            out.println("      <button type='submit' class='btn btn-primary'>" + (mkEdit != null ? "Update Data" : "Simpan") + "</button>");
            if (mkEdit != null) {
                out.println("      <a href='matakuliah' class='btn btn-secondary ms-2'>Batal</a>");
            }
            out.println("    </form>");
            out.println("  </div>");
            out.println("</div>");

            // Gunakan Card untuk Tabel
            out.println("<div class='card'>");
            out.println("  <div class='card-header'>Daftar Mata Kuliah</div>");
            out.println("  <div class='card-body'>");
            out.println("    <div class='table-responsive'>");
            out.println("      <table class='table table-striped table-hover'>");
            out.println("        <thead><tr><th>Kode MK</th><th>Nama Mata Kuliah</th><th>SKS</th><th>Aksi</th></tr></thead><tbody>");
            if (matkulModel.bacaData()) {
                for (Object[] row : matkulModel.getList()) {
                    out.println("<tr>");
                    out.println("<td>" + row[0] + "</td><td>" + row[1] + "</td><td>" + row[2] + "</td>");
                    out.println("<td><a href='matakuliah?action=edit&kode_mk=" + row[0] + "' class='btn btn-sm btn-warning'><i class='bi bi-pencil-square'></i> Edit</a> ");
                    out.println("<a href='matakuliah?action=delete&kode_mk=" + row[0] + "' class='btn btn-sm btn-danger' onclick='return confirm(\"Yakin ingin menghapus? Semua nilai yang terhubung akan ikut terhapus.\")'><i class='bi bi-trash-fill'></i> Hapus</a></td>");
                    out.println("</tr>");
                }
            }
            out.println("</tbody></table></div></div></div>");

            // Panggil Footer
            Layout.printFooter(out);
        }
    }
}