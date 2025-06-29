package com.nilai.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Mahasiswa {
    private String nim;
    private String nama;
    private String jurusan;
    private String pesan;
    private final ArrayList<Object[]> list = new ArrayList<>();
    private final Koneksi koneksi = new Koneksi();

    // Getter dan Setter
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
    public String getPesan() { return pesan; }
    public Object[][] getList() { return list.toArray(new Object[0][]); }

    public boolean simpan() {
        String sql = "INSERT INTO t_mahasiswa (nim, nama, jurusan) VALUES (?, ?, ?)";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nim);
            stmt.setString(2, this.nama);
            stmt.setString(3, this.jurusan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            // Cek jika error karena duplikasi primary key
            if (ex.getSQLState().equals("23000")) { 
                pesan = "Gagal menyimpan: NIM '" + this.nim + "' sudah ada.";
            } else {
                pesan = "Gagal menyimpan data: " + ex.getMessage();
            }
            return false;
        }
    }

    public boolean baca(String nim) {
        String sql = "SELECT * FROM t_mahasiswa WHERE nim = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nim);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.nim = rs.getString("nim");
                this.nama = rs.getString("nama");
                this.jurusan = rs.getString("jurusan");
                return true;
            }
        } catch (SQLException ex) {
            pesan = "Gagal membaca data: " + ex.getMessage();
        }
        return false;
    }

    public boolean update(String nimLama) {
        String sql = "UPDATE t_mahasiswa SET nim=?, nama=?, jurusan=? WHERE nim=?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nim);
            stmt.setString(2, this.nama);
            stmt.setString(3, this.jurusan);
            stmt.setString(4, nimLama);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesan = "Gagal mengupdate data: " + ex.getMessage();
            return false;
        }
    }
    
    public boolean bacaData() {
        list.clear();
        String sql = "SELECT * FROM t_mahasiswa ORDER BY nama";
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("nim");
                row[1] = rs.getString("nama");
                row[2] = rs.getString("jurusan");
                list.add(row);
            }
            return true;
        } catch (SQLException ex) {
            pesan = "Gagal memuat data: " + ex.getMessage();
            return false;
        }
    }

    public boolean hapus(String nim) {
        String sql = "DELETE FROM t_mahasiswa WHERE nim = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nim);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesan = "Gagal menghapus data: " + ex.getMessage();
            return false;
        }
    }
}