package com.nilai.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Warudo
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    // Sesuaikan URL, USER, dan PASSWORD dengan konfigurasi database Anda.
    private static final String URL = "jdbc:mysql://localhost:3306/db_nilai_mahasiswa";
    private static final String USER = "root"; // Ganti jika user database Anda berbeda
    private static final String PASSWORD = ""; // Ganti dengan password database Anda

    public Connection getConnection() {
        Connection connection = null;
        try {
            // Menggunakan driver baru untuk MySQL 8+
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi ke database GAGAL: " + e.getMessage());
        }
        return connection;
    }
}
