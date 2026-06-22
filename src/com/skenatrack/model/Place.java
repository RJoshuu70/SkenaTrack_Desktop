package com.skenatrack.model;

/**
 * Model data untuk sebuah tempat wisata/kuliner.
 *
 * Perbedaan dari versi Android:
 * - Dihapus: implements Parcelable (tidak relevan di desktop; Parcelable adalah
 *   mekanisme serialisasi antar-komponen khusus Android/IPC).
 * - imageRes (int R.drawable.*) diganti imageFileName (String nama file),
 *   karena di desktop kita load gambar dari classpath/resources folder.
 */
public class Place {

    private final String        name;
    private final PlaceCategory category;
    private final String        location;
    private final float         rating;
    private final String        imageFileName; // ex: "kopi_nako.jpg"
    private final String        mapUrl;
    private final String        description;

    public Place(String name, PlaceCategory category, String location,
                 float rating, String imageFileName, String mapUrl, String description) {
        this.name          = name;
        this.category      = category;
        this.location      = location;
        this.rating        = rating;
        this.imageFileName = imageFileName;
        this.mapUrl        = mapUrl;
        this.description   = description;
    }

    // Overload tanpa description
    public Place(String name, PlaceCategory category, String location,
                 float rating, String imageFileName, String mapUrl) {
        this(name, category, location, rating, imageFileName, mapUrl, "");
    }

    public String        getName()         { return name; }
    public PlaceCategory getCategory()     { return category; }
    public String        getLocation()     { return location; }
    public float         getRating()       { return rating; }
    public String        getImageFileName(){ return imageFileName; }
    public String        getMapUrl()       { return mapUrl; }
    public String        getDescription()  { return description; }
}
