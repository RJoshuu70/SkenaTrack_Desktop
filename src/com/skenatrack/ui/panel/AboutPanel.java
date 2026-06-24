package com.skenatrack.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * AboutPanel — menggabungkan AboutFragment + AboutAppFragment Android.
 * (Di Android keduanya ditampilkan via ViewPager2 di dalam AboutFragment.)
 *
 * Di desktop kita simplifikasi jadi satu panel karena tidak ada navigasi
 * yang kompleks — sejalan dengan prinsip "YAGNI" (You Aren't Gonna Need It).
 */
public class AboutPanel extends JPanel {

    private static final Color BG       = new Color(250, 248, 245);
    private static final Color PURPLE   = new Color(14, 64, 45);
    private static final Color TEXT_MAIN = new Color(44, 62, 53);
    private static final Color TEXT_SUB  = new Color(80, 95, 85);

    public AboutPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        add(makeHeader("🗺️ Tentang SkenaTrack"));
        add(Box.createVerticalStrut(16));
        add(makeBody(
            "SkenaTrack adalah aplikasi rekomendasi tempat wisata, kuliner, " +
            "kafe, dan taman di area Depok dan Jakarta. Dirancang untuk membantu " +
            "pengguna menemukan destinasi menarik dengan informasi rating, lokasi, " +
            "dan akses langsung ke Google Maps."
        ));
        add(Box.createVerticalStrut(20));
        add(makeSectionTitle("Fitur Utama"));
        add(Box.createVerticalStrut(8));
        add(makeBullet("🔍 Pencarian real-time dengan debounce 2 detik"));
        add(makeBullet("🏷️ Filter berdasarkan kategori (Cafe, Museum, Kuliner, Taman)"));
        add(makeBullet("⇅ Sorting berdasarkan rating dan nama"));
        add(makeBullet("❤️ Sistem favorit tersimpan permanen (Preferences API)"));
        add(makeBullet("🗺️ Integrasi langsung dengan Google Maps"));
        add(Box.createVerticalStrut(20));
        add(makeSectionTitle("Teknologi"));
        add(Box.createVerticalStrut(8));
        add(makeBullet("Java SE 11+ dengan Java Swing GUI"));
        add(makeBullet("java.util.prefs.Preferences untuk penyimpanan lokal"));
        add(makeBullet("Pattern OOP: Encapsulation, Inheritance, Polymorphism"));
        add(Box.createVerticalStrut(20));
        add(makeSectionTitle("Versi"));
        add(Box.createVerticalStrut(8));
        add(makeBody("SkenaTrack Desktop v1.0 — Converted from Android (OOP Final Project)"));
    }

    private JLabel makeHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(PURPLE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel makeSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(TEXT_MAIN);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextArea makeBody(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setForeground(TEXT_SUB);
        area.setBackground(BG);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        return area;
    }

    private JLabel makeBullet(String text) {
        JLabel lbl = new JLabel("  • " + text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SUB);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
