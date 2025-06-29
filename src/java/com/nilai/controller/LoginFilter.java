package com.nilai.controller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter ini akan mencegat semua permintaan ke halaman yang dilindungi (dalam hal ini "/nilai")
 * dan memeriksa apakah pengguna sudah login atau belum.
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = {"/nilai"})
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); // Ambil session yang ada, jangan buat baru

        // Jika tidak ada session ATAU atribut "user" tidak ada di session, paksa ke halaman login
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
        } else {
            chain.doFilter(request, response); // Jika sudah login, izinkan akses ke tujuan
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}