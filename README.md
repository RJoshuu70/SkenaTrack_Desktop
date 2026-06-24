# SkenaTrack Desktop (Java Swing)

Versi desktop dari aplikasi Android SkenaTrack, dikonversi menggunakan **Java Swing** sebagai GUI toolkit.
Aplikasi ini sudah mendukung integrasi dengan **Database SQLite** untuk menyimpan data secara persisten, sehingga memenuhi syarat "menampilkan data dari database" untuk UAS PBO.

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
│       │   └── DataSource.java      ← Manajemen data via koneksi SQLite
│       ├── utils/
│       │   ├── FavoriteManager.java ← Persistensi favorit (Preferences)
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
│   └── images/           ← Folder untuk menyimpan aset gambar
├── lib/
│   └── sqlite-jdbc.jar   ← Driver database SQLite
├── skenatrack.db         ← File database SQLite (tergenerate otomatis)
└── README.md
```

---

## Cara Compile & Run

### Prasyarat
- **Java JDK 11+** terinstall
- Git terinstall (untuk clone repository)

### 1. Clone & Masuk ke Folder Proyek

Buka terminal dan jalankan perintah berikut:
```bash
git clone <URL_REPO_ANDA>
cd SkenaTrack_Desktop
```
*(Catatan: pastikan terminal Anda sudah berada di dalam direktori `SkenaTrack_Desktop` sebelum lanjut ke langkah berikutnya!)*

### 2. Compile semua file Java

Buka terminal yang sudah berada di folder `SkenaTrack_Desktop`, lalu jalankan:

**Windows (Command Prompt / PowerShell):**
```cmd
Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName | Out-File sources.txt; javac -d out -cp "lib/*" @sources.txt
```
Atau jika ingin spesifik satu file saja:
```cmd
javac -d out -cp "lib/*" -sourcepath src src\com\skenatrack\ui\MainWindow.java
```

**macOS / Linux:**
```bash
find src -name "*.java" > sources.txt
javac -d out -cp "lib/*" @sources.txt
```

### 3. Run aplikasi

**Windows:**
```cmd
java -cp "out;lib/*" com.skenatrack.ui.MainWindow
```

**macOS / Linux:**
```bash
java -cp "out:lib/*" com.skenatrack.ui.MainWindow
```

*(Perhatikan perbedaan penggunaan titik koma `;` untuk Windows dan titik dua `:` untuk macOS/Linux).*

---

## Manajemen Database & Gambar

Aplikasi SkenaTrack sekarang menggunakan file `skenatrack.db`. 
- **Auto-Seeding:** Jika file `skenatrack.db` dihapus, aplikasi otomatis akan membuatkan database baru dan memasukkan 16 data tempat default saat pertama kali dijalankan.
- Kamu dapat mengedit datanya langsung menggunakan aplikasi visual seperti **DB Browser for SQLite**.

### Daftar Nama Gambar yang Diperlukan:
Letakkan file gambar di dalam folder `resources/images/`. Foto disarankan memiliki resolusi dengan rasio lebar (misal: 960x400) agar terlihat rapi saat dibuka di halaman detail.

| Kategori | Nama file gambar |
|---|---|
| **CAFE** | `ol_pops_coffee_cinere.jpg`, `62_coffee_space.jpg`, `menanti_senja_cipete.jpg`, `uma_kopi.jpg` |
| **TAMAN** | `taman_literasi.jpg`, `taman_ismail_marzuki.jpg`, `tebet_eco_park.jpg`, `hutan_kota_gbk.jpg` |
| **KULINER** | `obihiro_nikudon.jpg`, `gulai_tikungan.jpg`, `daebak_tokpoki.jpg`, `kedai_penuh_nikmat.jpg` |
| **PERPUSTAKAAN**| `perpustakaan_jakarta.jpg`, `perpustakaan_nasional.jpg`, `baca_di_tebet.jpg`, `grha_aaji_building.jpg` |
| **PROFIL** | `profile.jpg` |

*(Jika ada file gambar yang tidak ditemukan, aplikasi akan menampilkannya dengan kotak placeholder abu-abu otomatis).*

---

## Mapping Arsitektur: Android → Swing

| Konsep Android | Java Swing Desktop |
|---|---|
| `AppCompatActivity` | `JFrame` |
| `Fragment` | `JPanel` + `CardLayout` |
| `BottomNavigationView` | Kustom `JPanel` + Pill-shaped `JButton` |
| `RecyclerView` | `JScrollPane` + `JPanel` |
| `ViewHolder` | `PlaceCardPanel` |
| `BottomSheetDialogFragment`| `JDialog` (modal) |
| `SharedPreferences` | `java.util.prefs.Preferences` |
| `Room / SQLiteOpenHelper` | `JDBC` + `sqlite-jdbc` |
| `ChipGroup` + `Chip` | Kustom Rounded `JToggleButton` |

---

## Catatan OOP

Semua prinsip PBO (Pemrograman Berbasis Objek) berhasil diimplementasikan sesuai tuntutan tugas:
- **Encapsulation** — Field `private`, manipulasi state via `getter/setter`.
- **Inheritance** — Mewarisi dan mengembangkan kelas bawaan Java UI seperti `JPanel` dan `JDialog`.
- **Polymorphism** — Pemanggilan interface dinamis via event listeners.
- **Abstraction** — Penggunaan kelas manajer (seperti `DataSource` dan `FavoriteManager`) untuk menyembunyikan detail kompleksitas SQL dan Preferences dari UI utama.
