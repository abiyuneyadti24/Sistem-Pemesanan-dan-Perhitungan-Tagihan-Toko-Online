import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pesanan
 * -------
 * Mewakili satu transaksi pelanggan. Perhitungan total dilakukan oleh
 * INSTANCE method karena hasilnya bergantung pada isi pesanan ini
 * (daftar item dan kota tujuan), bukan hanya pada parameter.
 *
 * Perhitungan (hitungXxx) dipisahkan dari tampilan (cetakRingkasanPesanan).
 */
public class Pesanan {

    private final String idPesanan;
    private final String namaPelanggan;
    private final String kotaTujuan;
    private final List<ItemPesanan> daftarItem = new ArrayList<>();

    public Pesanan(String idPesanan, String namaPelanggan, String kotaTujuan) {
        if (idPesanan == null || idPesanan.isBlank()) {
            throw new IllegalArgumentException("ID pesanan wajib diisi");
        }
        if (kotaTujuan == null || kotaTujuan.isBlank()) {
            throw new IllegalArgumentException("Kota tujuan wajib diisi");
        }
        this.idPesanan = idPesanan;
        this.namaPelanggan = namaPelanggan;
        this.kotaTujuan = kotaTujuan;
    }

    /**
     * Menambah item ke pesanan. Mengembalikan false (dan TIDAK menambah)
     * jika produk null atau jumlah tidak valid, sehingga pemanggil tahu
     * bahwa input ditolak tanpa program berhenti.
     */
    public boolean tambahItem(Produk produk, int jumlah) {
        if (produk == null || !KalkulatorTagihan.validasiJumlah(jumlah)) {
            return false;
        }
        daftarItem.add(new ItemPesanan(produk, jumlah));
        return true;
    }

    // ===== Perhitungan (instance method, membaca state objek) =====

    /** Jumlah seluruh subtotal item di pesanan ini. */
    public long hitungTotalBelanja() {
        long total = 0;
        for (ItemPesanan item : daftarItem) {
            total = Math.addExact(total, item.hitungSubtotal());
        }
        return total;
    }

    /** Total berat seluruh item (kg). */
    public double hitungTotalBerat() {
        double total = 0;
        for (ItemPesanan item : daftarItem) {
            total += item.hitungBerat();
        }
        return total;
    }

    public long hitungDiskon() {
        return KalkulatorTagihan.hitungDiskon(hitungTotalBelanja());
    }

    /** Ongkir 0 jika pesanan kosong (tidak ada barang yang dikirim). */
    public long hitungOngkir() {
        if (daftarItem.isEmpty()) return 0L;
        return KalkulatorTagihan.hitungOngkir(kotaTujuan, hitungTotalBerat());
    }

    /** Total akhir = total belanja - diskon + ongkir. */
    public long hitungTotalAkhir() {
        return hitungTotalBelanja() - hitungDiskon() + hitungOngkir();
    }

    // ===== Tampilan (hanya mencetak, tidak menghitung sendiri) =====

    public void cetakRingkasanPesanan() {
        String garis = "=".repeat(64);
        System.out.println(garis);
        System.out.println("RINGKASAN PESANAN " + idPesanan);
        System.out.println("Pelanggan   : " + namaPelanggan);
        System.out.println("Kota tujuan : " + kotaTujuan);
        System.out.println("-".repeat(64));
        System.out.printf("%-24s %14s %5s %16s%n", "Produk", "Harga", "Qty", "Subtotal");
        for (ItemPesanan item : daftarItem) {
            System.out.printf("%-24s %14s %5d %16s%n",
                    item.getProduk().getNama(),
                    rupiah(item.getProduk().getHarga()),
                    item.getJumlah(),
                    rupiah(item.hitungSubtotal()));
        }
        System.out.println("-".repeat(64));
        System.out.printf("%-45s %18s%n", "Total belanja", rupiah(hitungTotalBelanja()));
        System.out.printf("%-45s %18s%n", "Diskon", "-" + rupiah(hitungDiskon()));
        System.out.printf("%-45s %18s%n",
                String.format("Ongkir (%.2f kg)", hitungTotalBerat()), rupiah(hitungOngkir()));
        System.out.printf("%-45s %18s%n", "TOTAL AKHIR", rupiah(hitungTotalAkhir()));
        System.out.println(garis);
    }

    /** Helper format tampilan, contoh: 1500000 -> "Rp1.500.000". */
    private static String rupiah(long nilai) {
        return "Rp" + String.format("%,d", nilai).replace(',', '.');
    }

    // ===== Getter =====

    public String getIdPesanan()     { return idPesanan; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public String getKotaTujuan()    { return kotaTujuan; }

    /** Daftar item read-only agar isi pesanan tidak diubah dari luar. */
    public List<ItemPesanan> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }
}
