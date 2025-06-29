package com.nilai.controller;

import com.nilai.model.User;
import com.nilai.view.Layout;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AdminController", urlPatterns = {"/admin"})
public class AdminController extends HttpServlet {
    private final User userModel = new User();
    
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
        String idUserStr = request.getParameter("idUser");
        
        userModel.setUsername(request.getParameter("username"));
        userModel.setNamaLengkap(request.getParameter("nama_lengkap"));
        userModel.setPassword(request.getParameter("password")); // Akan di-handle di model jika kosong

        boolean sukses;
        if (idUserStr != null && !idUserStr.isEmpty()) { // Mode Update
            userModel.setIdUser(Integer.parseInt(idUserStr));
            sukses = userModel.update();
        } else { // Mode Simpan
            sukses = userModel.simpan();
        }
        
        if (sukses) {
            request.getSession().setAttribute("message", "Data admin berhasil disimpan!");
        } else {
            request.getSession().setAttribute("error", userModel.getPesan());
        }
        response.sendRedirect("admin");
    }
    
    private void handleEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        User userEdit = new User();
        if(userEdit.baca(id)){
            tampilkanHalaman(request, response, userEdit);
        } else {
             request.getSession().setAttribute("error", "Data tidak ditemukan!");
             response.sendRedirect("admin");
        }
    }
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
         int idHapus = Integer.parseInt(request.getParameter("id"));
         HttpSession session = request.getSession();
         User userLogin = (User) session.getAttribute("user");

         // Fitur Keamanan: Jangan biarkan user menghapus akunnya sendiri
         if (userLogin != null && userLogin.getIdUser() == idHapus) {
             session.setAttribute("error", "Anda tidak dapat menghapus akun Anda sendiri!");
         } else {
             if(userModel.hapus(idHapus)){
                 session.setAttribute("message", "Data admin berhasil dihapus!");
             } else {
                 session.setAttribute("error", "Gagal menghapus: " + userModel.getPesan());
             }
         }
         response.sendRedirect("admin");
    }

    private void tampilkanHalaman(HttpServletRequest request, HttpServletResponse response, User userEdit) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            
            Layout.printHeader(out, session, "Kelola Admin");

            out.println("<h1><i class='bi bi-person-gear'></i> Kelola Data Admin</h1>");

            if (session.getAttribute("message") != null) {
                out.println("<div class='alert alert-success'>" + session.getAttribute("message") + "</div>");
                session.removeAttribute("message");
            }
            if (session.getAttribute("error") != null) {
                out.println("<div class='alert alert-danger'>" + session.getAttribute("error") + "</div>");
                session.removeAttribute("error");
            }

            out.println("<div class='card mb-4'>");
            out.println("  <div class='card-header'>" + (userEdit != null ? "Edit" : "Tambah") + " Admin</div>");
            out.println("  <div class='card-body'>");
            out.println("    <form method='post' action='admin'>");
            if (userEdit != null) {
                out.println("      <input type='hidden' name='idUser' value='" + userEdit.getIdUser() + "'>");
            }
            out.println("      <div class='mb-3'><label for='nama_lengkap' class='form-label'>Nama Lengkap</label><input type='text' class='form-control' id='nama_lengkap' name='nama_lengkap' value='" + (userEdit != null ? userEdit.getNamaLengkap() : "") + "' required></div>");
            out.println("      <div class='mb-3'><label for='username' class='form-label'>Username</label><input type='text' class='form-control' id='username' name='username' value='" + (userEdit != null ? userEdit.getUsername() : "") + "' required></div>");
            out.println("      <div class='mb-3'><label for='password' class='form-label'>Password</label><input type='password' class='form-control' id='password' name='password' " + (userEdit == null ? "required" : "") + ">");
            if (userEdit != null) {
                out.println("<small class='form-text text-muted'>Kosongkan jika tidak ingin mengubah password.</small>");
            }
            out.println("      </div>");
            out.println("      <button type='submit' class='btn btn-primary'>" + (userEdit != null ? "Update Data" : "Simpan") + "</button>");
            if (userEdit != null) {
                out.println("      <a href='admin' class='btn btn-secondary ms-2'>Batal</a>");
            }
            out.println("    </form>");
            out.println("  </div>");
            out.println("</div>");
            
            out.println("<div class='card'>");
            out.println("  <div class='card-header'>Daftar Admin</div>");
            out.println("  <div class='card-body'>");
            out.println("    <div class='table-responsive'>");
            out.println("      <table class='table table-striped table-hover'>");
            out.println("        <thead><tr><th>ID</th><th>Nama Lengkap</th><th>Username</th><th>Aksi</th></tr></thead><tbody>");
            if (userModel.bacaData()) {
                for (Object[] row : userModel.getList()) {
                    out.println("<tr>");
                    out.println("<td>" + row[0] + "</td><td>" + row[2] + "</td><td>" + row[1] + "</td>");
                    out.println("<td><a href='admin?action=edit&id=" + row[0] + "' class='btn btn-sm btn-warning'><i class='bi bi-pencil-square'></i> Edit</a> ");
                    out.println("<a href='admin?action=delete&id=" + row[0] + "' class='btn btn-sm btn-danger' onclick='return confirm(\"Yakin ingin menghapus admin ini?\")'><i class='bi bi-trash-fill'></i> Hapus</a></td>");
                    out.println("</tr>");
                }
            }
            out.println("</tbody></table></div></div></div>");

            Layout.printFooter(out);
        }
    }
}