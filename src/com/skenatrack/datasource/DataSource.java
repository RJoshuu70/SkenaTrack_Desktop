package com.skenatrack.datasource;

import com.skenatrack.model.Place;
import com.skenatrack.model.PlaceCategory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DataSource — layer data terhubung ke SQLite.
 */
public class DataSource {

    private static final String DB_URL = "jdbc:sqlite:skenatrack.db";

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {

            // Buat tabel jika belum ada
            String createTableSQL = "CREATE TABLE IF NOT EXISTS places (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "category TEXT NOT NULL," +
                    "location TEXT," +
                    "rating REAL," +
                    "image_file_name TEXT," +
                    "map_url TEXT," +
                    "description TEXT" +
                    ")";
            stmt.execute(createTableSQL);

            // Cek apakah tabel kosong (baru dibuat)
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM places");
            if (rs.next() && rs.getInt("total") == 0) {
                seedData(conn);
            }
        } catch (Exception e) {
            System.err.println("Gagal inisialisasi database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedData(Connection conn) throws Exception {
        System.out.println("Memasukkan data awal (seeding) ke database SQLite...");
        String insertSQL = "INSERT INTO places (name, category, location, rating, image_file_name, map_url, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            Object[][] initialData = {
                    // === KATEGORI: CAFE ===
                    { "Ol' Pops Coffee Cinere", PlaceCategory.CAFE.name(), "Depok", 4.7f, "ol_pops_coffee_cinere.jpg",
                            "https://maps.app.goo.gl/M1XMEDqL2wvQ6AVV6",
                            "Kafe bernuansa homey nan asri bergaya klasik dengan area outdoor yang luas di Cinere." },
                    { "+62 Coffee & Space", PlaceCategory.CAFE.name(), "Depok", 4.4f, "62_coffee_space.jpg",
                            "https://maps.app.goo.gl/jQm35LxeTaMsg5Qo9",
                            "Kafe estetik di Depok dengan menu kopi susu premium dan dessert pilihan." },
                    { "Menanti Senja Cipete", PlaceCategory.CAFE.name(), "Jakarta Selatan", 4.6f,
                            "menanti_senja_cipete.jpg", "https://maps.app.goo.gl/bHAnjX2apngcoVo36",
                            "Kafe pencuci mulut viral yang terkenal dengan Poured Tiramisu dan kue estetiknya." },
                    { "Uma Kopi", PlaceCategory.CAFE.name(), "Jakarta Selatan", 4.5f, "uma_kopi.jpg",
                            "https://maps.app.goo.gl/DNcqqRCggtM3E58B8",
                            "Coffee shop mungil nan estetik bergaya rumahan dengan suasana tenang yang cocok untuk santai." },

                    // === KATEGORI: TAMAN ===
                    { "Taman Literasi Martha Christina Tiahahu", PlaceCategory.TAMAN.name(), "Jakarta Selatan", 4.7f,
                            "taman_literasi.jpg", "https://maps.app.goo.gl/3i8UtRasFRPxZH266",
                            "Taman baca modern di kawasan Blok M yang dilengkapi perpustakaan mini dan ruang komunal." },
                    { "Taman Ismail Marzuki", PlaceCategory.TAMAN.name(), "Jakarta Pusat", 4.5f,
                            "taman_ismail_marzuki.jpg", "https://maps.google.com/?q=Taman+Ismail+Marzuki+Jakarta",
                            "Pusat kebudayaan Jakarta dengan taman hijau, galeri seni, dan planetarium." },
                    { "Tebet Eco Park", PlaceCategory.TAMAN.name(), "Jakarta Selatan", 4.6f, "tebet_eco_park.jpg",
                            "https://maps.google.com/?q=Tebet+Eco+Park",
                            "Taman kota modern peraih penghargaan dengan jembatan ikonik Infinity Link Bridge." },
                    { "Hutan Kota GBK", PlaceCategory.TAMAN.name(), "Jakarta Pusat", 4.6f, "hutan_kota_gbk.jpg",
                            "https://maps.google.com/?q=Hutan+Kota+GBK",
                            "Taman rumput luas di tengah SCBD yang menawarkan pemandangan gedung pencakar langit ala New York." },

                    // === KATEGORI: KULINER ===
                    { "Obihiro Nikudon", PlaceCategory.KULINER.name(), "Jakarta Selatan", 4.6f, "obihiro_nikudon.jpg",
                            "https://maps.google.com/?q=Obihiro+nikudon+Jakarta",
                            "Restoran Jepang autentik dengan menu nikudon dan ramen khas Hokkaido." },
                    { "Gulai Tikungan Blok M", PlaceCategory.KULINER.name(), "Jakarta Selatan", 4.5f,
                            "gulai_tikungan.jpg", "https://maps.google.com/?q=Gulai+Tikungan+Blok+M",
                            "Kuliner malam legendaris berupa gulai sapi hangat porsi pas yang ramah di kantong." },
                    { "Daebak Tokpoki", PlaceCategory.KULINER.name(), "Depok", 4.7f, "daebak_tokpoki.jpg",
                            "https://maps.app.goo.gl/ooY613pYXadYZzpZ9",
                            "Restoran yang menyajikan berbagai macam street food Korea dengan bumbu autentik yang kaya rasa." },
                    { "Kedai Penuh Nikmat", PlaceCategory.KULINER.name(), "Jakarta Selatan", 4.6f,
                            "kedai_penuh_nikmat.jpg", "https://maps.app.goo.gl/KmUwZSE8Hp1L2LV86",
                            "Kedai makan estetik berkonsep klasik yang menyajikan comfort food khas Nusantara nan lezat." },

                    // === KATEGORI: PERPUSTAKAAN ===
                    { "Perpustakaan Jakarta", PlaceCategory.PERPUSTAKAAN.name(), "Jakarta Pusat", 4.8f,
                            "perpustakaan_jakarta.jpg", "https://maps.google.com/?q=Perpustakaan+Jakarta+TIM",
                            "Perpustakaan modern berdesain kayu estetik di dalam kompleks TIM yang sangat nyaman untuk WFC." },
                    { "Perpustakaan Nasional RI", PlaceCategory.PERPUSTAKAAN.name(), "Jakarta Pusat", 4.7f,
                            "perpustakaan_nasional.jpg", "https://maps.google.com/?q=Perpustakaan+Nasional+RI",
                            "Gedung perpustakaan tertinggi di dunia dengan koleksi lengkap dan view langsung ke Monas." },
                    { "Baca Di Tebet", PlaceCategory.PERPUSTAKAAN.name(), "Jakarta Selatan", 4.7f, "baca_di_tebet.jpg",
                            "https://maps.google.com/?q=Baca+Di+Tebet",
                            "Perpustakaan independen dan ruang baca komunal yang super cozy untuk pencinta literasi." },
                    { "Grha AAJI Building", PlaceCategory.PERPUSTAKAAN.name(), "Jakarta Selatan", 4.5f,
                            "grha_aaji_building.jpg", "https://maps.app.goo.gl/vgBBvFj4P7nnTQep6",
                            "Gedung asuransi jiwa yang dilengkapi dengan fasilitas literasi dan ruang baca yang tenang." }
            };

            for (Object[] row : initialData) {
                pstmt.setString(1, (String) row[0]);
                pstmt.setString(2, (String) row[1]);
                pstmt.setString(3, (String) row[2]);
                pstmt.setFloat(4, (Float) row[3]);
                pstmt.setString(5, (String) row[4]);
                pstmt.setString(6, (String) row[5]);
                pstmt.setString(7, (String) row[6]);
                pstmt.executeUpdate();
            }
        }
    }

    public static List<Place> getPlaces(String category, String sortBy) {
        List<Place> places = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT * FROM places");

        if (category != null && !category.isEmpty()) {
            query.append(" WHERE category = ?");
        }

        if (sortBy == null)
            sortBy = "rating_desc";
        switch (sortBy) {
            case "rating_asc":
                query.append(" ORDER BY rating ASC");
                break;
            case "name_asc":
                query.append(" ORDER BY name ASC");
                break;
            case "name_desc":
                query.append(" ORDER BY name DESC");
                break;
            default:
                query.append(" ORDER BY rating DESC");
                break;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            if (category != null && !category.isEmpty()) {
                pstmt.setString(1, category);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                places.add(new Place(
                        rs.getString("name"),
                        PlaceCategory.valueOf(rs.getString("category")),
                        rs.getString("location"),
                        rs.getFloat("rating"),
                        rs.getString("image_file_name"),
                        rs.getString("map_url"),
                        rs.getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return places;
    }

    public static List<Place> getAllPlaces() {
        return getPlaces(null, null);
    }
}
