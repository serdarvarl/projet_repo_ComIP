import static java.lang.Math.*;
import java.awt.Color;
import java.util.List;
import java.util.Random;
//hazem
public class Pietons implements Runnable {
    // Coordinate pieton
    private double axeXP;
    private double axeYP;

    // Target coordinates
    private double targetX;
    private double targetY;

    // pieton vites
    private double speed;

    // Movement status
    private boolean enMovementP;

    // Feu tricoloeur
    private Feu trafficLight1;
    private Feu trafficLight2;

    // Lists de pieton et vehicule
    private List<Pietons> pedestrians;
    private List<Vehicules> vehicles;

    // color pieton
    private Color color;

    // Collision status
    private boolean collided;

    // min distance pour collision
    private static final double MIN_DISTANCE = 20.0;

    //
    private SimulationPanel panel;

    // Lock object for sequential access
    private static final Object sequentialLock = new Object();
    //serdar
    // les points drapeau
    private double[][] waypoints = {
            {240, 640}, {240, 460},
            {320, 360}, {460, 320},
            {480, 300}, {480, 140},
            {520, 100}, {560, 80}
    };

    // index waypoints
    private int currentWaypointIndex = 0;

    // constructor
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

        // random color pietons
        Random rand = new Random();
        this.color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }


    public double getAxeXP() {
        return axeXP;
    }


    public double getAxeYP() {
        return axeYP;
    }


    public Color getColor() {
        return color;
    }


    public boolean isCollided() {
        return collided;
    }


    public double getSpeed() {
        return speed;
    }


    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
    }

    // collision control vehicule et pieton entre deux agent distance inf egal 20 pixel c-à-d accidant
    //serdar
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
    //Serdar + hazem
    @Override
    public void run() {
        try {
            //Ajouter un temps d'attente pour que les piétons sortent toutes les 5 secondes
            Thread.sleep(0);
            System.out.println("Pedestrian starting to move..."); // Start message

            while (true) {
                if (!panel.isCollisionActive()) {
                    //Contrôle d'approche passage piéton (points feu 7 et 3)
                    double distanceToTrafficLight7 = Math.sqrt(Math.pow(axeXP - 300, 2) + Math.pow(axeYP - 600, 2));
                    boolean isNearTrafficLight7 = distanceToTrafficLight7 <= 30;

                    double distanceToTrafficLight3 = Math.sqrt(Math.pow(axeXP - 420, 2) + Math.pow(axeYP - 240, 2));
                    boolean isNearTrafficLight3 = distanceToTrafficLight3 <= 30;

                    if (isNearTrafficLight7 || isNearTrafficLight3) {
                        //feu cotrol
                        String trafficLightColor1 = trafficLight1.getCouleur();
                        String trafficLightColor2 = trafficLight2.getCouleur();

                        if (trafficLightColor1.equals("Vert") || trafficLightColor2.equals("Vert")) {
                            // situtaion vert
                            enMovementP = false;
                            System.out.println("Pieton arrete");
                        } else {
                            //
                            enMovementP = true;
                            System.out.println("Pieton marche.");
                        }
                    } else {
                        // si les pieton proche de passage pieton, continue normal
                        enMovementP = true;
                    }

                    // Movement logic
                    if (enMovementP) {
                        moveTowardsNextWaypoint();
                    }

                    // Collision detec
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
    //serdar
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
            // arrive target revien le depart
            currentWaypointIndex = 0;
        }
    }
    //serdar
    // Method check pieton close to feu
    private boolean isNearTrafficLight(double x, double y) {
        return (Math.abs(x - 300) <= 30 && Math.abs(y - 620) <= 30) || (Math.abs(x - 260) <= 30 && Math.abs(y - 420) <= 30);
    }
}


