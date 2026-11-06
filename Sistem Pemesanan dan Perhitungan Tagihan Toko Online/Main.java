/**
 * Main
 * ----
 * Contoh pemakaian: membuat produk, memasukkan ke pesanan, lalu mencetak
 * ringkasan. Juga mensimulasikan input formulir browser yang tidak valid.
 */
public class Main {

    public static void main(String[] args) {
        Produk laptop = new Produk("P003", "Laptop 14 inch", 7_500_000, 2.0);
        Produk tas    = new Produk("P004", "Tas Laptop", 350_000, 0.7);
        Produk mouse  = new Produk("P002", "Mouse Wireless", 75_000, 0.2);

        Pesanan pesanan = new Pesanan("INV-003", "Andi", "Makassar");
        pesanan.tambahItem(laptop, 1);
        pesanan.tambahItem(tas, 1);

        // Simulasi data dari formulir browser: dicek dulu sebelum dipakai
        String jumlahDariForm = "-2";
        if (KalkulatorTagihan.validasiJumlah(jumlahDariForm)) {
            pesanan.tambahItem(mouse, Integer.parseInt(jumlahDariForm.trim()));
        } else {
            System.out.println("Input jumlah \"" + jumlahDariForm + "\" ditolak: harus bilangan bulat 1-100.");
        }

        pesanan.cetakRingkasanPesanan();
    }
}
