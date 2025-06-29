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
            response.sendRedirect("nilai");
            return;
        }
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Login</title>");
            out.println("<link rel='stylesheet' href='https://unpkg.com/simpledotcss/simple.min.css'>");
            out.println("<style>");
            out.println("body { display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f4; }");
            out.println("article { min-width: 350px; background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }");
            out.println(".alert-error { background-color: #f8d7da; color: #721c24; padding: 1rem; border-radius: 5px; margin-bottom:1rem; border: 1px solid #f5c6cb; }");
            out.println("</style>");
            out.println("</head><body><article>");
            out.println("<h2>Login Aplikasi</h2>");
            
            if (session.getAttribute("loginError") != null) {
                out.println("<div class='alert-error'>" + session.getAttribute("loginError") + "</div>");
                session.removeAttribute("loginError");
            }
            
            out.println("<form method='post' action='login'>");
            out.println("<p><label>Username</label><input type='text' name='username' required autofocus></p>");
            out.println("<p><label>Password</label><input type='password' name='password' required></p>");
            out.println("<button type='submit'>Login</button>");
            out.println("</form></article></body></html>");
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
            response.sendRedirect("nilai");
        } else {
            session.setAttribute("loginError", userModel.getPesan());
            response.sendRedirect("login");
        }
    }
}