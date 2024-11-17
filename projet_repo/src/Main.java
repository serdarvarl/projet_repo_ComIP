import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // İlk trafik ışığı (başlangıçta yeşil, hemen çalışacak)
            Feu trafficLight1 = new Feu(0, "Vert");
            Thread trafficLightThread1 = new Thread(trafficLight1);
            trafficLightThread1.start();

            // İkinci trafik ışığı (başlangıçta kırmızı, 3 saniye gecikmeli çalışacak)
            Feu trafficLight2 = new Feu(3000, "Rouge");
            Thread trafficLightThread2 = new Thread(trafficLight2);
            trafficLightThread2.start();

            // SimulationPanel oluşturulurken iki Feu nesnesini veriyoruz
            JFrame frame = new JFrame("Simulation de trafic");
            SimulationPanel simulationPanel = new SimulationPanel(trafficLight1, trafficLight2);
            frame.add(simulationPanel);
            frame.setSize(942, 730);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
