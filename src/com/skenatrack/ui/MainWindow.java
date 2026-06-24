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
    private static final Color NAV_BG      = new Color(14, 64, 45);
    private static final Color NAV_ACTIVE  = new Color(255, 184, 76);
    private static final Color NAV_TEXT    = Color.WHITE;
    private static final Color HEADER_BG   = new Color(14, 64, 45);

    public MainWindow() {
        super("SkenaTrack");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 760);
        setMinimumSize(new Dimension(380, 600));
        setLocationRelativeTo(null); // tengah layar

        setLocationRelativeTo(null); // tengah layar
        com.skenatrack.datasource.DataSource.initDatabase();

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

        // (Tombol settings dihapus karena dark mode tidak disupport secara rapih di Swing)

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
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background utama hijau gelap
                g2.setColor(NAV_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Jika sedang aktif, gambar bentuk kapsul (pill) di belakang teks
                if (getBackground().equals(NAV_ACTIVE)) {
                    g2.setColor(NAV_ACTIVE);
                    int h = 32;
                    int y = (getHeight() - h) / 2;
                    int w = Math.min(100, getWidth() - 20);
                    int x = (getWidth() - w) / 2;
                    g2.fillRoundRect(x, y, w, h, 20, 20);
                }
                
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setForeground(NAV_TEXT);
        btn.setBackground(NAV_BG);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
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
        activeBtn.setForeground(new Color(14, 64, 45));
    }

    // Fitur Dark Mode sudah dihapus karena tidak compatible dengan komponen Swing kustom

    // -----------------------------------------------------------------------
    // Entry Point
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        // SwingUtilities.invokeLater — best practice: jalankan UI di Event Dispatch Thread
        // Analog dengan Android main thread untuk UI operations
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
