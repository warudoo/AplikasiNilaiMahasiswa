package com.nilai.controller;

import com.nilai.model.Koneksi;
import com.nilai.model.Nilai;
import com.nilai.model.User;
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
            request.getSession().setAttribute("message", "Data berhasil disimpan!");
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
             request.getSession().setAttribute("message", "Data berhasil dihapus!");
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

            out.println("<!DOCTYPE html><html><head><title>CRUD Input Nilai</title>");
            out.println("<link rel='stylesheet' href='https://unpkg.com/simpledotcss/simple.min.css'>");
            out.println("<style>");
            out.println("body { max-width: 900px; margin: 2rem auto; }");
            out.println(".alert { padding: 1rem; margin-bottom: 1rem; border-radius: 5px; }");
            out.println(".alert-success { background-color: #d4edda; color: #155724; border:1px solid #c3e6cb;}");
            out.println(".alert-error { background-color: #f8d7da; color: #721c24; border:1px solid #f5c6cb;}");
            out.println("header { display:flex; justify-content:space-between; align-items:center; }");
            out.println("</style></head><body>");
            
            out.println("<header><h1>Aplikasi Input Nilai</h1>");
            User user = (User) session.getAttribute("user");
            if (user != null) {
                out.println("<p>Halo, <strong>" + user.getNamaLengkap() + "</strong> | <a href='logout'>Logout</a></p>");
            }
            out.println("</header>");

            if (session.getAttribute("message") != null) {
                out.println("<div class='alert alert-success'>" + session.getAttribute("message") + "</div>");
                session.removeAttribute("message");
            }
            if (session.getAttribute("error") != null) {
                out.println("<div class='alert alert-error'>" + session.getAttribute("error") + "</div>");
                session.removeAttribute("error");
            }

            out.println("<h2>" + (nilaiEdit != null ? "Edit" : "Tambah") + " Nilai</h2>");
            out.println("<form method='post' action='nilai'>");
            if (nilaiEdit != null) {
                out.println("<input type='hidden' name='idNilai' value='" + nilaiEdit.getIdNilai() + "'>");
            }
            
            out.println("<p><label>Mahasiswa</label><select name='nim' required>");
            for (Map.Entry<String, String> entry : mahasiswaList.entrySet()) {
                String selected = (nilaiEdit != null && entry.getKey().equals(nilaiEdit.getNimMahasiswa())) ? "selected" : "";
                out.println("<option value='" + entry.getKey() + "' " + selected + ">" + entry.getValue() + "</option>");
            }
            out.println("</select></p>");
            
            out.println("<p><label>Mata Kuliah</label><select name='kode_mk' required>");
            for (Map.Entry<String, String> entry : matakuliahList.entrySet()) {
                String selected = (nilaiEdit != null && entry.getKey().equals(nilaiEdit.getKodeMatakuliah())) ? "selected" : "";
                out.println("<option value='" + entry.getKey() + "' " + selected + ">" + entry.getValue() + "</option>");
            }
            out.println("</select></p>");

            out.println("<p><label>Nilai Tugas</label><input type='number' name='tugas' min='0' max='100' value='" + (nilaiEdit != null ? nilaiEdit.getNilaiTugas() : 0) + "' required></p>");
            out.println("<p><label>Nilai UTS</label><input type='number' name='uts' min='0' max='100' value='" + (nilaiEdit != null ? nilaiEdit.getNilaiUts() : 0) + "' required></p>");
            out.println("<p><label>Nilai UAS</label><input type='number' name='uas' min='0' max='100' value='" + (nilaiEdit != null ? nilaiEdit.getNilaiUas() : 0) + "' required></p>");
            out.println("<button type='submit'>" + (nilaiEdit != null ? "Update" : "Simpan") + "</button>");
            if (nilaiEdit != null) {
                out.println("<a href='nilai' style='margin-left: 1rem;'><button type='button' class='secondary'>Batal</button></a>");
            }
            out.println("</form>");

            out.println("<h2>Daftar Nilai</h2><table>");
            out.println("<thead><tr><th>NIM</th><th>Nama</th><th>Mata Kuliah</th><th>Tugas</th><th>UTS</th><th>UAS</th><th>Nilai Akhir</th><th>Aksi</th></tr></thead><tbody>");
            if (nilaiModel.bacaData()) {
                for (Object[] row : nilaiModel.getList()) {
                    out.println("<tr>");
                    out.println("<td>" + row[1] + "</td><td>" + row[2] + "</td><td>" + row[4] + "</td>");
                    out.println("<td>" + row[5] + "</td><td>" + row[6] + "</td><td>" + row[7] + "</td>");
                    out.println("<td>" + String.format("%.2f", row[8]) + "</td>");
                    out.println("<td><a href='nilai?action=edit&id=" + row[0] + "'>Edit</a> | ");
                    out.println("<a href='nilai?action=delete&id=" + row[0] + "' onclick='return confirm(\"Yakin ingin menghapus data ini?\")'>Hapus</a></td>");
                    out.println("</tr>");
                }
            }
            out.println("</tbody></table></body></html>");
        }
    }

    private Map<String, String> getDropdownData(String query) {
        Map<String, String> data = new LinkedHashMap<>(); // Pakai LinkedHashMap agar urutan terjaga
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