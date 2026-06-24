package com.skenatrack.ui;

import com.skenatrack.model.Place;
import com.skenatrack.utils.FavoriteManager;
import com.skenatrack.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;

/**
 * PlaceDetailDialog — pengganti PlaceDetailBottomSheet (BottomSheetDialogFragment).
 *
 * Di Android: BottomSheetDialogFragment muncul dari bawah layar (modal sheet).
 * Di Swing  : JDialog modal yang muncul di tengah window — behavior paling mendekati.
 *
 * Callback OnFavoriteChangedListener dipertahankan agar HomePanel bisa
 * me-refresh list setelah status favorit berubah — pattern yang sama dengan Android.
 */
public class PlaceDetailDialog extends JDialog {

    public interface OnFavoriteChangedListener {
        void onFavoriteChanged();
    }

    private final Place place;
    private final OnFavoriteChangedListener favoriteChangedListener;

    // Warna tema
    private static final Color PURPLE      = new Color(14, 64, 45);
    private static final Color PURPLE_LIGHT = new Color(255, 184, 76);
    private static final Color BG          = new Color(250, 248, 245);
    private static final Color TEXT_MAIN   = new Color(44, 62, 53);
    private static final Color TEXT_SUB    = new Color(80, 95, 85);

    public PlaceDetailDialog(Frame parent, Place place, OnFavoriteChangedListener listener) {
        super(parent, place.getName(), true); // modal = true
        this.place = place;
        this.favoriteChangedListener = listener;

        buildUI();

        setSize(480, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);

        // === HEADER: Gambar tempat ===
        JLabel imgLabel = new JLabel(ImageLoader.loadScaled(place.getImageFileName(), 480, 200));
        imgLabel.setPreferredSize(new Dimension(480, 200));
        root.add(imgLabel, BorderLayout.NORTH);

        // === BODY: Info tempat ===
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 0, 20));

        // Nama
        JLabel lblName = new JLabel(place.getName());
        lblName.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblName.setForeground(TEXT_MAIN);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Chip kategori
        JLabel lblCategory = makeChip(place.getCategory().name());
        lblCategory.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Info baris: lokasi & rating
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        infoRow.setBackground(BG);
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblLoc    = new JLabel("📍 " + place.getLocation());
        JLabel lblSep    = new JLabel("   ");
        JLabel lblRating = new JLabel("⭐ " + place.getRating());
        lblLoc.setForeground(TEXT_SUB);
        lblRating.setForeground(TEXT_SUB);
        infoRow.add(lblLoc);
        infoRow.add(lblSep);
        infoRow.add(lblRating);

        // Deskripsi
        JTextArea txDesc = new JTextArea(place.getDescription());
        txDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txDesc.setForeground(TEXT_SUB);
        txDesc.setBackground(BG);
        txDesc.setEditable(false);
        txDesc.setLineWrap(true);
        txDesc.setWrapStyleWord(true);
        txDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        txDesc.setMaximumSize(new Dimension(440, 80));

        body.add(lblName);
        body.add(Box.createVerticalStrut(6));
        body.add(lblCategory);
        body.add(Box.createVerticalStrut(8));
        body.add(infoRow);
        body.add(Box.createVerticalStrut(12));
        body.add(txDesc);

        root.add(body, BorderLayout.CENTER);

        // === FOOTER: Tombol aksi ===
        JPanel footer = new JPanel(new GridLayout(1, 2, 10, 0));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

        JButton btnMaps = createButton("🗺️ Buka Maps", PURPLE, Color.WHITE);
        JButton btnFav  = createFavButton();

        btnMaps.addActionListener(e -> openMapUrl());
        btnFav.addActionListener(e  -> handleFavoriteToggle(btnFav));

        footer.add(btnMaps);
        footer.add(btnFav);

        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private JLabel makeChip(String text) {
        JLabel chip = new JLabel(" " + text + " ");
        chip.setOpaque(true);
        chip.setBackground(PURPLE_LIGHT);
        chip.setForeground(PURPLE);
        chip.setFont(new Font("SansSerif", Font.BOLD, 11));
        chip.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return chip;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }

    private JButton createFavButton() {
        boolean isFav = FavoriteManager.isFavorite(place.getName());
        String  label = isFav ? "💔 Hapus Favorit" : "❤️ Favorit";
        Color   bg    = isFav ? new Color(255, 240, 240) : PURPLE_LIGHT;
        Color   fg    = isFav ? new Color(180, 0, 0) : PURPLE;
        return createButton(label, bg, fg);
    }

    /**
     * Analog dengan btnFav.setOnClickListener di Android.
     * Menampilkan konfirmasi dialog → update FavoriteManager → notify listener.
     */
    private void handleFavoriteToggle(JButton btnFav) {
        boolean isFav   = FavoriteManager.isFavorite(place.getName());
        String  title   = isFav ? "Hapus Favorit"  : "Tambah Favorit";
        String  message = isFav
                ? "Hapus tempat ini dari daftar favorit?"
                : "Tambahkan tempat ini ke daftar favorit?";

        int confirm = JOptionPane.showConfirmDialog(
                this, message, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (isFav) {
                FavoriteManager.removeFavorite(place.getName());
                btnFav.setText("❤️ Favorit");
                btnFav.setBackground(PURPLE_LIGHT);
                btnFav.setForeground(PURPLE);
                showToast("Dihapus dari favorit");
            } else {
                FavoriteManager.addFavorite(place.getName());
                btnFav.setText("💔 Hapus Favorit");
                btnFav.setBackground(new Color(255, 240, 240));
                btnFav.setForeground(new Color(180, 0, 0));
                showToast("Ditambahkan ke favorit");
            }

            if (favoriteChangedListener != null) {
                favoriteChangedListener.onFavoriteChanged();
            }
        }
    }

    /** Pengganti Snackbar.make() — Swing tidak punya Snackbar, pakai dialog singkat. */
    private void showToast(String message) {
        JOptionPane.showMessageDialog(this, message, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /** Buka URL di browser default sistem — analog dengan Intent ACTION_VIEW. */
    private void openMapUrl() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(place.getMapUrl()));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Tidak bisa membuka browser:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
