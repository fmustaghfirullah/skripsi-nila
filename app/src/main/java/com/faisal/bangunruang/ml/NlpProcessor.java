package com.faisal.bangunruang.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.faisal.bangunruang.utils.ShapeData;

public class NlpProcessor {

    private static final Map<String, List<Pattern>> INTENT_PATTERNS = new HashMap<>();
    private static final Map<String, Pattern> ENTITY_PATTERNS = new HashMap<>();
    private static final Map<String, Map<String, String>> SPECIFIC_RESPONSES = new HashMap<>();

    static {
        // ===== Intent Patterns =====

        List<Pattern> rumusPatterns = new ArrayList<>();
        rumusPatterns.add(Pattern.compile("(?i).*(rumus|formula|hitung|menghitung|cara hitung).*"));
        rumusPatterns.add(Pattern.compile("(?i).*(volume|luas permukaan|luas alas|keliling).*"));
        rumusPatterns.add(Pattern.compile("(?i).*(berapa|hitungan).*"));
        INTENT_PATTERNS.put("tanya_rumus", rumusPatterns);

        List<Pattern> sifatPatterns = new ArrayList<>();
        sifatPatterns.add(Pattern.compile("(?i).*(sifat|ciri|karakteristik|properti).*"));
        sifatPatterns.add(Pattern.compile("(?i).*(memiliki|mempunyai|punya).*(sisi|rusuk|titik sudut).*"));
        sifatPatterns.add(Pattern.compile("(?i).*(berapa).*(sisi|rusuk|titik sudut|bidang).*"));
        sifatPatterns.add(Pattern.compile("(?i).*(sisi|rusuk|titik sudut|sudut|alas|tutup|selimut|puncak|diagonal).*"));
        INTENT_PATTERNS.put("tanya_sifat", sifatPatterns);

        List<Pattern> contohPatterns = new ArrayList<>();
        contohPatterns.add(Pattern.compile("(?i).*(contoh|contohnya|contoh benda|benda.*sehari).*"));
        contohPatterns.add(Pattern.compile("(?i).*(benda.*berbentuk|bentuk.*benda|kehidupan sehari).*"));
        contohPatterns.add(Pattern.compile("(?i).*(apa saja|sebutkan).*benda.*"));
        INTENT_PATTERNS.put("tanya_contoh", contohPatterns);

        List<Pattern> definisiPatterns = new ArrayList<>();
        definisiPatterns.add(Pattern.compile("(?i).*(apa itu|apakah|definisi|pengertian|arti).*"));
        definisiPatterns.add(Pattern.compile("(?i).*(jelaskan|terangkan|deskripsikan).*"));
        definisiPatterns.add(Pattern.compile("(?i).*apa.*yang.*dimaksud.*"));
        INTENT_PATTERNS.put("tanya_definisi", definisiPatterns);

        List<Pattern> jaringPatterns = new ArrayList<>();
        jaringPatterns.add(Pattern.compile("(?i).*(jaring|jaring-jaring|jarring|net).*"));
        jaringPatterns.add(Pattern.compile("(?i).*(bentuk.*buka|buka.*bentuk|dibuka|direntang).*"));
        INTENT_PATTERNS.put("tanya_jaring", jaringPatterns);

        List<Pattern> perbedaanPatterns = new ArrayList<>();
        perbedaanPatterns.add(Pattern.compile("(?i).*(beda|perbedaan|perbedaannya|berbeda|bedanya).*"));
        perbedaanPatterns.add(Pattern.compile("(?i).*(sama|persamaan|mirip|serupa).*"));
        perbedaanPatterns.add(Pattern.compile("(?i).*(banding|perbandingan|dibandingkan).*"));
        INTENT_PATTERNS.put("tanya_perbedaan", perbedaanPatterns);

        List<Pattern> unsurPatterns = new ArrayList<>();
        unsurPatterns.add(Pattern.compile("(?i).*(unsur|elemen|bagian|komponen).*"));
        unsurPatterns.add(Pattern.compile("(?i).*(terdiri|tersusun|membentuk).*"));
        INTENT_PATTERNS.put("tanya_unsur", unsurPatterns);

        // ===== Entity Patterns =====

        ENTITY_PATTERNS.put("kubus", Pattern.compile("(?i).*(kubus|cube|kotak|dadu).*"));
        ENTITY_PATTERNS.put("balok", Pattern.compile("(?i).*(balok|cuboid|kotak panjang|persegi panjang).*"));
        ENTITY_PATTERNS.put("kerucut", Pattern.compile("(?i).*(kerucut|cone|krucut|lancip).*"));
        ENTITY_PATTERNS.put("tabung", Pattern.compile("(?i).*(tabung|silinder|cylinder|drum).*"));
        ENTITY_PATTERNS.put("bola", Pattern.compile("(?i).*(bola|sphere|bulat|globe).*"));

        // ===== Specific Responses (C#-style direct answers) =====

        Map<String, String> kubus = new HashMap<>();
        kubus.put("sisi", "Kubus memiliki 6 sisi yang berbentuk persegi dan kongruen (sama besar).");
        kubus.put("rusuk", "Kubus memiliki 12 rusuk yang sama panjang.");
        kubus.put("sudut", "Kubus memiliki 8 titik sudut. Setiap sudut dibentuk oleh pertemuan 3 rusuk yang saling tegak lurus.");
        kubus.put("volume", "Rumus volume kubus:\nV = s × s × s = s³\n\nDimana s = panjang sisi kubus.");
        kubus.put("luas", "Rumus luas permukaan kubus:\nL = 6 × s²\n\nDimana s = panjang sisi kubus.");
        kubus.put("contoh", "Contoh benda berbentuk kubus:\n• Dadu\n• Rubik\n• Kotak kado\n• Es batu");
        kubus.put("diagonal", "Kubus memiliki:\n• 12 diagonal bidang (sisi)\n• 4 diagonal ruang\n• 6 bidang diagonal");
        kubus.put("umum", "Kubus adalah bangun ruang yang memiliki 6 sisi persegi kongruen, 12 rusuk sama panjang, dan 8 titik sudut.");
        SPECIFIC_RESPONSES.put("kubus", kubus);

        Map<String, String> balok = new HashMap<>();
        balok.put("sisi", "Balok memiliki 6 sisi berbentuk persegi panjang, dengan sisi-sisi yang berhadapan sama besar (kongruen).");
        balok.put("rusuk", "Balok memiliki 12 rusuk yang terdiri dari:\n• 4 rusuk panjang (p)\n• 4 rusuk lebar (l)\n• 4 rusuk tinggi (t)");
        balok.put("sudut", "Balok memiliki 8 titik sudut. Setiap sudut dibentuk oleh pertemuan 3 rusuk.");
        balok.put("volume", "Rumus volume balok:\nV = p × l × t\n\nDimana p = panjang, l = lebar, t = tinggi.");
        balok.put("luas", "Rumus luas permukaan balok:\nL = 2 × (pl + pt + lt)\n\nDimana p = panjang, l = lebar, t = tinggi.");
        balok.put("contoh", "Contoh benda berbentuk balok:\n• Lemari\n• Kulkas\n• Batu bata\n• Kotak sepatu\n• Buku");
        balok.put("diagonal", "Balok memiliki:\n• 12 diagonal bidang\n• 4 diagonal ruang\n• 6 bidang diagonal");
        balok.put("umum", "Balok adalah bangun ruang yang memiliki 6 sisi persegi panjang, 12 rusuk, dan 8 titik sudut.");
        SPECIFIC_RESPONSES.put("balok", balok);

        Map<String, String> bola = new HashMap<>();
        bola.put("sisi", "Bola memiliki 1 sisi lengkung (bidang lengkung) tanpa sisi datar.");
        bola.put("rusuk", "Bola tidak memiliki rusuk.");
        bola.put("sudut", "Bola tidak memiliki titik sudut.");
        bola.put("volume", "Rumus volume bola:\nV = 4/3 × π × r³\n\nDimana r = jari-jari bola, π ≈ 3,14.");
        bola.put("luas", "Rumus luas permukaan bola:\nL = 4 × π × r²\n\nDimana r = jari-jari bola, π ≈ 3,14.");
        bola.put("contoh", "Contoh benda berbentuk bola:\n• Kelereng\n• Bola basket\n• Bola sepak\n• Globe/bola dunia\n• Semangka");
        bola.put("umum", "Bola adalah bangun ruang yang memiliki 1 sisi lengkung, tanpa rusuk dan tanpa titik sudut.");
        SPECIFIC_RESPONSES.put("bola", bola);

        Map<String, String> tabung = new HashMap<>();
        tabung.put("sisi", "Tabung memiliki 3 sisi:\n• 1 sisi alas (lingkaran)\n• 1 sisi tutup (lingkaran)\n• 1 sisi selimut (lengkung)");
        tabung.put("rusuk", "Tabung memiliki 2 rusuk lengkung yaitu rusuk alas dan rusuk tutup.");
        tabung.put("sudut", "Tabung tidak memiliki titik sudut.");
        tabung.put("alas", "Alas dan tutup tabung berbentuk lingkaran yang kongruen (sama besar) dan sejajar.");
        tabung.put("selimut", "Selimut tabung berbentuk persegi panjang jika dibuka/direntangkan, dengan panjang = keliling lingkaran alas dan lebar = tinggi tabung.");
        tabung.put("volume", "Rumus volume tabung:\nV = π × r² × t\n\nDimana r = jari-jari alas, t = tinggi, π ≈ 3,14.");
        tabung.put("luas", "Rumus luas permukaan tabung:\nL = 2 × π × r × (r + t)\n\nDimana r = jari-jari alas, t = tinggi.");
        tabung.put("contoh", "Contoh benda berbentuk tabung:\n• Kaleng susu\n• Pipa\n• Gelas\n• Drum\n• Celengan");
        tabung.put("umum", "Tabung adalah bangun ruang yang memiliki 3 sisi (2 lingkaran + 1 selimut), 2 rusuk lengkung, dan tidak memiliki titik sudut.");
        SPECIFIC_RESPONSES.put("tabung", tabung);

        Map<String, String> kerucut = new HashMap<>();
        kerucut.put("sisi", "Kerucut memiliki 2 sisi:\n• 1 sisi alas (lingkaran)\n• 1 sisi selimut (lengkung)");
        kerucut.put("rusuk", "Kerucut memiliki 1 rusuk lengkung yaitu rusuk alas.");
        kerucut.put("sudut", "Kerucut memiliki 1 titik puncak (titik sudut) di bagian atas.");
        kerucut.put("puncak", "Kerucut memiliki 1 titik puncak yang merupakan ujung atas kerucut.");
        kerucut.put("alas", "Alas kerucut berbentuk lingkaran.");
        kerucut.put("selimut", "Selimut kerucut berbentuk juring lingkaran jika dibuka/direntangkan.");
        kerucut.put("volume", "Rumus volume kerucut:\nV = 1/3 × π × r² × t\n\nDimana r = jari-jari alas, t = tinggi, π ≈ 3,14.");
        kerucut.put("luas", "Rumus luas permukaan kerucut:\nL = π × r × (r + s)\n\nDimana r = jari-jari alas, s = garis pelukis.");
        kerucut.put("contoh", "Contoh benda berbentuk kerucut:\n• Topi ulang tahun\n• Tumpeng\n• Cone es krim\n• Corong\n• Nasi tumpeng");
        kerucut.put("umum", "Kerucut adalah bangun ruang yang memiliki 2 sisi (alas dan selimut), 1 rusuk lengkung, dan 1 titik puncak.");
        SPECIFIC_RESPONSES.put("kerucut", kerucut);
    }

    public String processQuery(String query) {
        String intent = detectIntent(query);
        String entity = detectEntity(query);

        if (entity == null) {
            return generateGeneralResponse(intent, query);
        }

        String specificResponse = trySpecificResponse(entity, query);
        if (specificResponse != null) {
            return specificResponse;
        }

        return generateResponse(intent, entity);
    }

    private String trySpecificResponse(String entity, String query) {
        String lower = query.toLowerCase();
        Map<String, String> responses = SPECIFIC_RESPONSES.get(entity);
        if (responses == null) return null;

        if (lower.contains("titik sudut")) return responses.get("sudut");
        if (lower.contains("titik puncak")) return responses.getOrDefault("puncak", responses.get("sudut"));
        if (lower.contains("diagonal")) return responses.getOrDefault("diagonal", null);
        if (lower.contains("selimut")) return responses.getOrDefault("selimut", null);
        if (lower.contains("puncak")) return responses.getOrDefault("puncak", responses.get("sudut"));

        if (containsAny(lower, "volume", "isi")) return responses.get("volume");
        if (lower.contains("luas")) return responses.get("luas");
        if (lower.contains("sisi")) return responses.get("sisi");
        if (lower.contains("rusuk")) return responses.get("rusuk");
        if (lower.contains("sudut")) return responses.get("sudut");
        if (containsAny(lower, "alas", "tutup")) return responses.getOrDefault("alas", null);
        if (containsAny(lower, "contoh", "benda")) return responses.get("contoh");

        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private String detectIntent(String query) {
        for (Map.Entry<String, List<Pattern>> entry : INTENT_PATTERNS.entrySet()) {
            for (Pattern pattern : entry.getValue()) {
                if (pattern.matcher(query).matches()) {
                    return entry.getKey();
                }
            }
        }
        return "tanya_umum";
    }

    private String detectEntity(String query) {
        for (Map.Entry<String, Pattern> entry : ENTITY_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(query).matches()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String generateResponse(String intent, String entity) {
        ShapeData.ShapeInfo info = ShapeData.getShapeInfo(entity);
        if (info == null) {
            return "Maaf, saya tidak memiliki informasi tentang bangun ruang tersebut.";
        }

        switch (intent) {
            case "tanya_rumus":
                return generateRumusResponse(info);
            case "tanya_sifat":
                return generateSifatResponse(info);
            case "tanya_contoh":
                return generateContohResponse(info);
            case "tanya_definisi":
                return generateDefinisiResponse(info);
            case "tanya_jaring":
                return generateJaringResponse(info);
            case "tanya_unsur":
                return generateUnsurResponse(info);
            case "tanya_perbedaan":
                return generateDefinisiResponse(info) + "\n\nUntuk perbandingan lebih detail, silakan tanyakan perbedaan antara dua bangun ruang tertentu.";
            default:
                Map<String, String> responses = SPECIFIC_RESPONSES.get(entity);
                if (responses != null && responses.containsKey("umum")) {
                    return responses.get("umum");
                }
                return generateDefinisiResponse(info);
        }
    }

    private String generateRumusResponse(ShapeData.ShapeInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rumus ").append(info.getName()).append(":\n\n");
        sb.append("• Volume: ").append(info.getVolumeFormula()).append("\n");
        sb.append("• Luas Permukaan: ").append(info.getSurfaceAreaFormula()).append("\n");
        if (info.getBaseAreaFormula() != null) {
            sb.append("• Luas Alas: ").append(info.getBaseAreaFormula()).append("\n");
        }
        sb.append("\nKeterangan:\n").append(info.getFormulaDescription());
        return sb.toString();
    }

    private String generateSifatResponse(ShapeData.ShapeInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sifat-sifat ").append(info.getName()).append(":\n\n");
        for (String sifat : info.getProperties()) {
            sb.append("• ").append(sifat).append("\n");
        }
        return sb.toString();
    }

    private String generateContohResponse(ShapeData.ShapeInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Contoh benda berbentuk ").append(info.getName()).append(" dalam kehidupan sehari-hari:\n\n");
        for (String contoh : info.getExamples()) {
            sb.append("• ").append(contoh).append("\n");
        }
        return sb.toString();
    }

    private String generateDefinisiResponse(ShapeData.ShapeInfo info) {
        return info.getName() + " adalah " + info.getDefinition();
    }

    private String generateJaringResponse(ShapeData.ShapeInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Jaring-jaring ").append(info.getName()).append(":\n\n");
        sb.append(info.getNetDescription());
        return sb.toString();
    }

    private String generateUnsurResponse(ShapeData.ShapeInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unsur-unsur ").append(info.getName()).append(":\n\n");
        for (String unsur : info.getElements()) {
            sb.append("• ").append(unsur).append("\n");
        }
        return sb.toString();
    }

    private String generateGeneralResponse(String intent, String query) {
        if (intent.equals("tanya_perbedaan")) {
            return "Berikut perbedaan umum bangun ruang:\n\n" +
                    "• Kubus: Semua sisi berbentuk persegi sama besar\n" +
                    "• Balok: Sisi berbentuk persegi panjang, tidak harus sama besar\n" +
                    "• Kerucut: Memiliki alas lingkaran dan satu titik puncak\n" +
                    "• Tabung: Memiliki dua alas lingkaran yang sejajar\n" +
                    "• Bola: Semua titik permukaannya berjarak sama dari pusat\n\n" +
                    "Silakan tanyakan secara spesifik bangun ruang mana yang ingin kamu ketahui lebih lanjut!";
        }

        return "Halo! Saya bisa membantu kamu belajar tentang bangun ruang!\n\n" +
                "Kamu bisa bertanya tentang:\n" +
                "• Sisi → \"Berapa sisi kubus?\"\n" +
                "• Rusuk → \"Berapa rusuk balok?\"\n" +
                "• Titik sudut → \"Sudut tabung?\"\n" +
                "• Volume → \"Rumus volume bola?\"\n" +
                "• Luas → \"Luas permukaan kerucut?\"\n" +
                "• Contoh benda → \"Contoh benda kubus?\"\n" +
                "• Jaring-jaring → \"Jaring-jaring balok?\"\n" +
                "• Sifat → \"Sifat tabung?\"\n" +
                "• Unsur → \"Unsur kerucut?\"\n\n" +
                "Bangun ruang yang tersedia: Kubus, Balok, Kerucut, Tabung, dan Bola.";
    }
}
