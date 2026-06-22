package com.skenatrack.datasource;

import com.skenatrack.model.Place;
import com.skenatrack.model.PlaceCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DataSource — layer data statis aplikasi.
 *
 * Perbedaan dari versi Android:
 * - Dihapus: import R (resource ID sistem Android).
 * - imageRes (int) → imageFileName (String): kita gunakan nama file
 *   yang akan di-load dari folder resources/images/.
 * - Logika filter & sort 100% identik.
 */
public class DataSource {

    private static final List<Place> ALL_PLACES = new ArrayList<>();

    static {
        ALL_PLACES.add(new Place(
            "Kopi Nako Depok", PlaceCategory.CAFE, "Depok", 4.4f,
            "kopi_nako_depok.jpg",
            "https://maps.google.com/?q=Kopi+Nako+Depok",
            "Kafe estetik di Depok dengan menu kopi susu premium dan dessert pilihan."
        ));
        ALL_PLACES.add(new Place(
            "MATCHAMAN, Blok M", PlaceCategory.CAFE, "Jakarta Selatan", 4.7f,
            "matchaman_blok_m.jpg",
            "https://maps.google.com/?q=MATCHAMAN+Blok+M",
            "Spesialis matcha dengan suasana Jepang modern di pusat kota Jakarta."
        ));
        ALL_PLACES.add(new Place(
            "Museum MACAN", PlaceCategory.MUSEUM, "Jakarta Barat", 4.8f,
            "museum_macan.jpg",
            "https://maps.google.com/?q=Museum+MACAN+Jakarta",
            "Museum seni kontemporer dan modern terbesar di Indonesia."
        ));
        ALL_PLACES.add(new Place(
            "Museum Nasional", PlaceCategory.MUSEUM, "Jakarta Pusat", 4.6f,
            "museum_nasional.jpg",
            "https://maps.google.com/?q=Museum+Nasional+Jakarta",
            "Museum bersejarah dengan koleksi artefak budaya Nusantara terlengkap."
        ));
        ALL_PLACES.add(new Place(
            "Obihiro Nikudon", PlaceCategory.KULINER, "Jakarta Selatan", 4.6f,
            "obihiro_nikudon.jpg",
            "https://maps.google.com/?q=Obihiro+nikudon+Jakarta",
            "Restoran Jepang autentik dengan menu nikudon dan ramen khas Hokkaido."
        ));
        ALL_PLACES.add(new Place(
            "Waduk Brigif", PlaceCategory.TAMAN, "Depok", 4.7f,
            "waduk_brigif.jpg",
            "https://maps.google.com/?q=Waduk+Brigif+Depok",
            "Taman wisata air dengan pemandangan danau yang asri di pinggiran Depok."
        ));
        ALL_PLACES.add(new Place(
            "Taman Ismail Marzuki", PlaceCategory.TAMAN, "Jakarta Pusat", 4.5f,
            "taman_ismail_marzuki.jpg",
            "https://maps.google.com/?q=Taman+Ismail+Marzuki+Jakarta",
            "Pusat kebudayaan Jakarta dengan taman hijau, galeri seni, dan planetarium."
        ));
    }

    /** Filter + sort sesuai parameter. Logika 100% sama dengan versi Android. */
    public static List<Place> getPlaces(String category, String sortBy) {
        List<Place> result = new ArrayList<>(ALL_PLACES);

        if (category != null && !category.isEmpty()) {
            List<Place> filtered = new ArrayList<>();
            for (Place p : result) {
                if (p.getCategory().name().equals(category)) {
                    filtered.add(p);
                }
            }
            result = filtered;
        }

        if (sortBy == null) sortBy = "rating_desc";
        switch (sortBy) {
            case "rating_asc":
                Collections.sort(result, (a, b) -> Float.compare(a.getRating(), b.getRating()));
                break;
            case "name_asc":
                Collections.sort(result, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case "name_desc":
                Collections.sort(result, (a, b) -> b.getName().compareToIgnoreCase(a.getName()));
                break;
            default:
                Collections.sort(result, (a, b) -> Float.compare(b.getRating(), a.getRating()));
                break;
        }

        return result;
    }

    public static List<Place> getAllPlaces() {
        return new ArrayList<>(ALL_PLACES);
    }
}
