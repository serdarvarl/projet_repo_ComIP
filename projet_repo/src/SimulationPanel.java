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
        // demarer les objet
        trafficLight = new Feu();
        vehicles = new ArrayList<>();
        pedestrians = new ArrayList<>();

        // ajouter vehicule
        for (int i = 0; i < 5; i++) {
            vehicles.add(new Vehicules(-100 * i, getHeight() / 2 + 50, trafficLight));  // sens X
        }

        // ajouter pieton
        for (int i = 0; i < 3; i++) {
            pedestrians.add(new Pietons(getWidth() / 2 + 100, getHeight() / 2 - 100 - i * 50, trafficLight));  // sens Y
        }

        // demarer Thread
        executorService = Executors.newCachedThreadPool();
        startSimulation();
    }

    public void startSimulation() {
        // demarer thread feu
        executorService.submit(trafficLight);

        // demarer thread vehicule et pieton
        for (Vehicules vehicle : vehicles) {
            executorService.submit(vehicle);
        }

        for (Pietons pedestrian : pedestrians) {
            executorService.submit(pedestrian);
        }

        // redessiner graphique
        Timer repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // dessiner les axes
        drawAxes(g);

        // dessiner feu au (0,0)
        g.setColor(trafficLight.getCouleur().equals("Vert") ? Color.GREEN : trafficLight.getCouleur().equals("Jaune") ? Color.YELLOW : Color.RED);
        g.fillRect(getWidth() / 2 - 10, getHeight() / 2 - 60, 20, 60);  // Trafik ışığı boyutu ve pozisyonu

        // dessiner vehicules
        g.setColor(Color.BLUE);
        for (Vehicules vehicle : vehicles) {
            g.fillRect((int) vehicle.axeXV, getHeight() / 2 + 100, 50, 30);  // Araçlar yaya geçidinden daha aşağıda olacak
        }

        // dessiner les pietons
        g.setColor(Color.ORANGE);
        for (Pietons pedestrian : pedestrians) {
            g.fillOval((int) pedestrian.axeXP, (int) pedestrian.axeYP, 20, 20);  // Yayalar yukarıda yaya geçidinde olacak
        }

        // dessiner passage
        g.setColor(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            g.fillRect(getWidth() / 2 + 100, getHeight() / 2 + i * 30, 150, 5);  // Yaya geçidi çizgileri
        }
    }

    // dessiner axess virtuel
    private void drawAxes(Graphics g) {
        g.setColor(Color.GRAY);  //

        // X
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        // Y
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
    }
}
