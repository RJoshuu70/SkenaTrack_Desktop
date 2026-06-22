# SkenaTrack Desktop (Java Swing)

Versi desktop dari aplikasi Android SkenaTrack, dikonversi menggunakan **Java Swing**
sebagai GUI toolkit. Tidak memerlukan framework tambahan — semua library sudah
tersedia di JDK standar (Java 11+).

---

## Struktur Proyek

```
SkenaTrackDesktop/
├── src/
│   └── com/skenatrack/
│       ├── model/
│       │   ├── Place.java           ← Model data (tanpa Parcelable)
│       │   └── PlaceCategory.java   ← Enum kategori
│       ├── datasource/
│       │   └── DataSource.java      ← Data statis + filter/sort
│       ├── utils/
│       │   ├── FavoriteManager.java ← Persistensi (java.util.prefs.Preferences)
│       │   └── ImageLoader.java     ← Load gambar dari resources/images/
│       └── ui/
│           ├── MainWindow.java      ← JFrame utama (pengganti MainActivity)
│           ├── PlaceDetailDialog.java ← JDialog (pengganti BottomSheet)
│           ├── PlaceCardPanel.java  ← Card item (pengganti ViewHolder)
│           └── panel/
│               ├── HomePanel.java    ← Pengganti HomeFragment
│               ├── ProfilePanel.java ← Pengganti ProfileFragment
│               └── AboutPanel.java   ← Pengganti AboutFragment
├── resources/
│   └── images/           ← Taruh file gambar di sini!
│       ├── kopi_nako_depok.jpg
│       ├── matchaman_blok_m.jpg
│       ├── museum_macan.jpg
│       ├── museum_nasional.jpg
│       ├── obihiro_nikudon.jpg
│       ├── waduk_brigif.jpg
│       ├── taman_ismail_marzuki.jpg
│       └── profile.jpg
└── README.md
```

---

## Cara Compile & Run

### Prasyarat
- **Java JDK 11+** terinstall
- Pastikan `java` dan `javac` tersedia di terminal

### 1. Compile semua file Java

Buka terminal di folder `SkenaTrackDesktop/`, lalu jalankan:

**Windows (Command Prompt):**
```cmd
javac -d out -sourcepath src src\com\skenatrack\ui\MainWindow.java
```

**macOS / Linux:**
```bash
javac -d out -sourcepath src src/com/skenatrack/ui/MainWindow.java
```

Flag `-d out` berarti hasil compile (.class files) masuk ke folder `out/`.
Flag `-sourcepath src` memberi tahu javac di mana mencari file Java lainnya.

### 2. Run aplikasi

**Windows:**
```cmd
java -cp out com.skenatrack.ui.MainWindow
```

**macOS / Linux:**
```bash
java -cp out com.skenatrack.ui.MainWindow
```

### 3. Satu perintah (compile + run)

**macOS / Linux:**
```bash
javac -d out -sourcepath src src/com/skenatrack/ui/MainWindow.java && java -cp out com.skenatrack.ui.MainWindow
```

---

## Menambah Gambar

Buat folder `resources/images/` di dalam `SkenaTrackDesktop/`.
Taruh gambar dengan nama persis sesuai yang ada di `DataSource.java`:

| Nama file yang diharapkan          | Tempat                |
|------------------------------------|-----------------------|
| `kopi_nako_depok.jpg`              | Kopi Nako Depok       |
| `matchaman_blok_m.jpg`             | MATCHAMAN Blok M      |
| `museum_macan.jpg`                 | Museum MACAN          |
| `museum_nasional.jpg`              | Museum Nasional       |
| `obihiro_nikudon.jpg`              | Obihiro Nikudon       |
| `waduk_brigif.jpg`                 | Waduk Brigif          |
| `taman_ismail_marzuki.jpg`         | Taman Ismail Marzuki  |
| `profile.jpg`                      | Foto profil           |

Jika gambar tidak ada, aplikasi tetap berjalan dengan menampilkan **placeholder abu-abu**.

---

## Mapping Arsitektur: Android → Swing

| Android                          | Java Swing Desktop                        |
|----------------------------------|-------------------------------------------|
| `AppCompatActivity` (JFrame)     | `JFrame`                                  |
| `Fragment`                       | `JPanel` + `CardLayout`                   |
| `BottomNavigationView`           | `JPanel` + `JButton` (GridLayout 1x3)     |
| `NavController` (navigasi)       | `CardLayout.show(contentArea, key)`        |
| `RecyclerView` + `Adapter`       | `JScrollPane` + `JPanel` + `PlaceCardPanel`|
| `ViewHolder`                     | `PlaceCardPanel` (custom JPanel)           |
| `BottomSheetDialogFragment`      | `JDialog` (modal)                          |
| `SharedPreferences`              | `java.util.prefs.Preferences`              |
| `Handler.postDelayed` (debounce) | `javax.swing.Timer`                        |
| `Intent` + `ACTION_VIEW` (Maps)  | `Desktop.getDesktop().browse(URI)`         |
| `Snackbar`                       | `JOptionPane.showMessageDialog`            |
| `MaterialAlertDialogBuilder`     | `JOptionPane.showConfirmDialog`            |
| `ChipGroup` + `Chip`             | `ButtonGroup` + `JToggleButton`            |
| `PopupMenu`                      | `JPopupMenu` + `JMenuItem`                 |
| `R.drawable.*` (int resource ID) | `String fileName` + `ImageIO.read()`       |
| `Parcelable`                     | *Dihapus* (tidak diperlukan di desktop)    |
| `Context`                        | *Dihapus* dari semua method signature      |

---

## Optional: Tampilan Lebih Modern dengan FlatLaf

Jika ingin tampilan mirip Material Design, tambahkan FlatLaf:

1. Download `flatlaf-x.x.x.jar` dari: https://github.com/JFormDesigner/FlatLaf/releases
2. Taruh di folder `lib/`
3. Tambahkan di awal `main()` sebelum `new MainWindow()`:
   ```java
   com.formdev.flatlaf.FlatLightLaf.setup(); // atau FlatDarkLaf
   ```
4. Compile + run dengan menambahkan JAR ke classpath:
   ```bash
   javac -cp lib/flatlaf-x.x.x.jar -d out -sourcepath src src/com/skenatrack/ui/MainWindow.java
   java -cp out:lib/flatlaf-x.x.x.jar com.skenatrack.ui.MainWindow
   # Windows: ganti ":" dengan ";"
   ```

---

## Catatan OOP

Semua prinsip OOP dari versi Android **dipertahankan penuh**:

- **Encapsulation** — `Place` tetap punya semua field `private final` + getters
- **Inheritance** — `PlaceCardPanel` extends `JPanel`, `HomePanel` extends `JPanel`
- **Polymorphism** — `OnItemClickListener` interface dipakai via lambda di `HomePanel`
- **Abstraction** — `FavoriteManager` menyembunyikan detail `Preferences` API
