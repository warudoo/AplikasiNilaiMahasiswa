package com.nilai.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class User {
    private int idUser; // Tambahan
    private String username;
    private String password;
    private String namaLengkap;
    private String pesan;
    private final ArrayList<Object[]> list = new ArrayList<>(); // Tambahan
    private final Koneksi koneksi = new Koneksi();

    // Getter dan Setter (dengan tambahan idUser)
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getPesan() { return pesan; }
    public Object[][] getList() { return list.toArray(new Object[0][]); }


    public boolean login() {
        String sql = "SELECT id_user, nama_lengkap FROM t_users WHERE username = ? AND password = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, this.username);
            stmt.setString(2, Enkripsi.sha256(this.password));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.idUser = rs.getInt("id_user"); // Simpan ID user saat login
                this.namaLengkap = rs.getString("nama_lengkap");
                return true;
            } else {
                pesan = "Username atau Password salah!";
                return false;
            }
        } catch (SQLException ex) {
            pesan = "Terjadi error pada database: " + ex.getMessage();
            return false;
        }
    }

    // --- METODE CRUD BARU ---

    public boolean simpan() {
        if (password == null || password.isEmpty()) {
            pesan = "Password tidak boleh kosong.";
            return false;
        }
        String sql = "INSERT INTO t_users (username, password, nama_lengkap) VALUES (?, ?, ?)";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.username);
            stmt.setString(2, Enkripsi.sha256(this.password)); // Enkripsi password baru
            stmt.setString(3, this.namaLengkap);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23000")) { 
                pesan = "Gagal menyimpan: Username '" + this.username + "' sudah digunakan.";
            } else {
                pesan = "Gagal menyimpan data: " + ex.getMessage();
            }
            return false;
        }
    }
    
    public boolean baca(int id) {
        String sql = "SELECT * FROM t_users WHERE id_user = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.idUser = rs.getInt("id_user");
                this.username = rs.getString("username");
                this.namaLengkap = rs.getString("nama_lengkap");
                // Password tidak diambil untuk keamanan
                return true;
            }
        } catch (SQLException ex) {
            pesan = "Gagal membaca data: " + ex.getMessage();
        }
        return false;
    }

    public boolean update() {
        String sql;
        // Jika password diisi, update password. Jika tidak, jangan update password.
        if (password != null && !password.isEmpty()) {
            sql = "UPDATE t_users SET username=?, nama_lengkap=?, password=? WHERE id_user=?";
        } else {
            sql = "UPDATE t_users SET username=?, nama_lengkap=? WHERE id_user=?";
        }
        
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.username);
            stmt.setString(2, this.namaLengkap);
            if (password != null && !password.isEmpty()) {
                stmt.setString(3, Enkripsi.sha256(this.password));
                stmt.setInt(4, this.idUser);
            } else {
                stmt.setInt(3, this.idUser);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesan = "Gagal mengupdate data: " + ex.getMessage();
            return false;
        }
    }
    
    public boolean bacaData() {
        list.clear();
        String sql = "SELECT id_user, username, nama_lengkap FROM t_users ORDER BY nama_lengkap";
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getInt("id_user");
                row[1] = rs.getString("username");
                row[2] = rs.getString("nama_lengkap");
                list.add(row);
            }
            return true;
        } catch (SQLException ex) {
            pesan = "Gagal memuat data: " + ex.getMessage();
            return false;
        }
    }

    public boolean hapus(int id) {
        String sql = "DELETE FROM t_users WHERE id_user = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesan = "Gagal menghapus data: " + ex.getMessage();
            return false;
        }
    }
}