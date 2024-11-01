import static java.lang.Math.*;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Pietons implements Runnable {
    private double axeXP;
    private double axeYP;
    private boolean enMovementP;
    private Feu trafficLight;
    private int waitTime;
    private List<Pietons> pedestrians;
    private List<Vehicules> vehicles;
    private Color color;
    private boolean collided;
    private static final double MIN_DISTANCE = 30.0;
    private SimulationPanel panel;  // pour verifier accident
    private double collisionPont;

    public Pietons(double axeXP, double axeYP, Feu trafficLight, List<Vehicules> vehicles, List<Pietons> pedestrians, SimulationPanel panel) {
        this.axeXP = axeXP;
        this.axeYP = axeYP;
        this.trafficLight = trafficLight;
        this.vehicles = vehicles;
        this.pedestrians = pedestrians;
        this.panel = panel;
        this.enMovementP = false;
        this.waitTime = 0;
        this.collided = false;

        // random color pour pietons
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

    // verifier distance entre les 2 pietons
    private boolean isTooCloseToPedestrianAhead() {
        for (Pietons pedestrian : pedestrians) {
            if (pedestrian != this && pedestrian.getAxeYP() < this.axeYP && this.axeYP - pedestrian.getAxeYP() < MIN_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    // accident control
    private boolean detectCollision() {
        for (Vehicules vehicle : vehicles) {
            if (Math.abs(this.axeXP - vehicle.getAxeXV()) <= 20 && Math.abs(this.axeYP - vehicle.getAxeYV()) <=20 ) {
                collided = true;
                panel.setCollisionOccurred(true); // tiregger accident

                System.out.println("Collision Detected. Positon est Pieton:"+axeXP + ","+axeYP + "vehicule:"+vehicle.getAxeXV()+","+vehicle.getAxeYV());
                return true;
            }
        }
        collided = false;
        return false;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // si y a pas d'accident, la simulation continue
                if (!panel.isCollisionActive()) {
                    if ((trafficLight.getCouleur().equals("Rouge") || (axeYP < 300 && enMovementP)) && !isTooCloseToPedestrianAhead()) {
                        enMovementP = true;
                        axeYP -= 2;

                        // quand finir l'ecran, retourne au point de depart
                        if (axeYP < -50) {
                            axeYP = 600;
                            collided = false;
                        }

                        // controle d'accident
                        if (detectCollision()) {
                            enMovementP = false;
                            Thread.sleep(3000);  // Pause après l'accident
                        }
                    } else {
                        enMovementP = false;
                        // si pieton arrive au milieu quand feu est vert, retourne au trottoir
                        if (axeYP < 300) {
                            axeYP = 600;
                        }
                    }
                }
                // attendre pour le prochain cycle
                Thread.sleep(16);  // 60 FPS
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void reprendreMouvement() {
        enMovementP = true;
        notify();
    }
}
