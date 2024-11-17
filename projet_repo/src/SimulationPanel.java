import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import java.awt.Image;

public class SimulationPanel extends JPanel {
    private List<Vehicules> vehicles;
    private List<Pietons> pedestrians;
    private Feu trafficLight1;
    private Feu trafficLight2;
    private ExecutorService executorService;
    private Plan plan;
    private boolean collisionOccurred = false; // drapeau de situation d'accident
    private long collisionTime; // time accident
    private int collisionCounter = 0;
    private int collisionX=-1; // Coordonnées de la collision
    private int collisionY=-1; // Coordonnées de la collision
    Image crossImage = new ImageIcon("cross.png").getImage();

    public SimulationPanel(Feu trafficLight1, Feu trafficLight2) {
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;

        // CVS plan
        try {
            plan = new Plan("plan1.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        vehicles = new ArrayList<>();
        pedestrians = new ArrayList<>();

        // Ajouter vehicules
        for (int i = 0; i < 5; i++) {
            vehicles.add(new Vehicules(-100 * i, 180, trafficLight1, trafficLight2, vehicles));
        }

        // Ajouter pietons - Yayalar 5 saniye aralıklarla çıkacak ve belirlenen noktalardan geçecek
        int[][] waypoints = {
                {240, 640}, {240, 460}, {320, 360}, {460, 320}, {480, 300}, {480, 140}
        };

        // Ajouter pietons
        for (int i = 0; i < 2; i++) {
            pedestrians.add(new Pietons(220, 680 - i * 50, 600, 80, 1.0, trafficLight1, trafficLight2, vehicles, pedestrians, this));
            pedestrians.add(new Pietons(620, 80 - i * 50, 200, 700, 1.0, trafficLight1, trafficLight2, vehicles, pedestrians, this));
        }


        // Start thread et simulation
        executorService = Executors.newCachedThreadPool();
        startSimulation();
    }

    public void startSimulation() {
        // starter feu
        executorService.submit(trafficLight1);
        executorService.submit(trafficLight2);

        // starter vehicule et pieton
        for (Vehicules vehicle : vehicles) {
            executorService.submit(vehicle);
        }

        for (Pietons pedestrian : pedestrians) {
            executorService.submit(pedestrian);
        }

        Timer repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();

    }

    // Mettre à jour le statut de l'accident
    public void setCollisionOccurred(boolean collisionOccurred, int collisionX, int collisionY) {
        if (collisionOccurred && !this.collisionOccurred) { // increase counter pour chaque accident
            collisionCounter++;
            System.out.println("Accident: " + collisionCounter); // afficher sur terminal total accident
            System.out.println("Accident #" + collisionCounter + "position (" + collisionX + ", " + collisionY + ")"); // afficher sur terminal total accident et position
            System.out.println("Collision Occurred: " + collisionOccurred + " at (" + collisionX + ", " + collisionY + ")");

        }
        this.collisionOccurred = collisionOccurred;
        this.collisionTime = System.currentTimeMillis(); // Temps de l'accident
        this.collisionX = collisionX; // Mettre à jour la position de l'accident
        this.collisionY = collisionY;
    }

    public int getCollisionX(){
        return collisionX;
    }

    public int getCollisionY(){
        return collisionY;
    }

    // Vérifier si le statut de l'accident est actif
    public boolean isCollisionActive() {
        // Si l'accident s'est produit il y a moins de 3 secondes, il reste actif
        if (collisionOccurred && (System.currentTimeMillis() - collisionTime >= 3000)) {
            collisionOccurred = false;
        }
        return collisionOccurred;
    }

    // Grid'i almak için getGrid() metodu eklendi
    public int[][] getGrid() {
        return plan != null ? plan.getGrid() : new int[0][0];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Painting component...");

        System.out.println("Painting component...");
        g.setColor(Color.RED);
        g.drawString("Testing Paint", 50, 50);


        // dessiner plan
        if (plan != null) {
            int[][] grid = plan.getGrid();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    switch (grid[i][j]) {
                        case 0 -> g.setColor(Color.GRAY); // Trottoir
                        case 1 -> g.setColor(Color.BLACK); // Route
                        case 2 -> g.setColor(Color.WHITE); // Passage piéton
                        case 4 -> g.setColor(Color.GRAY); // Passage piéton
                        case 3 -> {
                            // Feu principal
                            g.setColor(trafficLight1.getCouleur().equals("Vert") ? Color.GREEN :
                                    trafficLight1.getCouleur().equals("Jaune") ? Color.YELLOW : Color.RED);
                            g.fillRect(j * 20, i * 20, 20, 60);
                            continue;
                        }
                        case 7 -> {
                            // Deuxième feu avec un peu de délai
                            g.setColor(trafficLight2.getCouleur().equals("Vert") ? Color.GREEN :
                                    trafficLight2.getCouleur().equals("Jaune") ? Color.YELLOW : Color.RED);
                            g.fillRect(j * 20, i * 20, 20, 60);
                            continue;
                        }
                        default -> g.setColor(Color.WHITE);
                    }
                    g.fillRect(j * 20, i * 20, 20, 20);
                }
            }
        }
        //crois dessiner
        if (collisionOccurred && collisionX >= 0 && collisionY >= 0) {
            g.setColor(Color.RED);  //
            drawCross(g, collisionX, collisionY);
        }

        // Dessiner la grille de coordonnées
        /*
        g.setColor(Color.BLUE);
        for (int i = 0; i < getWidth(); i += 20) {
            g.drawLine(i, 0, i, getHeight()); // Lignes verticales
        }
        for (int j = 0; j < getHeight(); j += 20) {
            g.drawLine(0, j, getWidth(), j); // Lignes horizontales
        }
         */

        // Marquer le point (0, 0)
        g.setColor(Color.BLUE);
        g.fillOval(0, 0, 10, 10);

        // Marquer le centre
        g.setColor(Color.GREEN);
        g.fillOval(getWidth() / 2, getHeight() / 2, 10, 10);

        // dessiner vehicules
        g.setColor(Color.BLUE);
        for (Vehicules vehicle : vehicles) {
            g.fillRect((int) vehicle.getAxeXV(), (int)vehicle.getAxeYV(), 20, 20);
        }

        // Yayaları çizin
        for (Pietons pedestrian : pedestrians) {
            g.setColor(pedestrian.getColor()); // Her yayanın farklı bir rengi var
            g.fillOval((int) pedestrian.getAxeXP(), (int) pedestrian.getAxeYP(), 20, 20);

        }

        // Afficher le compteur de collisions en haut à gauche
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Total accidents: " + collisionCounter, 10, 30);



    }

    private void drawCross(Graphics g, int collisionX, int collisionY) {
        int imageSize = 30; // Resmin boyutunu ayarlayın

        // crossImage adlı resmi kaza pozisyonuna (collisionX, collisionY) çiziyoruz
        if (collisionOccurred && this.collisionX >= 0 && this.collisionY >= 0) {
            g.drawImage(crossImage, 430, 200, imageSize, imageSize, this);
            System.out.println("Possiotn image: " + collisionX + ", " + collisionY);
        }
    }
}
