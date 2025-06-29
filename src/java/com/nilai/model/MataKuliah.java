package com.nilai.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MataKuliah {
    private String kodeMk;
    private String namaMk;
    private int sks;
    private String pesan;
    private final ArrayList<Object[]> list = new ArrayList<>();
    private final Koneksi koneksi = new Koneksi();

    // Getter dan Setter
    public String getKodeMk() { return kodeMk; }
    public void setKodeMk(String kodeMk) { this.kodeMk = kodeMk; }
    public String getNamaMk() { return namaMk; }
    public void setNamaMk(String namaMk) { this.namaMk = namaMk; }
    public int getSks() { return sks; }
    public void setSks(int sks) { this.sks = sks; }
    public String getPesan() { return pesan; }
    public Object[][] getList() { return list.toArray(new Object[0][]); }
    
    public boolean simpan() {
        String sql = "INSERT INTO t_matakuliah (kode_mk, nama_mk, sks) VALUES (?, ?, ?)";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.kodeMk);
            stmt.setString(2, this.namaMk);
            stmt.setInt(3, this.sks);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23000")) { 
                pesan = "Gagal menyimpan: Kode MK '" + this.kodeMk + "' sudah ada.";
            } else {
                pesan = "Gagal menyimpan data: " + ex.getMessage();
            }
            return false;
        }
    }

    public boolean baca(String kodeMk) {
        String sql = "SELECT * FROM t_matakuliah WHERE kode_mk = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kodeMk);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.kodeMk = rs.getString("kode_mk");
                this.namaMk = rs.getString("nama_mk");
                this.sks = rs.getInt("sks");
                return true;
            }
        } catch (SQLException ex) {
            pesan = "Gagal membaca data: " + ex.getMessage();
        }
        return false;
    }
    
    public boolean update(String kodeMKLama) {
        String sql = "UPDATE t_matakuliah SET kode_mk=?, nama_mk=?, sks=? WHERE kode_mk=?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.kodeMk);
            stmt.setString(2, this.namaMk);
            stmt.setInt(3, this.sks);
            stmt.setString(4, kodeMKLama);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesan = "Gagal mengupdate data: " + ex.getMessage();
            return false;
        }
    }

    public boolean bacaData() {
        list.clear();
        String sql = "SELECT * FROM t_matakuliah ORDER BY nama_mk";
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("kode_mk");
                row[1] = rs.getString("nama_mk");
                row[2] = rs.getInt("sks");
                list.add(row);
            }
            return true;
        } catch (SQLException ex) {
            pesan = "Gagal memuat data: " + ex.getMessage();
            return false;
        }
    }

    public boolean hapus(String kodeMk) {
        String sql = "DELETE FROM t_matakuliah WHERE kode_mk = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kodeMk);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesan = "Gagal menghapus data: " + ex.getMessage();
            return false;
        }
    }
}