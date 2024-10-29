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

    public SimulationPanel() {
        // lydia : lire le paln a partir du fichier Csv
        try {
            plan = new Plan ("plan.csv");//le chemain // lydia
        }catch(IOException e){
            e.printStackTrace();
        }


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

        // Planı çiz
        if (plan != null) {
            int[][] grid = plan.getGrid();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    switch (grid[i][j]) {
                        case 0:
                            g.setColor(Color.GRAY); // Trottoir
                            break;
                        case 1:
                            g.setColor(Color.BLACK); // Route
                            break;
                        case 2:
                            g.setColor(Color.WHITE); //Passage piéton
                            break;
                        case 3:
                            // feu
                            g.setColor(trafficLight.getCouleur().equals("Vert") ? Color.GREEN :
                                    trafficLight.getCouleur().equals("Jaune") ? Color.YELLOW : Color.RED);
                            g.fillRect(j * 20, i * 20, 20, 60);  //feu
                            continue; //
                        default:
                            g.setColor(Color.WHITE); //
                            break;
                    }
                    g.fillRect(j * 20, i * 20, 20, 20); //
                }
            }
        }

        // arreter vehicule on rouge
        g.setColor(Color.BLUE);
        for (Vehicules vehicle : vehicles) {
            int roadYPosition = getHeight() / 2;  // position millieu de route
            if (trafficLight.getCouleur().equals("Rouge") && vehicle.axeXV < (getWidth() / 2 + 100)) {
                //
                g.fillRect((int) vehicle.axeXV, roadYPosition - 20, 60, 40);  // stop
            } else {
                // demarer
                g.fillRect((int) vehicle.axeXV, roadYPosition - 20, 60, 40);  // dessiner vehicule
            }
        }

        // pieteon desseiner
        g.setColor(Color.ORANGE);
        for (Pietons pedestrian : pedestrians) {
            int crosswalkXPosition = getWidth() / 2 + 100;  // pasage axe X
            if (trafficLight.getCouleur().equals("Rouge")) {
                // si rouge les pieton stop
                g.fillOval(crosswalkXPosition, (int) pedestrian.axeYP, 20, 20);  // pieton
            }
        }
    }


}


