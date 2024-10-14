import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationPanel extends JPanel {
    private List<Vehicules> vehicles;
    private List<Pietons> pedestrians;
    private Feu trafficLight;
    private ExecutorService executorService;

    public SimulationPanel() {
        // Nesneleri başlat
        trafficLight = new Feu();
        vehicles = new ArrayList<>();
        pedestrians = new ArrayList<>();

        // Araçları ekle (Araçlar y ekseninde yaya geçidinin altında olacak)
        for (int i = 0; i < 5; i++) {
            vehicles.add(new Vehicules(-100 * i, getHeight() / 2 + 50, trafficLight));  // Araçlar y ekseninde alt kısımda hareket eder
        }

        // Yayaları ekle (Yayalar y ekseninde yukarıda ve yaya geçidinden geçecek şekilde ayarlandı)
        for (int i = 0; i < 3; i++) {
            pedestrians.add(new Pietons(getWidth() / 2 + 100, getHeight() / 2 - 100 - i * 50, trafficLight));  // Yayalar yukarıda olacak şekilde konumlandırıldı
        }

        // Thread havuzunu başlat
        executorService = Executors.newCachedThreadPool();
        startSimulation();
    }

    public void startSimulation() {
        // Trafik ışığı thread'ini başlat
        executorService.submit(trafficLight);

        // Araç ve yaya thread'lerini başlat
        for (Vehicules vehicle : vehicles) {
            executorService.submit(vehicle);
        }

        for (Pietons pedestrian : pedestrians) {
            executorService.submit(pedestrian);
        }

        // Simülasyonu sürekli yeniden çiz
        Timer repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Eksenleri çiz
        drawAxes(g);

        // Trafik ışığını (0,0) noktasına yerleştir
        g.setColor(trafficLight.getCouleur().equals("Vert") ? Color.GREEN : trafficLight.getCouleur().equals("Sarı") ? Color.YELLOW : Color.RED);
        g.fillRect(getWidth() / 2 - 10, getHeight() / 2 - 60, 20, 60);  // Trafik ışığı boyutu ve pozisyonu

        // Araçları çiz (y ekseninde daha aşağıda olacaklar)
        g.setColor(Color.BLUE);
        for (Vehicules vehicle : vehicles) {
            g.fillRect((int) vehicle.axeXV, getHeight() / 2 + 100, 50, 30);  // Araçlar yaya geçidinden daha aşağıda olacak
        }

        // Yayaları çiz (yaya geçidinin üstünde yukarıdan aşağıya doğru hareket ederler)
        g.setColor(Color.ORANGE);
        for (Pietons pedestrian : pedestrians) {
            g.fillOval((int) pedestrian.axeXP, (int) pedestrian.axeYP, 20, 20);  // Yayalar yukarıda yaya geçidinde olacak
        }

        // Yaya geçidini (100, 0) noktasına yerleştir
        g.setColor(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            g.fillRect(getWidth() / 2 + 100, getHeight() / 2 + i * 30, 150, 5);  // Yaya geçidi çizgileri
        }
    }

    // X ve Y eksenlerini çiz
    private void drawAxes(Graphics g) {
        g.setColor(Color.GRAY);  // Eksen çizgileri gri renk olacak

        // X eksenini çiz (Yatay çizgi)
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        // Y eksenini çiz (Dikey çizgi)
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
    }
}
