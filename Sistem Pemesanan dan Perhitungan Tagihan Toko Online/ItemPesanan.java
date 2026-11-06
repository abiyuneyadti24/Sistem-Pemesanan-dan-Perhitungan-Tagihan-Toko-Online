/**
 * ItemPesanan
 * -----------
 * Satu baris di keranjang: produk + jumlah yang dibeli.
 * Class pendukung agar Pesanan dapat menampung banyak produk.
 */
public class ItemPesanan {

    private final Produk produk;
    private final int jumlah;

    public ItemPesanan(Produk produk, int jumlah) {
        if (produk == null) {
            throw new IllegalArgumentException("Produk tidak boleh null");
        }
        if (!KalkulatorTagihan.validasiJumlah(jumlah)) {
            throw new IllegalArgumentException("Jumlah tidak valid: " + jumlah);
        }
        this.produk = produk;
        this.jumlah = jumlah;
    }

    /** Instance method: subtotal item ini, memakai method static kalkulator. */
    public long hitungSubtotal() {
        return KalkulatorTagihan.hitungSubtotal(produk.getHarga(), jumlah);
    }

    /** Berat total item ini (berat per unit x jumlah). */
    public double hitungBerat() {
        return produk.getBerat() * jumlah;
    }

    public Produk getProduk() { return produk; }
    public int    getJumlah() { return jumlah; }
}
