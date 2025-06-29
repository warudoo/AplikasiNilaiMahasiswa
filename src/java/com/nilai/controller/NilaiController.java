package com.nilai.controller;

import com.nilai.model.Koneksi;
import com.nilai.model.Nilai;
import com.nilai.view.Layout; // Import kelas Layout
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "NilaiController", urlPatterns = {"/nilai"})
public class NilaiController extends HttpServlet {
    private final Nilai nilaiModel = new Nilai();
    private final Koneksi koneksi = new Koneksi();

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
        String idNilaiStr = request.getParameter("idNilai");
        
        nilaiModel.setNimMahasiswa(request.getParameter("nim"));
        nilaiModel.setKodeMatakuliah(request.getParameter("kode_mk"));
        nilaiModel.setNilaiTugas(Integer.parseInt(request.getParameter("tugas")));
        nilaiModel.setNilaiUts(Integer.parseInt(request.getParameter("uts")));
        nilaiModel.setNilaiUas(Integer.parseInt(request.getParameter("uas")));
        
        if (idNilaiStr != null && !idNilaiStr.isEmpty()) {
            nilaiModel.setIdNilai(Integer.parseInt(idNilaiStr));
        } else {
            nilaiModel.setIdNilai(0);
        }

        if (nilaiModel.simpanAtauUpdate()) {
            request.getSession().setAttribute("message", "Data nilai berhasil disimpan!");
        } else {
            request.getSession().setAttribute("error", nilaiModel.getPesan());
        }
        response.sendRedirect("nilai");
    }
    
    private void handleEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        Nilai nilaiEdit = new Nilai();
        if(nilaiEdit.baca(id)){
            tampilkanHalaman(request, response, nilaiEdit);
        } else {
             request.getSession().setAttribute("error", "Data tidak ditemukan!");
             response.sendRedirect("nilai");
        }
    }
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
         int id = Integer.parseInt(request.getParameter("id"));
         if(nilaiModel.hapus(id)){
             request.getSession().setAttribute("message", "Data nilai berhasil dihapus!");
         } else {
             request.getSession().setAttribute("error", "Gagal menghapus: " + nilaiModel.getPesan());
         }
         response.sendRedirect("nilai");
    }

    private void tampilkanHalaman(HttpServletRequest request, HttpServletResponse response, Nilai nilaiEdit) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();

            Map<String, String> mahasiswaList = getDropdownData("SELECT nim, nama FROM t_mahasiswa ORDER BY nama");
            Map<String, String> matakuliahList = getDropdownData("SELECT kode_mk, nama_mk FROM t_matakuliah ORDER BY nama_mk");
            
            // Panggil Header
            Layout.printHeader(out, session, "Input Nilai");

            out.println("<h1><i class='bi bi-pencil-fill'></i> Input Nilai Mahasiswa</h1>");

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
            out.println("  <div class='card-header'>" + (nilaiEdit != null ? "Edit" : "Tambah") + " Nilai</div>");
            out.println("  <div class='card-body'>");
            out.println("    <form method='post' action='nilai'>");
            if (nilaiEdit != null) {
                out.println("      <input type='hidden' name='idNilai' value='" + nilaiEdit.getIdNilai() + "'>");
            }
            out.println("      <div class='mb-3'><label for='nim' class='form-label'>Mahasiswa</label><select class='form-select' id='nim' name='nim' required>");
            for (Map.Entry<String, String> entry : mahasiswaList.entrySet()) {
                String selected = (nilaiEdit != null && entry.getKey().equals(nilaiEdit.getNimMahasiswa())) ? "selected" : "";
                out.println("<option value='" + entry.getKey() + "' " + selected + ">" + entry.getValue() + "</option>");
            }
            out.println("      </select></div>");
            out.println("      <div class='mb-3'><label for='kode_mk' class='form-label'>Mata Kuliah</label><select class='form-select' id='kode_mk' name='kode_mk' required>");
            for (Map.Entry<String, String> entry : matakuliahList.entrySet()) {
                String selected = (nilaiEdit != null && entry.getKey().equals(nilaiEdit.getKodeMatakuliah())) ? "selected" : "";
                out.println("<option value='" + entry.getKey() + "' " + selected + ">" + entry.getValue() + "</option>");
            }
            out.println("      </select></div>");
            
            // Form nilai dalam row dan column
            out.println("      <div class='row'>");
            out.println("        <div class='col-md-4 mb-3'><label for='tugas' class='form-label'>Nilai Tugas</label><input type='number' class='form-control' id='tugas' name='tugas' min='0' max='100' value='" + (nilaiEdit != null ? nilaiEdit.getNilaiTugas() : 0) + "' required></div>");
            out.println("        <div class='col-md-4 mb-3'><label for='uts' class='form-label'>Nilai UTS</label><input type='number' class='form-control' id='uts' name='uts' min='0' max='100' value='" + (nilaiEdit != null ? nilaiEdit.getNilaiUts() : 0) + "' required></div>");
            out.println("        <div class='col-md-4 mb-3'><label for='uas' class='form-label'>Nilai UAS</label><input type='number' class='form-control' id='uas' name='uas' min='0' max='100' value='" + (nilaiEdit != null ? nilaiEdit.getNilaiUas() : 0) + "' required></div>");
            out.println("      </div>");

            out.println("      <button type='submit' class='btn btn-primary'>" + (nilaiEdit != null ? "Update Data" : "Simpan") + "</button>");
            if (nilaiEdit != null) {
                out.println("      <a href='nilai' class='btn btn-secondary ms-2'>Batal</a>");
            }
            out.println("    </form>");
            out.println("  </div>");
            out.println("</div>");
            
            // Gunakan Card untuk Tabel
            out.println("<div class='card'>");
            out.println("  <div class='card-header'>Daftar Nilai</div>");
            out.println("  <div class='card-body'>");
            out.println("    <div class='table-responsive'>");
            out.println("      <table class='table table-striped table-hover'>");
            out.println("        <thead><tr><th>Nama Mahasiswa</th><th>Mata Kuliah</th><th>Tugas</th><th>UTS</th><th>UAS</th><th>Nilai Akhir</th><th>Aksi</th></tr></thead><tbody>");
            if (nilaiModel.bacaData()) {
                for (Object[] row : nilaiModel.getList()) {
                    out.println("<tr>");
                    out.println("<td>" + row[2] + "</td><td>" + row[4] + "</td>");
                    out.println("<td>" + row[5] + "</td><td>" + row[6] + "</td><td>" + row[7] + "</td>");
                    out.println("<td><strong>" + String.format("%.2f", row[8]) + "</strong></td>");
                    out.println("<td><a href='nilai?action=edit&id=" + row[0] + "' class='btn btn-sm btn-warning'><i class='bi bi-pencil-square'></i> Edit</a> ");
                    out.println("<a href='nilai?action=delete&id=" + row[0] + "' class='btn btn-sm btn-danger' onclick='return confirm(\"Yakin ingin menghapus data ini?\")'><i class='bi bi-trash-fill'></i> Hapus</a></td>");
                    out.println("</tr>");
                }
            }
            out.println("</tbody></table></div></div></div>");

            // Panggil Footer
            Layout.printFooter(out);
        }
    }

    private Map<String, String> getDropdownData(String query) {
        Map<String, String> data = new LinkedHashMap<>();
        try (Connection conn = koneksi.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.put(rs.getString(1), rs.getString(1) + " - " + rs.getString(2));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data dropdown: " + e.getMessage());
        }
        return data;
    }
}