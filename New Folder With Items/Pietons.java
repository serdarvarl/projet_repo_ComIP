import static java.lang.Math.*;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Pietons implements Runnable {
    private double axeXP;
    private double axeYP;
    private double targetX; // point arrive
    private double targetY; //point arrive
    private double speed; // vites de pieton
    private boolean enMovementP;
    private Feu trafficLight1;
    private Feu trafficLight2;
    private List<Pietons> pedestrians;
    private List<Vehicules> vehicles;
    private Color color;
    private boolean collided;
    private static final double MIN_DISTANCE = 20.0;
    private SimulationPanel panel;  // pour verifier accident


    private static final Object sequentialLock = new Object();

    // les points drapeau
    private double[][] waypoints = {
            {240, 640}, {240, 460}, //
            {320, 360}, {460, 320}, //
            {480, 300}, {480, 140},
            {520, 100}, {560, 80}//
    };
    private int currentWaypointIndex = 0;

    public Pietons(double axeXP, double axeYP, double targetX, double targetY, double speed, Feu trafficLight1, Feu trafficLight2, List<Vehicules> vehicles, List<Pietons> pedestrians, SimulationPanel panel) {
        this.axeXP = axeXP;
        this.axeYP = axeYP;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed; // speed
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;
        this.vehicles = vehicles;
        this.pedestrians = pedestrians;
        this.panel = panel;
        this.enMovementP = false;
        this.collided = false;

        // color random
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

    // collision control vehicule et pieton entre deux agent distance inf egal 20 pixel c-à-d accidant
    //serdar
    private boolean detectCollision() {
        for (Vehicules vehicle : vehicles) {
            if (Math.abs(this.axeXP - vehicle.getAxeXV()) <= 20 && Math.abs(this.axeYP - vehicle.getAxeYV()) <= 20) {
                collided = true;
                panel.setCollisionOccurred(true, (int) this.axeXP, (int) this.axeYP); // positon collision
                return true;
            }
        }
        collided = false;
        return false;
    }
    //serdar
    @Override
    public void run() {
        try {
            //Ajouter un temps d'attente pour que les piétons sortent toutes les 5 secondes
            Thread.sleep(0);
            System.out.println("Pedestrian starting to move..."); // message start

            while (true) {
                if (!panel.isCollisionActive()) {
                    // Contrôle d'approche passage piéton (points feu 7 et 3)
                    //boolean isNearTrafficLight7 = Math.abs(axeXP - 300) <= 20 && Math.abs(axeYP - 600) <= 20; // 7
                    //boolean isNearTrafficLight3 = Math.abs(axeXP - 420) <= 20 && Math.abs(axeYP - 240) <= 20; // 3


                    double distanceToTrafficLight7 = Math.sqrt(Math.pow(axeXP - 300, 2) + Math.pow(axeYP - 600, 2));
                    boolean isNearTrafficLight7 = distanceToTrafficLight7 <= 50;

                    double distanceToTrafficLight3 = Math.sqrt(Math.pow(axeXP - 420, 2) + Math.pow(axeYP - 240, 2));
                    boolean isNearTrafficLight3 = distanceToTrafficLight3 <= 50;



                    if (isNearTrafficLight7 || isNearTrafficLight3) {
                        // feu cotrol
                        String trafficLightColor1 = trafficLight1.getCouleur();
                        String trafficLightColor2 = trafficLight2.getCouleur();

                        if (trafficLightColor1.equals("Vert") || trafficLightColor2.equals("Vert")) {
                            // situation vert
                            enMovementP = false;
                            System.out.println("Pieton arrete");
                        } else {
                            //
                            enMovementP = true;
                            System.out.println("Pedestrian is moving due to red/yellow light at a crosswalk.");
                        }
                    } else {
                        // si les pieton proche de passage pieton, continue normal
                        enMovementP = true;
                    }

                    // logique movement
                    if (enMovementP) {
                        moveTowardsNextWaypoint();
                    }

                    // collision detetec
                    if (detectCollision()) {
                        enMovementP = false;
                        System.out.println("Collision detected. Pedestrian is stopping.");
                        Thread.sleep(2); // apres acciden 2 ms
                    }
                }
                Thread.sleep(16); // 60 FPS
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //serdar
    private void moveTowardsNextWaypoint() {
        if (currentWaypointIndex < waypoints.length) {
            double nextX = waypoints[currentWaypointIndex][0];
            double nextY = waypoints[currentWaypointIndex][1];

            // marche marche prochaine point
            if (Math.abs(axeXP - nextX) < 1 && Math.abs(axeYP - nextY) < 1) {
                currentWaypointIndex++;
            } else {
                if (axeXP < nextX) axeXP += 1;
                else if (axeXP > nextX) axeXP -= 1;

                if (axeYP < nextY) axeYP += 1;
                else if (axeYP > nextY) axeYP -= 1;

                // rester loin feu
                if (isNearTrafficLight(axeXP, axeYP)) {
                    System.out.println("Avoiding traffic light zone.");
                    currentWaypointIndex++;
                }
            }
        } else {
            // apres arrive point d'arrive revient point depart
            currentWaypointIndex = 0;
        }
    }
    //serdar
    private boolean isNearTrafficLight(double x, double y) {
        return (Math.abs(x - 300) <= 20 && Math.abs(y - 600) <= 20) || (Math.abs(x - 420) <= 20 && Math.abs(y - 240) <= 20);
    }


}
