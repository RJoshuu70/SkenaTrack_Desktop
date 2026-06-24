package com.skenatrack.ui.panel;

import com.skenatrack.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;

/**
 * ProfilePanel — pengganti ProfileFragment + fragment_profile.xml.
 *
 * Tidak ada logika bisnis di sini — murni display statis,
 * sama seperti versi Android yang hanya setText() tanpa logic lain.
 */
public class ProfilePanel extends JPanel {

    private static final Color BG       = new Color(250, 248, 245);
    private static final Color PURPLE   = new Color(14, 64, 45);
    private static final Color TEXT_MAIN = new Color(44, 62, 53);
    private static final Color TEXT_SUB  = new Color(80, 95, 85);

    public ProfilePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Foto profil (lingkaran) — analog imgProfile
        JLabel imgProfile = new JLabel(ImageLoader.loadScaled("profile.jpg", 100, 100));
        imgProfile.setPreferredSize(new Dimension(100, 100));
        imgProfile.setMaximumSize(new Dimension(100, 100));
        imgProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgProfile.setBorder(BorderFactory.createLineBorder(PURPLE, 2));

        // Info rows — analog tvDevName, tvNim, dll.
        add(imgProfile);
        add(Box.createVerticalStrut(16));
        add(makeTitle("Kelompok 2"));
        add(Box.createVerticalStrut(20));
        add(makeDivider());
        add(Box.createVerticalStrut(16));
        add(makeInfoRow("🎓 NIM",       "001, 009, 023, 029, 032"));
        add(Box.createVerticalStrut(10));
        add(makeInfoRow("🐙 GitHub",    "github.com/RJoshuu70"));
        add(Box.createVerticalStrut(10));
        add(makeInfoRow("📸 Instagram", "@aduhputbool"));
        add(Box.createVerticalStrut(10));
        add(makeInfoRow("📧 Email",     "NIM@mahasiswa.upnvj.ac.id"));
        add(Box.createVerticalStrut(24));
        add(makeDivider());
        add(Box.createVerticalStrut(16));
        add(makeCaption("Aplikasi SkenaTrack — OOP Final Project"));
        add(makeCaption("Fasilkom UPNVJ © 2025"));
    }

    private JLabel makeTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        lbl.setForeground(TEXT_MAIN);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(220, 215, 230));
        return sep;
    }

    private JPanel makeInfoRow(String key, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblKey = new JLabel(key);
        lblKey.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblKey.setForeground(PURPLE);
        lblKey.setPreferredSize(new Dimension(120, 20));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblVal.setForeground(TEXT_SUB);

        row.add(lblKey, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.CENTER);
        return row;
    }

    private JLabel makeCaption(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lbl.setForeground(new Color(150, 140, 160));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
}
