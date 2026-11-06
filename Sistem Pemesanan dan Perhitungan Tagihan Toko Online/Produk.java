/**
 * Produk
 * ------
 * Menyimpan data satu jenis barang. Semua field private agar tidak bisa
 * diubah sembarangan dari luar; harga divalidasi saat objek dibuat.
 */
public class Produk {

    private final String kode;
    private final String nama;
    private final long harga;   // Rupiah per unit
    private final double berat; // kg per unit

    public Produk(String kode, String nama, long harga, double berat) {
        if (kode == null || kode.isBlank()) {
            throw new IllegalArgumentException("Kode produk wajib diisi");
        }
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama produk wajib diisi");
        }
        if (!KalkulatorTagihan.validasiHarga(harga)) {
            throw new IllegalArgumentException("Harga tidak valid: " + harga);
        }
        if (berat <= 0) {
            throw new IllegalArgumentException("Berat harus lebih dari 0 kg");
        }
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.berat = berat;
    }

    public String getKode()  { return kode; }
    public String getNama()  { return nama; }
    public long   getHarga() { return harga; }
    public double getBerat() { return berat; }
}
