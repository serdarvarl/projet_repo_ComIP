import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationPanel extends JPanel {
    private List<Vehicules> vehicles;
    private List<Pietons> pedestrians;
    private Feu trafficLight;
    private ExecutorService executorService;
    private Plan plan;
    private boolean collisionOccurred = false; // drapeau de situation d'accident
    private long collisionTime; // time accident

    public SimulationPanel() {
        // CVS plan
        try {
            plan = new Plan("plan.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start les objet
        trafficLight = new Feu();
        vehicles = new ArrayList<>();
        pedestrians = new ArrayList<>();

        // ajouter vehicules
        for (int i = 0; i < 5; i++) {
            vehicles.add(new Vehicules(-100 * i, getHeight() / 2 + 50, trafficLight, vehicles));
        }

        // ajouter pietons diffenret color et vites
        for (int i = 0; i < 3; i++) {
            pedestrians.add(new Pietons(getWidth() / 2 + 100, getHeight() / 2 - 100 - i * 50, trafficLight, vehicles, pedestrians, this));
        }

        // starter thread et simulation
        executorService = Executors.newCachedThreadPool();
        startSimulation();
    }

    public void startSimulation() {
        // starter feu
        executorService.submit(trafficLight);

        // starter vehicule et pieton
        for (Vehicules vehicle : vehicles) {
            executorService.submit(vehicle);
        }

        for (Pietons pedestrian : pedestrians) {
            executorService.submit(pedestrian);
        }

        // Timer pour redessiner
        Timer repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();
    }

    // Mettre à jour le statut de l'accident
    public void setCollisionOccurred(boolean collisionOccurred) {
        this.collisionOccurred = collisionOccurred;
        this.collisionTime = System.currentTimeMillis(); // Enregistrer le moment de l'accident
    }

    // Vérifier si le statut de l'accident est actif
    public boolean isCollisionActive() {
        // Si l'accident s'est produit il y a moins de 3 secondes, il reste actif
        return collisionOccurred && (System.currentTimeMillis() - collisionTime < 3000);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // dessiner plan
        if (plan != null) {
            int[][] grid = plan.getGrid();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    switch (grid[i][j]) {
                        case 0 -> g.setColor(Color.GRAY); // Trottoir
                        case 1 -> g.setColor(Color.BLACK); // Route
                        case 2 -> g.setColor(Color.WHITE); // Passage piéton
                        case 3 -> {
                            // Feu
                            g.setColor(trafficLight.getCouleur().equals("Vert") ? Color.GREEN :
                                    trafficLight.getCouleur().equals("Jaune") ? Color.YELLOW : Color.RED);
                            g.fillRect(j * 20, i * 20, 20, 60);
                            continue;
                        }
                        default -> g.setColor(Color.WHITE);
                    }
                    g.fillRect(j * 20, i * 20, 20, 20);
                }
            }
        }

        // dessiner vehicules
        g.setColor(Color.BLUE);
        for (Vehicules vehicle : vehicles) {
            int roadYPosition = getHeight() / 2;
            g.fillRect((int) vehicle.getAxeXV(), roadYPosition - 20, 60, 40);
        }

        // dessiner pietons
        for (Pietons pedestrian : pedestrians) {
            g.setColor(pedestrian.getColor()); // couleur différent pour chaque piéton
            int crosswalkXPosition = getWidth() / 2 + 100;
            g.fillOval(crosswalkXPosition, (int) pedestrian.getAxeYP(), 20, 20);

            // Dessiner un signe de croix en cas d'accident
            if (pedestrian.isCollided()) {
                g.setColor(Color.RED);  // Couleur rouge pour la croix
                drawCross(g, (int) pedestrian.getAxeXP(), (int) pedestrian.getAxeYP());
            }
        }
    }

    // Dessine un signe de croix
    private void drawCross(Graphics g, int x, int y) {
        int size = 30;  // Taille de la croix
        g.drawLine(x - size, y - size, x + size, y + size);
        g.drawLine(x - size, y + size, x + size, y - size);
    }
}
