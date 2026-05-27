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
        while (running) {
            try {
                // Check every 30 seconds
                Thread.sleep(30000);
                
                List<Obat> semuaObat = obatController.getSemuaObat();
                for (Obat obat : semuaObat) {
                    if (obat.getStok() < CRITICAL_STOCK_THRESHOLD) {
                        // Trigger alert in Event Dispatch Thread
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, 
                                "Peringatan! Stok obat " + obat.getNama() + " kritis (" + obat.getStok() + " tersisa).", 
                                "Stok Kritis", 
                                JOptionPane.WARNING_MESSAGE);
                        });
                        
                        // Stop execution
                        running = false;
                        break; // Break the loop
                    }
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
