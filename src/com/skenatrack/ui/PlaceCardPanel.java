package com.skenatrack.ui;

import com.skenatrack.model.Place;
import com.skenatrack.utils.FavoriteManager;
import com.skenatrack.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * PlaceCardPanel — pengganti item_place.xml + PlaceViewHolder (dari PlaceAdapter).
 *
 * Di Android, setiap item list dirender oleh ViewHolder yang inflate layout XML.
 * Di Swing, kita buat JPanel custom yang menampilkan data Place dengan layout manual.
 *
 * Pattern yang dipertahankan:
 * - Encapsulation: Place hanya dibaca via getter.
 * - Callback OnItemClickListener tetap ada — HomePanel yang menentukan
 *   apa yang terjadi saat card diklik (buka detail dialog).
 */
public class PlaceCardPanel extends JPanel {

    // Warna — diambil dari colors.xml versi Android
    private static final Color BG_CARD    = Color.WHITE;
    private static final Color BG_HOVER   = new Color(240, 242, 238);
    private static final Color PURPLE     = new Color(14, 64, 45);
    private static final Color TEXT_MAIN  = new Color(44, 62, 53);
    private static final Color TEXT_SUB   = new Color(80, 95, 85);
    private static final Color BORDER_CLR = new Color(230, 225, 235);

    public interface OnItemClickListener {
        void onItemClick(Place place);
    }

    public PlaceCardPanel(Place place, OnItemClickListener listener) {
        setLayout(new BorderLayout(12, 0));
        setBackground(BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // === GAMBAR (kiri) — analog imgPlace di ViewHolder ===
        JLabel imgLabel = new JLabel(ImageLoader.loadScaled(place.getImageFileName(), 90, 70));
        imgLabel.setPreferredSize(new Dimension(90, 70));
        imgLabel.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        add(imgLabel, BorderLayout.WEST);

        // === TEKS (tengah) ===
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_CARD);

        JLabel lblName = new JLabel(place.getName());
        lblName.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblName.setForeground(TEXT_MAIN);

        JLabel lblCategory = new JLabel(place.getCategory().name());
        lblCategory.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCategory.setForeground(PURPLE);

        JLabel lblLocation = new JLabel("📍 " + place.getLocation());
        lblLocation.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblLocation.setForeground(TEXT_SUB);

        JLabel lblRating = new JLabel("⭐ " + place.getRating());
        lblRating.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblRating.setForeground(TEXT_SUB);

        textPanel.add(lblName);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblCategory);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblLocation);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblRating);
        add(textPanel, BorderLayout.CENTER);

        // === IKON FAVORIT (kanan) — analog imgFavorite ===
        boolean isFav = FavoriteManager.isFavorite(place.getName());
        if (isFav) {
            JLabel favIcon = new JLabel("❤️");
            favIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));
            favIcon.setVerticalAlignment(SwingConstants.CENTER);
            add(favIcon, BorderLayout.EAST);
        }

        // === HOVER EFFECT ===
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                setBackground(BG_HOVER);
                textPanel.setBackground(BG_HOVER);
                repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                setBackground(BG_CARD);
                textPanel.setBackground(BG_CARD);
                repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                listener.onItemClick(place);
            }
        });
    }
}
