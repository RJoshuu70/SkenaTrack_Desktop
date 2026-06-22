package com.skenatrack.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * FavoriteManager — pengganti SharedPreferences versi Android.
 *
 * Di Android, SharedPreferences menyimpan data ke file XML di internal storage app.
 * Di desktop, java.util.prefs.Preferences melakukan hal serupa:
 *   - Windows → Registry (HKCU\Software\JavaSoft\Prefs)
 *   - macOS   → ~/Library/Preferences/
 *   - Linux   → ~/.java/.userPrefs/
 *
 * Interface publik (isFavorite, addFavorite, removeFavorite) identik — hanya
 * parameter "Context" dihapus karena tidak ada Context di desktop.
 */
public class FavoriteManager {

    private static final String PREFS_NODE    = "/com/skenatrack";
    private static final String KEY_FAVORITES = "favorites";
    private static final String DELIMITER     = "||"; // pemisah antar nama tempat

    private FavoriteManager() {} // Utility class, tidak perlu diinstansiasi

    private static Preferences getPrefs() {
        return Preferences.userRoot().node(PREFS_NODE);
    }

    private static Set<String> loadFavorites() {
        String raw = getPrefs().get(KEY_FAVORITES, "");
        Set<String> set = new HashSet<>();
        if (!raw.isEmpty()) {
            set.addAll(Arrays.asList(raw.split("\\|\\|")));
        }
        return set;
    }

    private static void saveFavorites(Set<String> favorites) {
        String joined = String.join(DELIMITER, favorites);
        getPrefs().put(KEY_FAVORITES, joined);
    }

    public static boolean isFavorite(String placeName) {
        return loadFavorites().contains(placeName);
    }

    public static void addFavorite(String placeName) {
        Set<String> current = loadFavorites();
        current.add(placeName);
        saveFavorites(current);
    }

    public static void removeFavorite(String placeName) {
        Set<String> current = loadFavorites();
        current.remove(placeName);
        saveFavorites(current);
    }
}
