import static java.lang.Math.*;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Pietons implements Runnable {
    // Coordinates of the pedestrian
    private double axeXP;
    private double axeYP;

    // Target coordinates
    private double targetX;
    private double targetY;

    // Speed of the pedestrian
    private double speed;

    // Movement status
    private boolean enMovementP;

    // Traffic lights
    private Feu trafficLight1;
    private Feu trafficLight2;

    // Lists of pedestrians and vehicles
    private List<Pietons> pedestrians;
    private List<Vehicules> vehicles;

    // Color of the pedestrian
    private Color color;

    // Collision status
    private boolean collided;

    // Minimum distance for collision detection
    private static final double MIN_DISTANCE = 20.0;

    // Reference to the simulation panel for collision verification
    private SimulationPanel panel;

    // Lock object for sequential access
    private static final Object sequentialLock = new Object();

    // Waypoints for the pedestrian's path
    private double[][] waypoints = {
            {240, 640}, {240, 460},
            {320, 360}, {460, 320},
            {480, 300}, {480, 140},
            {520, 100}, {560, 80}
    };

    // Index of the current waypoint
    private int currentWaypointIndex = 0;

    // Constructor to initialize the pedestrian
    public Pietons(double axeXP, double axeYP, double targetX, double targetY, double speed, Feu trafficLight1, Feu trafficLight2, List<Vehicules> vehicles, List<Pietons> pedestrians, SimulationPanel panel) {
        this.axeXP = axeXP;
        this.axeYP = axeYP;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;
        this.vehicles = vehicles;
        this.pedestrians = pedestrians;
        this.panel = panel;
        this.enMovementP = false;
        this.collided = false;

        // Assign a random color to the pedestrian
        Random rand = new Random();
        this.color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }

    // Getter for the X coordinate
    public double getAxeXP() {
        return axeXP;
    }

    // Getter for the Y coordinate
    public double getAxeYP() {
        return axeYP;
    }

    // Getter for the color
    public Color getColor() {
        return color;
    }

    // Getter for the collision status
    public boolean isCollided() {
        return collided;
    }

    // Getter for the speed
    public double getSpeed() {
        return speed;
    }

    // Setter for the speed
    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
    }

    // Method to detect collision with vehicles
    private boolean detectCollision() {
        for (Vehicules vehicle : vehicles) {
            if (Math.abs(this.axeXP - vehicle.getAxeXV()) <= 20 && Math.abs(this.axeYP - vehicle.getAxeYV()) <= 20) {
                collided = true;
                panel.setCollisionOccurred(true, (int) this.axeXP, (int) this.axeYP); // Collision position
                return true;
            }
        }
        collided = false;
        return false;
    }

    @Override
    public void run() {
        try {
            // Add a delay so that pedestrians start moving every 5 seconds
            Thread.sleep(0);
            System.out.println("Pedestrian starting to move..."); // Start message

            while (true) {
                if (!panel.isCollisionActive()) {
                    // Check proximity to traffic lights (points 7 and 3)
                    double distanceToTrafficLight7 = Math.sqrt(Math.pow(axeXP - 300, 2) + Math.pow(axeYP - 600, 2));
                    boolean isNearTrafficLight7 = distanceToTrafficLight7 <= 30;

                    double distanceToTrafficLight3 = Math.sqrt(Math.pow(axeXP - 420, 2) + Math.pow(axeYP - 240, 2));
                    boolean isNearTrafficLight3 = distanceToTrafficLight3 <= 30;

                    if (isNearTrafficLight7 || isNearTrafficLight3) {
                        // Traffic light control
                        String trafficLightColor1 = trafficLight1.getCouleur();
                        String trafficLightColor2 = trafficLight2.getCouleur();

                        if (trafficLightColor1.equals("Vert") || trafficLightColor2.equals("Vert")) {
                            // Green light situation
                            enMovementP = false;
                            System.out.println("Pieton arrete");
                        } else {
                            // Red or yellow light situation
                            enMovementP = true;
                            System.out.println("Pieton marche.");
                        }
                    } else {
                        // Continue normally if not near a traffic light
                        enMovementP = true;
                    }

                    // Movement logic
                    if (enMovementP) {
                        moveTowardsNextWaypoint();
                    }

                    // Collision detection
                    if (detectCollision()) {
                        enMovementP = false;
                        System.out.println("Collision detected.");
                        Thread.sleep(2); // Wait 2 ms after collision
                    }
                }
                Thread.sleep(16); // 60 FPS
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to move towards the next waypoint
    private void moveTowardsNextWaypoint() {
        if (currentWaypointIndex < waypoints.length) {
            double nextX = waypoints[currentWaypointIndex][0];
            double nextY = waypoints[currentWaypointIndex][1];

            // Move towards the next point
            if (Math.abs(axeXP - nextX) < 1 && Math.abs(axeYP - nextY) < 1) {
                currentWaypointIndex++;
            } else {
                if (axeXP < nextX) axeXP += speed;
                else if (axeXP > nextX) axeXP -= speed;

                if (axeYP < nextY) axeYP += speed;
                else if (axeYP > nextY) axeYP -= speed;

                // Avoid traffic light zone
                //if (isNearTrafficLight(axeXP, axeYP)) {
                    //System.out.println("Éviter la zone du feu.");
                    //currentWaypointIndex++;
                //}
            }
        } else {
            // After reaching the destination, return to the starting point
            currentWaypointIndex = 0;
        }
    }
    //serdar
    // Method to check if the pedestrian is near a traffic light
    private boolean isNearTrafficLight(double x, double y) {
        return (Math.abs(x - 300) <= 30 && Math.abs(y - 620) <= 30) || (Math.abs(x - 260) <= 30 && Math.abs(y - 420) <= 30);
    }
}


