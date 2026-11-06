/**
 * KalkulatorTagihan
 * -----------------
 * Kumpulan method perhitungan dan validasi yang TIDAK bergantung pada
 * keadaan (state) objek mana pun. Semua hasil hanya ditentukan oleh
 * parameter yang dikirim, sehingga method dibuat public static.
 *
 * Satuan uang: Rupiah utuh (long), karena Rupiah tidak memakai sen dan
 * long menghindari galat pembulatan bilangan pecahan (double).
 */
public final class KalkulatorTagihan {

    // ===== Aturan bisnis (konstanta) =====
    public static final long HARGA_MIN = 1L;               // Rp1
    public static final long HARGA_MAX = 100_000_000L;     // Rp100 juta per unit
    public static final int  JUMLAH_MIN = 1;               // minimal beli 1 unit
    public static final int  JUMLAH_MAX = 100;             // maksimal 100 unit per item

    public static final long AMBANG_DISKON_1 = 500_000L;   // >= Rp500.000 -> 5%
    public static final long AMBANG_DISKON_2 = 1_000_000L; // >= Rp1.000.000 -> 10%
    public static final int  PERSEN_DISKON_1 = 5;
    public static final int  PERSEN_DISKON_2 = 10;

    public static final long TARIF_JABODETABEK = 10_000L;  // per kg
    public static final long TARIF_JAWA        = 15_000L;  // per kg
    public static final long TARIF_LUAR_JAWA   = 25_000L;  // per kg

    // private constructor: class utilitas tidak perlu dibuat objeknya
    private KalkulatorTagihan() { }

    // ===== Validasi =====

    /** true jika jumlah berada di rentang 1..100. */
    public static boolean validasiJumlah(int jumlah) {
        return jumlah >= JUMLAH_MIN && jumlah <= JUMLAH_MAX;
    }

    /**
     * OVERLOADING: versi untuk data mentah dari formulir browser (String).
     * Input dari browser tidak boleh dipercaya: bisa kosong, berisi huruf,
     * angka desimal, atau spasi. Semua kasus itu dianggap tidak valid.
     */
    public static boolean validasiJumlah(String jumlahDariForm) {
        if (jumlahDariForm == null) return false;
        try {
            int jumlah = Integer.parseInt(jumlahDariForm.trim());
            return validasiJumlah(jumlah);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** true jika harga berada di rentang Rp1..Rp100.000.000. */
    public static boolean validasiHarga(long harga) {
        return harga >= HARGA_MIN && harga <= HARGA_MAX;
    }

    /** OVERLOADING: versi untuk data mentah dari formulir browser (String). */
    public static boolean validasiHarga(String hargaDariForm) {
        if (hargaDariForm == null) return false;
        try {
            long harga = Long.parseLong(hargaDariForm.trim());
            return validasiHarga(harga);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ===== Perhitungan =====

    /**
     * Subtotal satu item = harga x jumlah.
     * Melempar IllegalArgumentException bila input tidak valid, agar
     * angka yang salah tidak pernah "diam-diam" masuk ke total.
     */
    public static long hitungSubtotal(long harga, int jumlah) {
        if (!validasiHarga(harga)) {
            throw new IllegalArgumentException("Harga tidak valid: " + harga);
        }
        if (!validasiJumlah(jumlah)) {
            throw new IllegalArgumentException("Jumlah tidak valid: " + jumlah);
        }
        return Math.multiplyExact(harga, (long) jumlah); // aman dari overflow
    }

    /**
     * Diskon bertingkat berdasarkan nilai transaksi:
     *   total >= Rp1.000.000 -> 10%
     *   total >= Rp500.000   -> 5%
     *   selain itu           -> 0
     */
    public static long hitungDiskon(long totalBelanja) {
        if (totalBelanja < 0) {
            throw new IllegalArgumentException("Total belanja negatif: " + totalBelanja);
        }
        if (totalBelanja >= AMBANG_DISKON_2) {
            return totalBelanja * PERSEN_DISKON_2 / 100;
        } else if (totalBelanja >= AMBANG_DISKON_1) {
            return totalBelanja * PERSEN_DISKON_1 / 100;
        }
        return 0L;
    }

    /**
     * Ongkos kirim = tarif zona x berat (kg, dibulatkan ke atas, minimal 1 kg).
     * Zona ditentukan dari kota tujuan.
     */
    public static long hitungOngkir(String kotaTujuan, double berat) {
        if (kotaTujuan == null || kotaTujuan.isBlank()) {
            throw new IllegalArgumentException("Kota tujuan wajib diisi");
        }
        if (berat <= 0 || Double.isNaN(berat) || Double.isInfinite(berat)) {
            throw new IllegalArgumentException("Berat tidak valid: " + berat);
        }
        long beratDitagih = Math.max(1L, (long) Math.ceil(berat));
        return tarifPerKg(kotaTujuan) * beratDitagih;
    }

    /** Helper internal: menentukan tarif per kg berdasarkan kota. */
    private static long tarifPerKg(String kotaTujuan) {
        String kota = kotaTujuan.trim().toLowerCase();
        switch (kota) {
            case "jakarta": case "bogor": case "depok":
            case "tangerang": case "bekasi":
                return TARIF_JABODETABEK;
            case "bandung": case "sukabumi": case "semarang":
            case "yogyakarta": case "surabaya": case "malang":
                return TARIF_JAWA;
            default:
                return TARIF_LUAR_JAWA;
        }
    }
}
