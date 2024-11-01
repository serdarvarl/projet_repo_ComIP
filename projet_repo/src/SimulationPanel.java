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
    private Feu trafficLight;
    private ExecutorService executorService;
    private Plan plan;
    private boolean collisionOccurred = false; // drapeau de situation d'accident
    private long collisionTime; // time accident
    private int collisionCounter = 0;
    private int collisionX=-1; // Coordonnées de la collision
    private int collisionY=-1; // Coordonnées de la collision
    Image crossImage = new ImageIcon("cross.png").getImage();

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
//crois dessiner
        if (collisionOccurred && collisionX >= 0 && collisionY >= 0) {
            g.setColor(Color.RED);  //
            drawCross(g, collisionX, collisionY);
        }




        // Dessiner la grille de coordonnées
        g.setColor(Color.BLUE);
        for (int i = 0; i < getWidth(); i += 50) {
            g.drawLine(i, 0, i, getHeight()); // Lignes verticales
        }
        for (int j = 0; j < getHeight(); j += 50) {
            g.drawLine(0, j, getWidth(), j); // Lignes horizontales
        }

        // Marquer le point (0, 0)
        g.setColor(Color.BLUE);
        g.fillOval(0, 0, 10, 10);

        // Marquer le centre
        g.setColor(Color.GREEN);
        g.fillOval(getWidth() / 2, getHeight() / 2, 10, 10);



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
            /*
            if (pedestrian.isCollided()) {
                g.setColor(Color.RED);  // Couleur rouge pour la croix
                drawCross(g, (int) pedestrian.getAxeXP(), (int) pedestrian.getAxeYP());
            }

             */
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
            g.drawImage(crossImage,430 , 200 , imageSize, imageSize, this);
            System.out.println("Possiotn image: " + collisionX + ", " + collisionY);
        }
    }



}
