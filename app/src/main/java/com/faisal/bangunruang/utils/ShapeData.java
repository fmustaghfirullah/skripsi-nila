package com.faisal.bangunruang.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapeData {

    private static final Map<String, ShapeInfo> SHAPE_DATABASE = new HashMap<>();

    static {
        // KUBUS
        SHAPE_DATABASE.put("kubus", new ShapeInfo(
                "Kubus",
                "bangun ruang tiga dimensi yang dibatasi oleh enam bidang sisi yang berbentuk persegi (bujur sangkar) yang kongruen (sama dan sebangun). Kubus merupakan bentuk khusus dari prisma segi empat.",
                Arrays.asList(
                        "Memiliki 6 sisi berbentuk persegi yang kongruen",
                        "Memiliki 12 rusuk yang sama panjang",
                        "Memiliki 8 titik sudut",
                        "Memiliki 12 diagonal sisi",
                        "Memiliki 4 diagonal ruang yang sama panjang",
                        "Memiliki 6 bidang diagonal berbentuk persegi panjang",
                        "Setiap sudut bernilai 90 derajat",
                        "Memiliki 3 pasang sisi yang sejajar"
                ),
                "V = s³ (sisi pangkat tiga)",
                "L = 6 × s² (6 kali sisi kuadrat)",
                "La = s² (sisi kuadrat)",
                "Keterangan:\n• s = panjang sisi kubus\n• V = volume\n• L = luas permukaan\n• Diagonal sisi = s√2\n• Diagonal ruang = s√3",
                Arrays.asList(
                        "Dadu",
                        "Rubik's cube",
                        "Kotak kado berbentuk kubus",
                        "Es batu",
                        "Kotak tisu berbentuk kubus",
                        "Akuarium berbentuk kubus"
                ),
                "Jaring-jaring kubus terdiri dari 6 buah persegi yang kongruen (sama besar) yang saling berhubungan. Terdapat 11 macam jaring-jaring kubus yang berbeda. Salah satu bentuk yang paling umum adalah susunan berbentuk salib (1 persegi di tengah atas, 4 persegi berjajar di tengah, dan 1 persegi di bawah).",
                Arrays.asList(
                        "Sisi: 6 buah berbentuk persegi kongruen",
                        "Rusuk: 12 buah dengan panjang yang sama",
                        "Titik sudut: 8 buah",
                        "Diagonal sisi: 12 buah",
                        "Diagonal ruang: 4 buah",
                        "Bidang diagonal: 6 buah berbentuk persegi panjang"
                )
        ));

        // BALOK
        SHAPE_DATABASE.put("balok", new ShapeInfo(
                "Balok",
                "bangun ruang tiga dimensi yang dibatasi oleh tiga pasang sisi berhadapan yang masing-masing berbentuk persegi panjang. Balok memiliki panjang, lebar, dan tinggi yang tidak harus sama.",
                Arrays.asList(
                        "Memiliki 6 sisi berbentuk persegi panjang",
                        "Sisi yang berhadapan kongruen (sama dan sebangun)",
                        "Memiliki 12 rusuk, terdiri dari 3 kelompok (4 panjang, 4 lebar, 4 tinggi)",
                        "Memiliki 8 titik sudut",
                        "Memiliki 12 diagonal sisi",
                        "Memiliki 4 diagonal ruang yang sama panjang",
                        "Memiliki 6 bidang diagonal",
                        "Setiap sudut bernilai 90 derajat"
                ),
                "V = p × l × t",
                "L = 2(pl + pt + lt)",
                "La = p × l",
                "Keterangan:\n• p = panjang balok\n• l = lebar balok\n• t = tinggi balok\n• V = volume\n• L = luas permukaan\n• Diagonal ruang = √(p² + l² + t²)",
                Arrays.asList(
                        "Lemari",
                        "Kulkas",
                        "Buku",
                        "Batu bata",
                        "Kotak sepatu",
                        "Akuarium",
                        "Kardus/karton"
                ),
                "Jaring-jaring balok terdiri dari 6 buah persegi panjang, dengan 3 pasang persegi panjang yang kongruen (pasangan sisi berhadapan). Terdapat 54 macam jaring-jaring balok yang berbeda. Untuk membentuk balok, setiap sisi harus dapat dilipat dan saling bertemu membentuk ruang tertutup.",
                Arrays.asList(
                        "Sisi: 6 buah berbentuk persegi panjang (3 pasang kongruen)",
                        "Rusuk: 12 buah (4 panjang, 4 lebar, 4 tinggi)",
                        "Titik sudut: 8 buah",
                        "Diagonal sisi: 12 buah",
                        "Diagonal ruang: 4 buah",
                        "Bidang diagonal: 6 buah berbentuk persegi panjang"
                )
        ));

        // KERUCUT
        SHAPE_DATABASE.put("kerucut", new ShapeInfo(
                "Kerucut",
                "bangun ruang tiga dimensi yang memiliki alas berbentuk lingkaran dan selimut berbentuk bidang lengkung yang meruncing ke satu titik puncak.",
                Arrays.asList(
                        "Memiliki 1 sisi alas berbentuk lingkaran",
                        "Memiliki 1 sisi selimut berbentuk bidang lengkung",
                        "Memiliki 1 titik puncak",
                        "Memiliki 1 rusuk lengkung (keliling alas)",
                        "Tidak memiliki titik sudut",
                        "Memiliki garis pelukis (s) yaitu garis dari puncak ke tepi alas",
                        "Tinggi kerucut tegak lurus dari puncak ke pusat alas",
                        "Penampang kerucut yang sejajar alas berbentuk lingkaran"
                ),
                "V = ⅓ × π × r² × t",
                "L = π × r × (r + s)",
                "La = π × r²",
                "Keterangan:\n• r = jari-jari alas\n• t = tinggi kerucut\n• s = garis pelukis = √(r² + t²)\n• π = 3,14 atau 22/7\n• Luas selimut = π × r × s",
                Arrays.asList(
                        "Topi ulang tahun",
                        "Cone es krim",
                        "Corong/funnel",
                        "Tumpeng",
                        "Traffic cone (kerucut lalu lintas)",
                        "Nasi tumpeng",
                        "Atap menara"
                ),
                "Jaring-jaring kerucut terdiri dari 2 bagian:\n1. Satu buah lingkaran (sebagai alas)\n2. Satu buah juring lingkaran (sebagai selimut)\n\nJuring lingkaran memiliki jari-jari sama dengan garis pelukis (s) kerucut, dan panjang busurnya sama dengan keliling alas kerucut (2πr).",
                Arrays.asList(
                        "Alas: 1 buah berbentuk lingkaran",
                        "Selimut: 1 buah berbentuk bidang lengkung",
                        "Titik puncak: 1 buah",
                        "Rusuk lengkung: 1 buah",
                        "Tinggi (t): jarak dari puncak ke pusat alas",
                        "Garis pelukis (s): garis dari puncak ke tepi alas",
                        "Jari-jari (r): jari-jari lingkaran alas"
                )
        ));

        // TABUNG
        SHAPE_DATABASE.put("tabung", new ShapeInfo(
                "Tabung",
                "bangun ruang tiga dimensi yang memiliki tutup dan alas berbentuk lingkaran yang sama besar dan sejajar, serta dihubungkan oleh selimut berbentuk persegi panjang yang dilengkungkan. Tabung disebut juga silinder.",
                Arrays.asList(
                        "Memiliki 2 sisi alas dan tutup berbentuk lingkaran kongruen",
                        "Memiliki 1 sisi selimut berbentuk persegi panjang (jika dibuka)",
                        "Memiliki 2 rusuk lengkung (keliling alas dan tutup)",
                        "Tidak memiliki titik sudut",
                        "Alas dan tutup sejajar dan kongruen",
                        "Tinggi tabung tegak lurus terhadap alas dan tutup",
                        "Penampang tabung yang sejajar alas selalu berbentuk lingkaran",
                        "Memiliki simetri putar tak hingga"
                ),
                "V = π × r² × t",
                "L = 2 × π × r × (r + t)",
                "La = π × r²",
                "Keterangan:\n• r = jari-jari alas/tutup\n• t = tinggi tabung\n• π = 3,14 atau 22/7\n• Luas selimut = 2 × π × r × t\n• Keliling alas = 2 × π × r",
                Arrays.asList(
                        "Kaleng minuman",
                        "Pipa paralon",
                        "Drum",
                        "Tabung gas elpiji",
                        "Gelas (tanpa pegangan)",
                        "Cerobong asap",
                        "Roller cat"
                ),
                "Jaring-jaring tabung terdiri dari 3 bagian:\n1. Dua buah lingkaran (sebagai alas dan tutup)\n2. Satu buah persegi panjang (sebagai selimut)\n\nPersegi panjang pada selimut memiliki panjang = keliling lingkaran alas (2πr) dan lebar = tinggi tabung (t).",
                Arrays.asList(
                        "Alas: 1 buah berbentuk lingkaran",
                        "Tutup: 1 buah berbentuk lingkaran (kongruen dengan alas)",
                        "Selimut: 1 buah berbentuk persegi panjang (jika dibuka)",
                        "Rusuk lengkung: 2 buah",
                        "Tinggi (t): jarak antara alas dan tutup",
                        "Jari-jari (r): jari-jari lingkaran alas/tutup",
                        "Diameter (d): 2 × jari-jari"
                )
        ));

        // BOLA
        SHAPE_DATABASE.put("bola", new ShapeInfo(
                "Bola",
                "bangun ruang tiga dimensi yang dibatasi oleh satu bidang lengkung, di mana semua titik pada permukaan bola berjarak sama (sama dengan jari-jari) dari satu titik pusat. Bola merupakan bangun ruang yang paling simetris.",
                Arrays.asList(
                        "Memiliki 1 sisi berupa bidang lengkung tertutup",
                        "Tidak memiliki rusuk",
                        "Tidak memiliki titik sudut",
                        "Memiliki jari-jari (r) yang sama ke segala arah",
                        "Memiliki diameter (d = 2r)",
                        "Memiliki simetri putar tak hingga pada setiap sumbu yang melalui pusat",
                        "Setiap irisan yang melalui pusat berbentuk lingkaran besar",
                        "Tidak memiliki jaring-jaring dalam arti sebenarnya"
                ),
                "V = ⁴⁄₃ × π × r³",
                "L = 4 × π × r²",
                null,
                "Keterangan:\n• r = jari-jari bola\n• d = diameter = 2r\n• π = 3,14 atau 22/7\n• Bola tidak memiliki luas alas karena tidak memiliki bidang datar",
                Arrays.asList(
                        "Bola sepak",
                        "Bola basket",
                        "Globe/bola dunia",
                        "Kelereng",
                        "Bola tenis",
                        "Planet dan bulan (mendekati bola)",
                        "Balon yang ditiup bulat"
                ),
                "Bola tidak memiliki jaring-jaring dalam arti yang sebenarnya, karena bola hanya memiliki satu sisi berupa bidang lengkung yang tidak dapat direntangkan menjadi bidang datar tanpa distorsi.\n\nNamun, dalam kartografi (pembuatan peta), permukaan bola dapat diproyeksikan menjadi bidang datar menggunakan berbagai metode proyeksi (seperti proyeksi Mercator), meskipun selalu ada distorsi.",
                Arrays.asList(
                        "Permukaan: 1 buah bidang lengkung tertutup",
                        "Titik pusat: 1 buah (pusat bola)",
                        "Jari-jari (r): jarak dari pusat ke permukaan",
                        "Diameter (d): garis lurus melalui pusat, d = 2r",
                        "Lingkaran besar: irisan melalui pusat bola",
                        "Lingkaran kecil: irisan tidak melalui pusat bola",
                        "Tidak memiliki rusuk dan titik sudut"
                )
        ));
    }

    public static ShapeInfo getShapeInfo(String shapeId) {
        return SHAPE_DATABASE.get(shapeId.toLowerCase());
    }

    public static class ShapeInfo {
        private String name;
        private String definition;
        private List<String> properties;
        private String volumeFormula;
        private String surfaceAreaFormula;
        private String baseAreaFormula;
        private String formulaDescription;
        private List<String> examples;
        private String netDescription;
        private List<String> elements;

        public ShapeInfo(String name, String definition, List<String> properties,
                         String volumeFormula, String surfaceAreaFormula, String baseAreaFormula,
                         String formulaDescription, List<String> examples,
                         String netDescription, List<String> elements) {
            this.name = name;
            this.definition = definition;
            this.properties = properties;
            this.volumeFormula = volumeFormula;
            this.surfaceAreaFormula = surfaceAreaFormula;
            this.baseAreaFormula = baseAreaFormula;
            this.formulaDescription = formulaDescription;
            this.examples = examples;
            this.netDescription = netDescription;
            this.elements = elements;
        }

        public String getName() { return name; }
        public String getDefinition() { return definition; }
        public List<String> getProperties() { return properties; }
        public String getVolumeFormula() { return volumeFormula; }
        public String getSurfaceAreaFormula() { return surfaceAreaFormula; }
        public String getBaseAreaFormula() { return baseAreaFormula; }
        public String getFormulaDescription() { return formulaDescription; }
        public List<String> getExamples() { return examples; }
        public String getNetDescription() { return netDescription; }
        public List<String> getElements() { return elements; }
    }
}
