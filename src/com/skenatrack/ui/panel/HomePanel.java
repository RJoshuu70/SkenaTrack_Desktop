package com.skenatrack.ui.panel;

import com.skenatrack.datasource.DataSource;
import com.skenatrack.model.Place;
import com.skenatrack.model.PlaceCategory;
import com.skenatrack.ui.PlaceCardPanel;
import com.skenatrack.ui.PlaceDetailDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HomePanel — pengganti HomeFragment + fragment_home.xml.
 *
 * Mapping komponen Android → Swing:
 *   ChipGroup (filter kategori)  → JPanel berisi JToggleButton (ButtonGroup)
 *   EditText (search)             → JTextField + DocumentListener
 *   ImageButton (sort)            → JButton + JPopupMenu
 *   RecyclerView + PlaceAdapter   → JScrollPane + JPanel berisi PlaceCardPanel
 *   Handler.postDelayed (debounce)→ javax.swing.Timer
 *
 * Logika bisnis (filter, sort, search debounce) dipertahankan 100%.
 */
public class HomePanel extends JPanel {

    private String  currentCategory = null;
    private String  currentSort     = "rating_desc";
    private String  currentQuery    = "";

    private JPanel  listPanel;   // kontainer card-card tempat
    private Timer   debounceTimer; // pengganti Handler.postDelayed

    // Warna tema
    private static final Color PURPLE      = new Color(103, 80, 164);
    private static final Color PURPLE_LIGHT = new Color(234, 221, 255);
    private static final Color BG          = new Color(255, 251, 254);
    private static final Color BG_PANEL    = new Color(247, 242, 255);
    private static final Color TEXT_SUB    = new Color(73, 69, 79);

    public HomePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildList(),    BorderLayout.CENTER);

        loadData();
    }

    // -----------------------------------------------------------------------
    // UI Builder Methods
    // -----------------------------------------------------------------------

    /** Toolbar atas: search + sort button — analog dengan fragment_home.xml header. */
    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBackground(BG_PANEL);
        top.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // Search field
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 190, 220), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Cari tempat...");

        // Debounce via Swing Timer (analog Handler.postDelayed 2000ms)
        debounceTimer = new Timer(2000, e -> {
            currentQuery = searchField.getText().trim();
            loadData();
        });
        debounceTimer.setRepeats(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChanged() {
                debounceTimer.restart(); // reset timer setiap keystroke
            }
            @Override public void insertUpdate(DocumentEvent e)  { onChanged(); }
            @Override public void removeUpdate(DocumentEvent e)  { onChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onChanged(); }
        });

        // Sort button + popup menu
        JButton btnSort = new JButton("⇅ Sort");
        btnSort.setBackground(PURPLE);
        btnSort.setForeground(Color.WHITE);
        btnSort.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSort.setFocusPainted(false);
        btnSort.setBorderPainted(false);
        btnSort.setOpaque(true);
        btnSort.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu sortMenu = new JPopupMenu();
        addSortItem(sortMenu, "Rating Tertinggi", "rating_desc");
        addSortItem(sortMenu, "Rating Terendah",  "rating_asc");
        addSortItem(sortMenu, "Nama A-Z",         "name_asc");
        addSortItem(sortMenu, "Nama Z-A",         "name_desc");

        btnSort.addActionListener(e ->
            sortMenu.show(btnSort, 0, btnSort.getHeight())
        );

        top.add(searchField, BorderLayout.CENTER);
        top.add(btnSort,     BorderLayout.EAST);

        // Panel filter chips (kategori) — di bawah search bar
        JPanel chips = buildCategoryChips();

        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(BG_PANEL);
        wrapper.add(top,   BorderLayout.NORTH);
        wrapper.add(chips, BorderLayout.SOUTH);
        return wrapper;
    }

    /**
     * Chips filter kategori — analog ChipGroup + Chip di Android Material.
     * Di sini kita pakai JToggleButton dalam ButtonGroup agar single-select.
     */
    private JPanel buildCategoryChips() {
        JPanel chipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        chipPanel.setBackground(BG_PANEL);
        chipPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        ButtonGroup group = new ButtonGroup();

        // Chip "Semua"
        addChip(chipPanel, group, "Semua", null, true);

        // Chip per kategori
        for (PlaceCategory cat : PlaceCategory.values()) {
            addChip(chipPanel, group, cat.name(), cat.name(), false);
        }

        return chipPanel;
    }

    private void addChip(JPanel parent, ButtonGroup group,
                         String label, String categoryValue, boolean selected) {
        JToggleButton chip = new JToggleButton(label, selected);
        chip.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chip.setFocusPainted(false);
        chip.setBorderPainted(false);
        chip.setOpaque(true);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.setPreferredSize(new Dimension(
            chip.getPreferredSize().width + 16, 28
        ));

        // Warna dinamis selected/unselected — analog ColorStateList chip
        updateChipStyle(chip);
        chip.addActionListener(e -> {
            currentCategory = categoryValue;
            updateChipStyle(chip);
            loadData();
        });
        chip.addChangeListener(e -> updateChipStyle(chip));

        group.add(chip);
        parent.add(chip);
    }

    private void updateChipStyle(JToggleButton chip) {
        if (chip.isSelected()) {
            chip.setBackground(PURPLE);
            chip.setForeground(Color.WHITE);
        } else {
            chip.setBackground(Color.WHITE);
            chip.setForeground(TEXT_SUB);
        }
    }

    /** List panel dengan scroll — analog RecyclerView + LinearLayoutManager. */
    private JScrollPane buildList() {
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    // -----------------------------------------------------------------------
    // Data Loading — logika identik dengan HomeFragment.loadData()
    // -----------------------------------------------------------------------

    private void loadData() {
        List<Place> places = DataSource.getPlaces(currentCategory, currentSort);

        // Filter by search query (same logic as HomeFragment)
        if (!currentQuery.isBlank()) {
            List<Place> filtered = new ArrayList<>();
            for (Place p : places) {
                if (p.getName().toLowerCase().contains(currentQuery.toLowerCase())) {
                    filtered.add(p);
                }
            }
            places = filtered;
        }

        renderList(places);
    }

    /**
     * Render ulang list — analog adapter.updateData() + notifyDataSetChanged().
     * Di Swing kita removeAll() lalu tambah ulang panel-panel card.
     */
    private void renderList(List<Place> places) {
        listPanel.removeAll();

        if (places.isEmpty()) {
            JLabel empty = new JLabel("Tidak ada tempat ditemukan");
            empty.setForeground(TEXT_SUB);
            empty.setFont(new Font("SansSerif", Font.ITALIC, 13));
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(40));
            listPanel.add(empty);
        } else {
            for (Place place : places) {
                PlaceCardPanel card = new PlaceCardPanel(place, p -> openDetail(p));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
                listPanel.add(card);
            }
        }

        // Paksa Swing re-render — analog notifyDataSetChanged()
        listPanel.revalidate();
        listPanel.repaint();
    }

    /** Buka detail dialog — analog PlaceDetailBottomSheet.show() */
    private void openDetail(Place place) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PlaceDetailDialog dialog = new PlaceDetailDialog(parent, place, () -> loadData());
        dialog.setVisible(true);
    }

    // -----------------------------------------------------------------------
    // Sort menu helper
    // -----------------------------------------------------------------------

    private void addSortItem(JPopupMenu menu, String label, String sortKey) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
            currentSort = sortKey;
            loadData();
        });
        menu.add(item);
    }
}
