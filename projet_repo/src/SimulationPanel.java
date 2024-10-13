import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationPanel extends JPanel {
    private Vehicules vehicle;
    private Pietons pedestrian;
    private Feu trafficLight;
    private ExecutorService executorService;
    private Timer timer; // Timer puor update simulation

    public <timer> SimulationPanel() {
        // Initialize the objects
        trafficLight = new Feu();
        trafficLight.changerCouleur("Vert"); // initial color est vert

        vehicle = new Vehicules(true, 700, 150); // creer vehicule
        pedestrian = new Pietons(true, 200, 250); // creer pieton


        executorService = Executors.newCachedThreadPool(); //
        startVehicleThread(); //
        //timer update; chaque 16 ms (10 FPS);
        timer = new Timer(16, e -> updateSimulation());
    }

    public void startVehicleThread() {
        executorService.submit(() -> {
            while (true) {
                try {
                    //
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Araba hareketi
                vehicle.axeXV -= 2;
                if (vehicle.axeXV < -50) { // Araba ekran dışına çıkarsa başa döndür
                    vehicle.axeXV = 800;
                }

                // Yeniden çizim Swing bileşenini ana thread (EDT) üzerinde çalıştırmalıyız
                SwingUtilities.invokeLater(this::repaint);
            }
        });
    }



    public void startSimulation() {
        timer.start(); // starter times
    }

    private void updateSimulation() {
        //update vehicule et pieton sur logique de sirqulation
      /*
        boolean vehicleMoving = vehicle.verifierArret(pedestrian, 100, 180); // check si il doit vehicule stop
        boolean pedestrianMoving = pedestrian.testerMovement(trafficLight); // check si il doit pieton se depalcer

        if (vehicleMoving) {
            // deplacer vers la gauche
            vehicle.axeXV -= 2;
        }

        if (pedestrianMoving) {
            // deplacer pieton vers la haut
            pedestrian = new Pietons(true, pedestrian.getAxeXP(), pedestrian.getAxeYP() - 2);
        }

       */

        vehicle.axeXV -=2;


        if (vehicle.axeXV < -50) {
            vehicle.axeXV = 800;
        }

        // redesiner
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the traffic light
        g.setColor(trafficLight.getCouleur().equals("Vert") ? Color.GREEN : Color.RED);
        g.fillRect(50, 50, 20, 60); // desiner feu

        // Draw the vehicle
        g.setColor(Color.BLUE);
        g.fillRect((int) vehicle.axeXV, (int) vehicle.axeYV, 50, 30); // desiner vehicule

        // Draw the pedestrian
        g.setColor(Color.ORANGE);
        g.fillOval((int) pedestrian.getAxeXP(), (int) pedestrian.getAxeYP(), 20, 20); // desiner pieton
    }
}


