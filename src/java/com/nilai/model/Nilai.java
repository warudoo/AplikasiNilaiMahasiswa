/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nilai.model;

/**
 *
 * @author Warudo
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Model utama untuk operasi CRUD (Create, Read, Update, Delete) pada data nilai.
 */
public class Nilai {
    private int idNilai;
    private String nimMahasiswa;
    private String kodeMatakuliah;
    private int nilaiTugas;
    private int nilaiUts;
    private int nilaiUas;
    private double nilaiAkhir;
    private String pesan;
    private final ArrayList<Object[]> list = new ArrayList<>();
    private final Koneksi koneksi = new Koneksi();

    // Getter dan Setter
    public int getIdNilai() { return idNilai; }
    public void setIdNilai(int idNilai) { this.idNilai = idNilai; }
    public String getNimMahasiswa() { return nimMahasiswa; }
    public void setNimMahasiswa(String nimMahasiswa) { this.nimMahasiswa = nimMahasiswa; }
    public String getKodeMatakuliah() { return kodeMatakuliah; }
    public void setKodeMatakuliah(String kodeMatakuliah) { this.kodeMatakuliah = kodeMatakuliah; }
    public int getNilaiTugas() { return nilaiTugas; }
    public void setNilaiTugas(int nilaiTugas) { this.nilaiTugas = nilaiTugas; }
    public int getNilaiUts() { return nilaiUts; }
    public void setNilaiUts(int nilaiUts) { this.nilaiUts = nilaiUts; }
    public int getNilaiUas() { return nilaiUas; }
    public void setNilaiUas(int nilaiUas) { this.nilaiUas = nilaiUas; }
    public double getNilaiAkhir() { return nilaiAkhir; }
    public String getPesan() { return pesan; }
    public Object[][] getList() { return list.toArray(new Object[0][]); }

    private void hitungNilaiAkhir() {
        // Bobot: Tugas 20%, UTS 30%, UAS 50%
        this.nilaiAkhir = (this.nilaiTugas * 0.2) + (this.nilaiUts * 0.3) + (this.nilaiUas * 0.5);
    }

    public boolean simpanAtauUpdate() {
        hitungNilaiAkhir();
        // Cek dulu apakah data dengan nim dan matkul yang sama sudah ada (untuk menghindari duplikasi)
        String cekSql = "SELECT id_nilai FROM t_nilai WHERE nim_mahasiswa = ? AND kode_matakuliah = ?";
        if (this.idNilai > 0) { // Jika sedang mengedit, abaikan ID yang sama
             cekSql += " AND id_nilai != " + this.idNilai;
        }

        try (Connection conn = koneksi.getConnection()) {
            try (PreparedStatement cekStmt = conn.prepareStatement(cekSql)) {
                cekStmt.setString(1, nimMahasiswa);
                cekStmt.setString(2, kodeMatakuliah);
                ResultSet rs = cekStmt.executeQuery();
                if (rs.next()) {
                    pesan = "Gagal: Data nilai untuk mahasiswa dan mata kuliah ini sudah ada.";
                    return false;
                }
            }

            String sql;
            if (idNilai > 0) {
                sql = "UPDATE t_nilai SET nim_mahasiswa=?, kode_matakuliah=?, nilai_tugas=?, nilai_uts=?, nilai_uas=?, nilai_akhir=? WHERE id_nilai=?";
            } else {
                sql = "INSERT INTO t_nilai (nim_mahasiswa, kode_matakuliah, nilai_tugas, nilai_uts, nilai_uas, nilai_akhir) VALUES (?, ?, ?, ?, ?, ?)";
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nimMahasiswa);
                stmt.setString(2, kodeMatakuliah);
                stmt.setInt(3, nilaiTugas);
                stmt.setInt(4, nilaiUts);
                stmt.setInt(5, nilaiUas);
                stmt.setDouble(6, nilaiAkhir);
                if (idNilai > 0) {
                    stmt.setInt(7, idNilai);
                }
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            pesan = "Gagal menyimpan data: " + ex.getMessage();
            return false;
        }
    }

    public boolean baca(int id) {
        String sql = "SELECT * FROM t_nilai WHERE id_nilai = ?";
        try (Connection conn = koneksi.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.idNilai = rs.getInt("id_nilai");
                this.nimMahasiswa = rs.getString("nim_mahasiswa");
                this.kodeMatakuliah = rs.getString("kode_matakuliah");
                this.nilaiTugas = rs.getInt("nilai_tugas");
                this.nilaiUts = rs.getInt("nilai_uts");
                this.nilaiUas = rs.getInt("nilai_uas");
                this.nilaiAkhir = rs.getDouble("nilai_akhir");
                return true;
            }
        } catch (SQLException ex) {
            pesan = "Gagal membaca data: " + ex.getMessage();
        }
        return false;
    }

    public boolean bacaData() {
        list.clear();
        String sql = "SELECT n.id_nilai, n.nim_mahasiswa, m.nama, n.kode_matakuliah, mk.nama_mk, " +
                     "n.nilai_tugas, n.nilai_uts, n.nilai_uas, n.nilai_akhir " +
                     "FROM t_nilai n " +
                     "JOIN t_mahasiswa m ON n.nim_mahasiswa = m.nim " +
                     "JOIN t_matakuliah mk ON n.kode_matakuliah = mk.kode_mk " +
                     "ORDER BY m.nama, mk.nama_mk";
        try (Connection conn = koneksi.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = new Object[9];
                row[0] = rs.getInt("id_nilai");
                row[1] = rs.getString("nim_mahasiswa");
                row[2] = rs.getString("nama");
                row[3] = rs.getString("kode_matakuliah");
                row[4] = rs.getString("nama_mk");
                row[5] = rs.getInt("nilai_tugas");
                row[6] = rs.getInt("nilai_uts");
                row[7] = rs.getInt("nilai_uas");
                row[8] = rs.getDouble("nilai_akhir");
                list.add(row);
            }
            return true;
        } catch (SQLException ex) {
            pesan = "Gagal memuat data: " + ex.getMessage();
            return false;
        }
    }

    public boolean hapus(int id) {
        String sql = "DELETE FROM t_nilai WHERE id_nilai = ?";
        try (Connection conn = koneksi.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesan = "Gagal menghapus data: " + ex.getMessage();
            return false;
        }
    }
}