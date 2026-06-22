package com.skenatrack.ui;

import com.skenatrack.ui.panel.AboutPanel;
import com.skenatrack.ui.panel.HomePanel;
import com.skenatrack.ui.panel.ProfilePanel;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * MainWindow — pengganti MainActivity + activity_main.xml.
 *
 * Mapping arsitektur:
 *
 *   MainActivity (AppCompatActivity)    → JFrame
 *   MaterialToolbar (setSupportActionBar) → JPanel header custom
 *   BottomNavigationView                 → JPanel navigasi bawah berisi JButton
 *   NavHostFragment (container fragment) → CardLayout untuk swap antar panel
 *   NavController + NavigationUI         → ActionListener manual pada nav buttons
 *   SharedPreferences (dark mode)        → java.util.prefs.Preferences
 *
 * Kenapa CardLayout dan bukan JTabbedPane?
 * JTabbedPane menaruh tab di atas, sedangkan BottomNavigationView ada di bawah.
 * CardLayout + panel navigasi custom lebih mendekati perilaku asli Android.
 */
public class MainWindow extends JFrame {

    private static final String PREFS_NODE = "/com/skenatrack";
    private static final String KEY_DARK   = "dark_mode";

    // Panel konten
    private final CardLayout cardLayout    = new CardLayout();
    private final JPanel     contentArea   = new JPanel(cardLayout);

    // Nav buttons — dipertahankan referensinya untuk highlight active
    private JButton btnHome, btnAbout, btnProfile;

    // Warna tema light
    private static final Color NAV_BG      = new Color(103, 80, 164);
    private static final Color NAV_ACTIVE  = new Color(234, 221, 255);
    private static final Color NAV_TEXT    = Color.WHITE;
    private static final Color HEADER_BG   = new Color(103, 80, 164);

    public MainWindow() {
        super("SkenaTrack");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 760);
        setMinimumSize(new Dimension(380, 600));
        setLocationRelativeTo(null); // tengah layar

        applyDarkModeFromPrefs();

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        add(buildHeader(),    BorderLayout.NORTH);
        add(contentArea,      BorderLayout.CENTER);
        add(buildBottomNav(), BorderLayout.SOUTH);

        // Register panels ke CardLayout — analog inflate fragment
        contentArea.add(new HomePanel(),    "home");
        contentArea.add(new AboutPanel(),   "about");
        contentArea.add(new ProfilePanel(), "profile");

        // Default: tampilkan Home
        showPanel("home", btnHome);
    }

    // -----------------------------------------------------------------------
    // Header — analog MaterialToolbar + setSupportActionBar
    // -----------------------------------------------------------------------

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Logo teks — analog setLogo()
        JLabel logo = new JLabel("🗺️  SkenaTrack");
        logo.setFont(new Font("SansSerif", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);

        // Menu settings — analog onCreateOptionsMenu
        JButton btnSettings = new JButton("⚙️");
        btnSettings.setBackground(HEADER_BG);
        btnSettings.setForeground(Color.WHITE);
        btnSettings.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnSettings.setBorderPainted(false);
        btnSettings.setFocusPainted(false);
        btnSettings.setOpaque(false);
        btnSettings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSettings.addActionListener(e -> showDarkModeDialog());
        header.add(btnSettings, BorderLayout.EAST);

        return header;
    }

    // -----------------------------------------------------------------------
    // Bottom Navigation — analog BottomNavigationView
    // -----------------------------------------------------------------------

    private JPanel buildBottomNav() {
        JPanel nav = new JPanel(new GridLayout(1, 3));
        nav.setBackground(NAV_BG);
        nav.setPreferredSize(new Dimension(0, 56));

        btnHome    = makeNavButton("🏠 Home",    "home");
        btnAbout   = makeNavButton("ℹ️ Tentang",  "about");
        btnProfile = makeNavButton("👤 Profil",   "profile");

        nav.add(btnHome);
        nav.add(btnAbout);
        nav.add(btnProfile);
        return nav;
    }

    private JButton makeNavButton(String label, String panelKey) {
        JButton btn = new JButton(label);
        btn.setForeground(NAV_TEXT);
        btn.setBackground(NAV_BG);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showPanel(panelKey, btn));
        return btn;
    }

    private void showPanel(String key, JButton activeBtn) {
        cardLayout.show(contentArea, key);

        // Reset semua nav button
        for (JButton b : new JButton[]{btnHome, btnAbout, btnProfile}) {
            b.setBackground(NAV_BG);
            b.setForeground(NAV_TEXT);
        }

        // Highlight active
        activeBtn.setBackground(NAV_ACTIVE);
        activeBtn.setForeground(new Color(60, 20, 120));
    }

    // -----------------------------------------------------------------------
    // Dark Mode — analog showDarkModeDialog() di MainActivity
    // -----------------------------------------------------------------------

    private void showDarkModeDialog() {
        Preferences prefs    = Preferences.userRoot().node(PREFS_NODE);
        boolean     isDark   = prefs.getBoolean(KEY_DARK, false);

        JCheckBox cbDark = new JCheckBox("Dark Mode", isDark);
        cbDark.setFont(new Font("SansSerif", Font.PLAIN, 13));

        int result = JOptionPane.showConfirmDialog(
            this, cbDark, "Pengaturan",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            prefs.putBoolean(KEY_DARK, cbDark.isSelected());
            applyLookAndFeel(cbDark.isSelected());
            // Restart diperlukan agar full effect karena Swing tidak support
            // hot-reload theme di semua komponen tanpa rebuild
            JOptionPane.showMessageDialog(this,
                "Dark mode " + (cbDark.isSelected() ? "aktif" : "nonaktif") +
                ".\nAplikasi perlu di-restart untuk efek penuh.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void applyDarkModeFromPrefs() {
        Preferences prefs  = Preferences.userRoot().node(PREFS_NODE);
        boolean     isDark = prefs.getBoolean(KEY_DARK, false);
        applyLookAndFeel(isDark);
    }

    private void applyLookAndFeel(boolean dark) {
        try {
            if (dark) {
                // Nimbus dark — bawaan JDK, tidak perlu library
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        UIManager.put("control", new Color(60, 63, 65));
                        UIManager.put("info", new Color(60, 63, 65));
                        UIManager.put("nimbusBase", new Color(18, 30, 49));
                        UIManager.put("nimbusBlueGrey", new Color(80, 80, 80));
                        UIManager.put("nimbusFocus", new Color(115, 164, 209));
                        UIManager.put("text", Color.WHITE);
                        break;
                    }
                }
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            // Fallback ke default jika gagal — aplikasi tetap jalan
        }
    }

    // -----------------------------------------------------------------------
    // Entry Point
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        // SwingUtilities.invokeLater — best practice: jalankan UI di Event Dispatch Thread
        // Analog dengan Android main thread untuk UI operations
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
