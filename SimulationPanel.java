import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import javax.swing.ImageIcon;
import java.awt.Image;
////serdar

public class SimulationPanel extends JPanel {
    // Lists to hold vehicles and pedestrians in the simulation
    private List<Vehicules> vehicles;
    private List<Pietons> pedestrians;

    // Traffic lights for the simulation
    private Feu trafficLight1;
    private Feu trafficLight2;

    // Thread pool to manage concurrent tasks
    private ExecutorService executorService;

    // Map representation of the simulation grid
    private Plan plan;

    // Collision management variables
    private boolean collisionOccurred = false;
    private long collisionTime;
    private int collisionCounter = 0;
    private int collisionX = -1;
    private int collisionY = -1;

    // Image to display at the collision site
    private Image crossImage = new ImageIcon("cross.png").getImage();

    // Buttons to control the simulation
    private JButton startButton;
    private JButton stopButton;
    private JButton resetButton;
    private JButton increasePedestrianSpeedButton;
    private JButton decreasePedestrianSpeedButton;
    private JButton increasePedestrianCountButton;
    private JButton decreasePedestrianCountButton;
    private JButton increaseVehicleSpeedButton;
    private JButton decreaseVehicleSpeedButton;
    private JButton increaseTrafficLight1SpeedButton;
    private JButton decreaseTrafficLight1SpeedButton;
    private JButton increaseTrafficLight2SpeedButton;
    private JButton decreaseTrafficLight2SpeedButton;

    // Flag to check if the simulation is running
    private boolean simulationRunning = false;

    // Constructor for the simulation panel
    public SimulationPanel(Feu trafficLight1, Feu trafficLight2) {
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;

        try {
            // Load the simulation grid from a CSV file
            plan = new Plan("plan1.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        vehicles = new ArrayList<>();
        pedestrians = new ArrayList<>();

        // Initialize vehicles and their positions
        for (int i = 0; i < 7; i++) {
            vehicles.add(new Vehicules(-100 * i, 180, trafficLight1, trafficLight2, vehicles));
        }

        // Define waypoints for pedestrian paths
        int[][] waypoints = {
                {240, 640}, {240, 460}, {320, 360}, {460, 320}, {480, 300}, {500, 100}, {580, 60}
        };

        // Create pedestrians with staggered timers for arrival
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Timer pedestrianTimer = new Timer(3000 * i, e -> {
                Pietons pedestrian1 = new Pietons(220, 680 - finalI * 50, 620, 60, 2.0, trafficLight1, trafficLight2, vehicles, pedestrians, this);
                Pietons pedestrian2 = new Pietons(620, 60 - finalI * 50, 220, 680, 1.0, trafficLight1, trafficLight2, vehicles, pedestrians, this);

                pedestrians.add(pedestrian1);
                pedestrians.add(pedestrian2);

                executorService.submit(pedestrian1);
                executorService.submit(pedestrian2);
            });
            pedestrianTimer.setRepeats(false);
            pedestrianTimer.start();
        }

        // Create a thread pool to manage tasks
        executorService = Executors.newCachedThreadPool();

        // Initialize control buttons
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        increasePedestrianSpeedButton = new JButton("Increase Pedestrian Speed");
        decreasePedestrianSpeedButton = new JButton("Decrease Pedestrian Speed");
        increaseVehicleSpeedButton = new JButton("Vehicule v+");
        decreaseVehicleSpeedButton = new JButton("Vehicule v-");
        increaseTrafficLight1SpeedButton = new JButton("FEU 1 T+");
        decreaseTrafficLight1SpeedButton = new JButton("FEU 1 T-");
        increaseTrafficLight2SpeedButton = new JButton("FEU 2 T+");
        decreaseTrafficLight2SpeedButton = new JButton("FEU 2 T+");

        // Yaya sayısını artıran buton
        increasePedestrianCountButton = new JButton("Pieton +=1");
        increasePedestrianCountButton.addActionListener(e -> {
            Pietons newPedestrian = new Pietons(220, 680, 620, 60, 2.0, trafficLight1, trafficLight2, vehicles, pedestrians, this);
            pedestrians.add(newPedestrian);
            executorService.submit(newPedestrian);
            System.out.println("Pieteon a ete ajouté total: " + pedestrians.size());
        });

        // Yaya sayısını azaltan buton
        decreasePedestrianCountButton = new JButton("Pieton -=1");
        decreasePedestrianCountButton.addActionListener(e -> {
            if (!pedestrians.isEmpty()) {
                Pietons pedestrianToRemove = pedestrians.remove(pedestrians.size() - 1);
                System.out.println("Pieton a ete dimunie total: " + pedestrians.size());
            } else {
                System.out.println("Il reste plus de pieton");
            }
        });


        // Add action listeners to the buttons
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!simulationRunning) {
                    startSimulation();
                    simulationRunning = true;
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    resetButton.setEnabled(true);
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
                simulationRunning = false;
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(true);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSimulation();
                simulationRunning = false;
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(true);
            }
        });


        increasePedestrianSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increasePedestrianSpeed();
            }
        });

        decreasePedestrianSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decreasePedestrianSpeed();
            }
        });



        increaseVehicleSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increaseVehicleSpeed();
            }
        });

        decreaseVehicleSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decreaseVehicleSpeed();
            }
        });

        increaseTrafficLight1SpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTrafficLightSpeed(trafficLight1, -1000);
            }
        });

        decreaseTrafficLight1SpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTrafficLightSpeed(trafficLight1, 1000);
            }
        });

        increaseTrafficLight2SpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTrafficLightSpeed(trafficLight2, -1000);
            }
        });

        decreaseTrafficLight2SpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTrafficLightSpeed(trafficLight2, 1000);
            }
        });

        // Arrange buttons in a panel with a GridLayout of 2 rows and 6 columns
        JPanel buttonPanel = new JPanel(new GridLayout(4, 6));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(increasePedestrianSpeedButton);
        buttonPanel.add(decreasePedestrianSpeedButton);

        buttonPanel.add(increasePedestrianCountButton);
        buttonPanel.add(decreasePedestrianCountButton);


        buttonPanel.add(increaseVehicleSpeedButton);
        buttonPanel.add(decreaseVehicleSpeedButton);
        buttonPanel.add(increaseTrafficLight1SpeedButton);
        buttonPanel.add(decreaseTrafficLight1SpeedButton);
        buttonPanel.add(increaseTrafficLight2SpeedButton);
        buttonPanel.add(decreaseTrafficLight2SpeedButton);

        // Add the button panel to the simulation panel
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Starts the simulation by submitting tasks to the thread pool
    public void startSimulation() {
        executorService.submit(trafficLight1);
        executorService.submit(trafficLight2);

        for (Vehicules vehicle : vehicles) {
            executorService.submit(vehicle);
        }

        for (Pietons pedestrian : pedestrians) {
            executorService.submit(pedestrian);
        }

        // Schedule a repaint task to refresh the UI
        Timer repaintTimer = new Timer(16, e -> repaint());
        repaintTimer.start();
    }

    // Stops the simulation by shutting down the thread pool
    private void stopSimulation() {
        executorService.shutdownNow();
    }

    // Resets the simulation by restarting all tasks
    /*
    private void resetSimulation() {
        stopSimulation();
        executorService = Executors.newCachedThreadPool();
        startSimulation();
    }
     */



    private void resetSimulation() {
        System.out.println("Simülasyon sıfırlanıyor...");

        // Simülasyonu durdur
        stopSimulation();

        // Araç ve yaya listelerini temizle
        vehicles.clear();
        pedestrians.clear();




        // Araçları yeniden oluştur
        for (int i = 0; i < 7; i++) {
            vehicles.add(new Vehicules(-100 * i, 180, trafficLight1, trafficLight2, vehicles));
        }

        // Yayaları yeniden oluştur
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Pietons pedestrian1 = new Pietons(220, 680 - finalI * 50, 620, 60, 2.0, trafficLight1, trafficLight2, vehicles, pedestrians, this);
            Pietons pedestrian2 = new Pietons(620, 60 - finalI * 50, 220, 680, 1.0, trafficLight1, trafficLight2, vehicles, pedestrians, this);
            pedestrians.add(pedestrian1);
            pedestrians.add(pedestrian2);
        }

        // Yeni bir thread pool oluştur ve simülasyonu yeniden başlat
        executorService = Executors.newCachedThreadPool();
        startSimulation();

        System.out.println("Simülasyon sıfırlandı ve yeniden başlatıldı!");
    }




    // Increases pedestrian speed by 0.5 units
    private void increasePedestrianSpeed() {
        for (Pietons pedestrian : pedestrians) {
            double newSpeed = pedestrian.getSpeed() + 0.5;
            pedestrian.setSpeed(newSpeed);
            System.out.println("Pedestrian speed increased to: " + newSpeed);
        }
    }

    // Decreases pedestrian speed by 0.5 units
    private void decreasePedestrianSpeed() {
        for (Pietons pedestrian : pedestrians) {
            double newSpeed = pedestrian.getSpeed() - 0.5;
            pedestrian.setSpeed(newSpeed);
            System.out.println("Pedestrian speed decreased to: " + newSpeed);
        }
    }

    // Increases vehicle speed by 0.5 units
    private void increaseVehicleSpeed() {
        for (Vehicules vehicle : vehicles) {
            double newSpeed = vehicle.getSpeed() + 0.5;
            vehicle.setSpeed(newSpeed);
            System.out.println("Vehicle speed increased to: " + newSpeed);
        }
    }

    // Decreases vehicle speed by 0.5 units
    private void decreaseVehicleSpeed() {
        for (Vehicules vehicle : vehicles) {
            double newSpeed = vehicle.getSpeed() - 0.5;
            vehicle.setSpeed(newSpeed);
            System.out.println("Vehicle speed decreased to: " + newSpeed);
        }
    }

    // Changes the delay of a traffic light
    private void changeTrafficLightSpeed(Feu trafficLight, int delayChange) {
        int newDelay = trafficLight.getDelay() + delayChange;
        trafficLight.setDelay(newDelay);
        System.out.println("Traffic light speed changed to: " + newDelay);
    }

    // Sets a collision event with its coordinates
    public void setCollisionOccurred(boolean collisionOccurred, int collisionX, int collisionY) {
        if (collisionOccurred && !this.collisionOccurred) {
            collisionCounter++;
            System.out.println("Accident: " + collisionCounter);
            System.out.println("Accident #" + collisionCounter + " position (" + collisionX + ", " + collisionY + ")");
            System.out.println("Collision occurred: " + collisionOccurred + " at (" + collisionX + ", " + collisionY + ")");
        }
        this.collisionOccurred = collisionOccurred;
        this.collisionTime = System.currentTimeMillis();
        this.collisionX = collisionX;
        this.collisionY = collisionY;
    }

    // Getter for the collision X coordinate
    public int getCollisionX() {
        return collisionX;
    }

    // Getter for the collision Y coordinate
    public int getCollisionY() {
        return collisionY;
    }

    // Checks if a collision is currently active
    public boolean isCollisionActive() {
        if (collisionOccurred && (System.currentTimeMillis() - collisionTime >= 2000)) {
            collisionOccurred = false;
        }
        return collisionOccurred;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Paint the grid from the plan
        if (plan != null) {
            int[][] grid = plan.getGrid();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    switch (grid[i][j]) {
                        case 0 -> g.setColor(Color.GRAY);
                        case 1 -> g.setColor(Color.BLACK);
                        case 2 -> g.setColor(Color.WHITE);
                        case 4 -> g.setColor(Color.GRAY);
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

        // Draw collision cross if a collision occurred
        if (collisionOccurred && collisionX >= 0 && collisionY >= 0) {
            g.setColor(Color.RED);
            drawCross(g, collisionX, collisionY);
        }

        // Draw vehicles
        g.setColor(Color.BLUE);
        for (Vehicules vehicle : vehicles) {
            g.fillRect((int) vehicle.getAxeXV(), (int) vehicle.getAxeYV(), 30, 30);
        }

        // Draw pedestrians
        for (Pietons pedestrian : pedestrians) {
            g.setColor(pedestrian.getColor());
            g.fillOval((int) pedestrian.getAxeXP(), (int) pedestrian.getAxeYP(), 15, 15);
        }

        // Draw total accidents count
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Total accidents: " + collisionCounter, 10, 30);
    }

    // Draws a cross at the collision site
    private void drawCross(Graphics g, int collisionX, int collisionY) {
        int imageSize = 30;
        if (collisionOccurred && this.collisionX >= 0 && this.collisionY >= 0) {
            g.drawImage(crossImage, this.collisionX, this.collisionY, imageSize, imageSize, this);
            System.out.println("Position image: " + collisionX + ", " + collisionY);
        }
    }
}


