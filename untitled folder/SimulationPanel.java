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

    private boolean collisionOccurred = false;
    private long collisionTime;
    private int collisionCounter = 0;
    private int collisionX = -1;
    private int collisionY = -1;

    Image crossImage = new ImageIcon("cross.png").getImage();

    // Buttons for control
    private JButton startButton;
    private JButton stopButton;
    private JButton resetButton;
    private JButton increaseVehicleSpeedButton;
    private JButton addPedestrianButton;
    private JButton increaseRedLightDurationButton;

    public SimulationPanel(Feu trafficLight1, Feu trafficLight2) {
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;

        try {
            plan = new Plan("plan1.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        vehicles = new ArrayList<>();
        pedestrians = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            vehicles.add(new Vehicules(-100 * i, 180, trafficLight1, trafficLight2, vehicles));
        }

        // Adding initial pedestrians
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            Timer pedestrianTimer = new Timer(3000 * i, e -> {
                Pietons pedestrian1 = new Pietons(220, 680 - finalI * 50, 620, 60, 2.0, trafficLight1, trafficLight2, vehicles, pedestrians, this);
                pedestrians.add(pedestrian1);
                executorService.submit(pedestrian1);
            });
            pedestrianTimer.setRepeats(false);
            pedestrianTimer.start();
        }

        // Start thread and simulation
        executorService = Executors.newCachedThreadPool();
        startSimulation();

        // Initialize buttons
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        increaseVehicleSpeedButton = new JButton("Increase Vehicle Speed");
        addPedestrianButton = new JButton("Add Pedestrian");
        increaseRedLightDurationButton = new JButton("Increase Red Light Duration");

        // Adding ActionListeners
        startButton.addActionListener(e -> {
            if (executorService.isShutdown()) {
                executorService = Executors.newCachedThreadPool();
                startSimulation();
            }
        });

        stopButton.addActionListener(e -> stopSimulation());

        resetButton.addActionListener(e -> {
            stopSimulation();
            vehicles.clear();
            pedestrians.clear();
            for (int i = 0; i < 7; i++) {
                vehicles.add(new Vehicules(-100 * i, 180, trafficLight1, trafficLight2, vehicles));
            }
            startSimulation();
        });

        increaseVehicleSpeedButton.addActionListener(e -> {
            for (Vehicules vehicle : vehicles) {
                double newSpeed = vehicle.getSpeed() + 1.0;
                vehicle.setSpeed(newSpeed);
                System.out.println("Vehicle speed increased to: " + newSpeed);
            }
        });

        addPedestrianButton.addActionListener(e -> {
            Pietons newPedestrian = new Pietons(220, 680, 620, 60, 2.0, trafficLight1, trafficLight2, vehicles, pedestrians, this);
            pedestrians.add(newPedestrian);
            executorService.submit(newPedestrian);
            System.out.println("New pedestrian added.");
        });

        increaseRedLightDurationButton.addActionListener(e -> {
            int newDelay = trafficLight1.getDelay() + 1000; // Increase delay by 1 second
            trafficLight1.setDelay(newDelay);
            System.out.println("Red light duration increased to: " + newDelay);
        });

        // Add buttons to a panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(increaseVehicleSpeedButton);
        buttonPanel.add(addPedestrianButton);
        buttonPanel.add(increaseRedLightDurationButton);

        // Add button panel to main panel
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void startSimulation() {
        executorService.submit(trafficLight1);
        executorService.submit(trafficLight2);

        for (Vehicules vehicle : vehicles) {
            executorService.submit(vehicle);
        }

        for (Pietons pedestrian : pedestrians) {
            executorService.submit(pedestrian);
        }

        Timer repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();
    }

    private void stopSimulation() {
        executorService.shutdownNow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (plan != null) {
            int[][] grid = plan.getGrid();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    switch (grid[i][j]) {
                        case 0 -> g.setColor(Color.GRAY);
                        case 1 -> g.setColor(Color.BLACK);
                        case 2 -> g.setColor(Color.WHITE);
                        case 3 -> {
                            g.setColor(trafficLight1.getCouleur().equals("Vert") ? Color.GREEN :
                                    trafficLight1.getCouleur().equals("Jaune") ? Color.YELLOW : Color.RED);
                            g.fillRect(j * 20, i * 20, 20, 60);
                            continue;
                        }
                        case 7 -> {
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

        if (collisionOccurred && collisionX >= 0 && collisionY >= 0) {
            g.setColor(Color.RED);
            drawCross(g, collisionX, collisionY);
        }

        g.setColor(Color.BLUE);
        for (Vehicules vehicle : vehicles) {
            g.fillRect((int) vehicle.getAxeXV(), (int) vehicle.getAxeYV(), 20, 20);
        }

        for (Pietons pedestrian : pedestrians) {
            g.setColor(pedestrian.getColor());
            g.fillOval((int) pedestrian.getAxeXP(), (int) pedestrian.getAxeYP(), 20, 20);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Total accidents: " + collisionCounter, 10, 30);
    }

    private void drawCross(Graphics g, int collisionX, int collisionY) {
        int imageSize = 30;
        if (collisionOccurred && this.collisionX >= 0 && this.collisionY >= 0) {
            g.drawImage(crossImage, this.collisionX, this.collisionY, imageSize, imageSize, this);
            System.out.println("Position image: " + collisionX + ", " + collisionY);
        }
    }
}
