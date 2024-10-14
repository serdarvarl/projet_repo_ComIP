import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;






public class SimulationPanel extends JPanel {
    private Plan plan ; // lydia ajoue
    private Vehicules vehicle;
    private Pietons pedestrian;
    private Feu trafficLight;
    private Timer timer; // Timer puor update simulation

    public SimulationPanel() {
        // lydia : lire le paln a partir du fichier Csv
        try {
            plan = new Plan ("projet_repo_copy/plan.csv");//le chemain lydia
        }catch(IOException e){
            e.printStackTrace();
        }

        // Initialize the objects
        trafficLight = new Feu();
        trafficLight.changerCouleur("Vert"); // initial color est vert

        vehicle = new Vehicules(true, 150, 250); // creer vehicule
        pedestrian = new Pietons(true, 325, 350); // creer pieton

        // timer update chaque 100 ms (10 FPS)
        timer = new Timer(100, e -> updateSimulation());
    }

    public void startSimulation() {
        timer.start(); // starter times
    }

    private void updateSimulation() {
        //update vehicule et pieton sur logique de sirqulation
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

        // redesiner
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (plan != null) {
        //lydia : dessiner le plan :
            int[][] grid = plan.getGrid();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    switch (grid[i][j]) {
                        case 0:
                            g.setColor(Color.GREEN); // Trottoir
                            break;
                        case 1:
                            g.setColor(Color.BLACK); // Route
                            break;
                        case 2:
                            g.setColor(Color.YELLOW); // Passage piéton
                            break;
                        case 3:
                            g.setColor(Color.RED); // Feu (par défaut)
                            g.fillRect(j * 20, i * 20, 20, 20); // Dessiner le feu
                            continue; // Passer au prochain élément

                        default:
                            g.setColor(Color.WHITE); // Espace vide
                            break;
                    }
                    g.fillRect(j * 20, i * 20, 20, 20); // Dessiner chaque cellule

                }
        }
        }


        // Draw the traffic light
        g.setColor(trafficLight.getCouleur().equals("Vert") ? Color.GREEN : Color.RED);
        g.fillRect(80, 50, 20, 60); // desiner feu

        // Draw the vehicle
        g.setColor(Color.BLUE);
        g.fillRect((int) vehicle.axeXV, (int) vehicle.axeYV, 60, 40); // desiner vehicule

        // Draw the pedestrian
        g.setColor(Color.ORANGE);
        g.fillOval((int) pedestrian.getAxeXP(), (int) pedestrian.getAxeYP(), 20, 20);// desiner pieton


    }
}
