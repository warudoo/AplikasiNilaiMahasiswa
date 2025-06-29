package com.nilai.controller;

import com.nilai.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect("mahasiswa");
            return;
        }
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            out.println("<!DOCTYPE html><html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Login - Aplikasi Nilai</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<style>");
            out.println("body { display: flex; align-items: center; justify-content: center; height: 100vh; background-color: #f8f9fa; }");
            out.println(".login-card { max-width: 400px; width: 100%; }");
            out.println("</style>");
            out.println("</head><body>");
            
            out.println("<div class='card login-card shadow-sm'>");
            out.println("  <div class='card-body p-5'>");
            out.println("    <h3 class='card-title text-center mb-4'>Selamat Datang</h3>");
            
            if (session.getAttribute("loginError") != null) {
                out.println("<div class='alert alert-danger'>" + session.getAttribute("loginError") + "</div>");
                session.removeAttribute("loginError");
            }
            
            out.println("<form method='post' action='login'>");
            out.println("  <div class='mb-3'>");
            out.println("    <label for='username' class='form-label'>Username</label>");
            out.println("    <input type='text' class='form-control' id='username' name='username' required autofocus>");
            out.println("  </div>");
            out.println("  <div class='mb-3'>");
            out.println("    <label for='password' class='form-label'>Password</label>");
            out.println("    <input type='password' class='form-control' id='password' name='password' required>");
            out.println("  </div>");
            out.println("  <div class='d-grid'>");
            out.println("    <button type='submit' class='btn btn-primary'>Login</button>");
            out.println("  </div>");
            out.println("</form>");
            out.println("  </div>");
            out.println("</div>");

            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User userModel = new User();
        userModel.setUsername(request.getParameter("username"));
        userModel.setPassword(request.getParameter("password"));

        HttpSession session = request.getSession();
        if (userModel.login()) {
            session.setAttribute("user", userModel);
            response.sendRedirect("mahasiswa"); 
        } else {
            session.setAttribute("loginError", userModel.getPesan());
            response.sendRedirect("login");
        }
    }
}