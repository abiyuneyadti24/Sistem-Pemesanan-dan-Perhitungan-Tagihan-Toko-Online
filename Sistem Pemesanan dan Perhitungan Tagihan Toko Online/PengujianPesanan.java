/**
 * PengujianPesanan
 * ----------------
 * Menjalankan seluruh test case dan mencetak tabel:
 * No | Skenario | Hasil diharapkan | Hasil aktual | Status.
 * Hasil aktual didapat dari menjalankan kode sungguhan, bukan ditulis manual.
 */
public class PengujianPesanan {

    private static int lulus = 0;
    private static int gagal = 0;

    public static void main(String[] args) {
        System.out.printf("%-4s | %-46s | %-22s | %-22s | %s%n",
                "No", "Skenario", "Diharapkan", "Aktual", "Status");
        System.out.println("-".repeat(112));

        // ===== Transaksi normal =====
        Produk keyboard = new Produk("P001", "Keyboard Mekanik", 250_000, 0.8);
        Produk mouse    = new Produk("P002", "Mouse Wireless", 75_000, 0.2);
        Pesanan normal = new Pesanan("INV-001", "Budi", "Bandung");
        normal.tambahItem(keyboard, 1);
        normal.tambahItem(mouse, 2);
        uji("T01", "Normal: total belanja (1x250rb + 2x75rb)", "400000", normal.hitungTotalBelanja());
        uji("T02", "Normal: diskon (di bawah ambang)", "0", normal.hitungDiskon());
        uji("T03", "Normal: ongkir Bandung 1,2 kg -> 2 kg", "30000", normal.hitungOngkir());
        uji("T04", "Normal: total akhir", "430000", normal.hitungTotalAkhir());

        // ===== Jumlah nol / negatif / berlebih =====
        Pesanan p = new Pesanan("INV-002", "Sari", "Jakarta");
        uji("T05", "tambahItem jumlah = 0", "false", p.tambahItem(mouse, 0));
        uji("T06", "tambahItem jumlah = -5", "false", p.tambahItem(mouse, -5));
        uji("T07", "validasiJumlah(101) melebihi batas", "false", KalkulatorTagihan.validasiJumlah(101));
        uji("T08", "Item ditolak tidak masuk pesanan", "0", p.getDaftarItem().size());

        // ===== Harga negatif / nol =====
        uji("T09", "validasiHarga(-50000)", "false", KalkulatorTagihan.validasiHarga(-50_000));
        uji("T10", "validasiHarga(0)", "false", KalkulatorTagihan.validasiHarga(0));
        uji("T11", "new Produk harga -50000", "DITOLAK",
                coba(() -> new Produk("P999", "Barang Aneh", -50_000, 1.0)));
        uji("T12", "hitungSubtotal(-1000, 2)", "DITOLAK",
                coba(() -> KalkulatorTagihan.hitungSubtotal(-1_000, 2)));

        // ===== Ambang diskon =====
        uji("T13", "hitungDiskon(499999) tepat di bawah ambang", "0", KalkulatorTagihan.hitungDiskon(499_999));
        uji("T14", "hitungDiskon(500000) tepat ambang 5%", "25000", KalkulatorTagihan.hitungDiskon(500_000));
        uji("T15", "hitungDiskon(1000000) tepat ambang 10%", "100000", KalkulatorTagihan.hitungDiskon(1_000_000));

        Produk laptop = new Produk("P003", "Laptop 14 inch", 7_500_000, 2.0);
        Produk tas    = new Produk("P004", "Tas Laptop", 350_000, 0.7);
        Pesanan besar = new Pesanan("INV-003", "Andi", "Makassar");
        besar.tambahItem(laptop, 1);
        besar.tambahItem(tas, 1);
        uji("T16", "Diskon pesanan Rp7.850.000 (10%)", "785000", besar.hitungDiskon());
        uji("T17", "Ongkir Makassar 2,7 kg -> 3 kg", "75000", besar.hitungOngkir());
        uji("T18", "Total akhir pesanan berdiskon", "7140000", besar.hitungTotalAkhir());

        // ===== Input mentah dari formulir browser =====
        uji("T19", "Form jumlah \"abc\"", "false", KalkulatorTagihan.validasiJumlah("abc"));
        uji("T20", "Form jumlah \"2.5\"", "false", KalkulatorTagihan.validasiJumlah("2.5"));
        uji("T21", "Form jumlah \" 3 \" (ada spasi)", "true", KalkulatorTagihan.validasiJumlah(" 3 "));
        uji("T22", "Form harga \"\" (kosong)", "false", KalkulatorTagihan.validasiHarga(""));

        // ===== Batas lain =====
        uji("T23", "Ongkir Jakarta 0,3 kg -> minimal 1 kg", "10000", KalkulatorTagihan.hitungOngkir("Jakarta", 0.3));
        uji("T24", "Ongkir berat 0 kg", "DITOLAK", coba(() -> KalkulatorTagihan.hitungOngkir("Jakarta", 0)));
        uji("T25", "Pesanan kosong: total akhir", "0",
                new Pesanan("INV-004", "Dina", "Bogor").hitungTotalAkhir());

        System.out.println("-".repeat(112));
        System.out.printf("Ringkasan: %d lulus, %d gagal dari %d test case%n", lulus, gagal, lulus + gagal);
    }

    /** Membandingkan hasil aktual dengan harapan lalu mencetak satu baris tabel. */
    private static void uji(String no, String skenario, String diharapkan, Object aktual) {
        String a = String.valueOf(aktual);
        boolean ok = a.equals(diharapkan);
        if (ok) lulus++; else gagal++;
        System.out.printf("%-4s | %-46s | %-22s | %-22s | %s%n",
                no, skenario, diharapkan, a, ok ? "LULUS" : "GAGAL");
    }

    /** Menjalankan aksi; "DITOLAK" jika melempar IllegalArgumentException. */
    private static String coba(Runnable aksi) {
        try {
            aksi.run();
            return "DITERIMA";
        } catch (IllegalArgumentException e) {
            return "DITOLAK";
        }
    }
}
