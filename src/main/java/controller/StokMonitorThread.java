package controller;

import model.Obat;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.List;

public class StokMonitorThread extends Thread {
    private ObatController obatController;
    private volatile boolean running = true;
    private static final int CRITICAL_STOCK_THRESHOLD = 10;

    public StokMonitorThread(ObatController obatController) {
        this.obatController = obatController;
    }

    @Override
    public void run() {
        setDaemon(true);
        while (running) {
            try {
                Thread.sleep(30000);

                List<Obat> semuaObat = obatController.getSemuaObat();
                StringBuilder pesanKritis = new StringBuilder();
                int jumlahKritis = 0;

                for (Obat obat : semuaObat) {
                    if (obat.getStok() < CRITICAL_STOCK_THRESHOLD) {
                        jumlahKritis++;
                        pesanKritis.append("• ").append(obat.getNama())
                                   .append(" (stok: ").append(obat.getStok()).append(")\n");
                    }
                }

                if (jumlahKritis > 0) {
                    final String pesan = pesanKritis.toString();
                    final int jumlah = jumlahKritis;
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null,
                            jumlah + " obat stok kritis:\n" + pesan,
                            "Peringatan Stok Obat",
                            JOptionPane.WARNING_MESSAGE)
                    );
                }
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopMonitor() {
        running = false;
        this.interrupt();
    }
}
