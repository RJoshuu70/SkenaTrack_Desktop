package com.skenatrack.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * ImageLoader — utility untuk memuat gambar di environment desktop.
 *
 * Di Android, gambar diakses via R.drawable.xxx (integer ID → resource compiler).
 * Di Swing, kita load dari filesystem atau classpath menggunakan ImageIO.
 *
 * Strategy yang dipakai:
 * 1. Coba load dari folder "resources/images/" relatif terhadap working directory.
 * 2. Jika tidak ada, tampilkan placeholder berwarna abu-abu.
 *
 * Kenapa tidak pakai getClass().getResource()?
 * Saat development tanpa JAR packaging, lebih mudah taruh gambar di folder
 * resources/images/ dan akses langsung via File. Saat di-JAR, bisa diubah
 * ke classpath approach.
 */
public class ImageLoader {

    private ImageLoader() {}

    /**
     * Load gambar dan scale ke ukuran target.
     * @param fileName   nama file, e.g. "kopi_nako.jpg"
     * @param targetW    lebar target dalam pixel
     * @param targetH    tinggi target dalam pixel
     * @return ImageIcon yang siap dipakai di JLabel
     */
    public static ImageIcon loadScaled(String fileName, int targetW, int targetH) {
        // Coba dari folder resources/images/
        File imgFile = new File("resources/images/" + fileName);
        if (imgFile.exists()) {
            try {
                BufferedImage original = ImageIO.read(imgFile);
                Image scaled = original.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } catch (IOException e) {
                // fall through ke placeholder
            }
        }

        // Placeholder jika gambar tidak ditemukan
        return createPlaceholder(targetW, targetH, fileName);
    }

    private static ImageIcon createPlaceholder(int w, int h, String label) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background abu-abu
        g.setColor(new Color(220, 220, 225));
        g.fillRoundRect(0, 0, w, h, 12, 12);

        // Teks nama file (debugging hint)
        g.setColor(new Color(150, 150, 160));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        String text = "📷 " + label;
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = h / 2 + fm.getAscent() / 2;
        g.drawString(text, tx, ty);

        g.dispose();
        return new ImageIcon(img);
    }
}
