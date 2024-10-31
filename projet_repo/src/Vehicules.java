import static java.lang.Math.*;
import java.util.List;

public class Vehicules implements Runnable {
    private double axeXV;
    private double axeYV;
    private Feu trafficLight;
    private boolean enMovement;
    private List<Vehicules> vehicles;  // liste de vehicules
    private static final double MIN_DISTANCE = 50.0;  // min distance entre 2 vehiules
    private double speed; // vites vehicule

    public Vehicules(double axeXV, double axeYV, Feu trafficLight, List<Vehicules> vehicles) {
        this.axeXV = axeXV;
        this.axeYV = axeYV;
        this.trafficLight = trafficLight;
        this.vehicles = vehicles;
        this.enMovement = true;
        this.speed = 2.0; // inital vites
    }

    // X
    public double getAxeXV() {
        return axeXV;
    }

    // X
    public void setAxeXV(double axeXV) {
        this.axeXV = axeXV;
    }

    // Y
    public double getAxeYV() {
        return axeYV;
    }

    // Y
    public void setAxeYV(double axeYV) {
        this.axeYV = axeYV;
    }

    // control distance entre 2 vehicules
    private boolean isTooCloseToVehicleAhead() {
        for (Vehicules vehicle : vehicles) {
            if (vehicle != this && vehicle.getAxeXV() > this.axeXV && vehicle.getAxeXV() - this.axeXV < MIN_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    // feu
    public void verifierFeu() {
        if (trafficLight.getCouleur().equals("Vert")) {
            this.enMovement = true;
            this.speed = 2.0; // vites vert
        } else if (trafficLight.getCouleur().equals("Jaune")) {
            this.enMovement = true;
            this.speed = 1.0; // vites jaune(relatisent)
        } else if (trafficLight.getCouleur().equals("Rouge") && axeXV < 400) { // 400 cordinant feu
            this.enMovement = false; // rouge arreter
        }
    }

    // calcul distance autre pieton
    public double calculerDistance(Pietons pieton) {
        double dx = this.axeXV - pieton.getAxeXP();
        double dy = this.axeYV - pieton.getAxeYP();
        return sqrt(pow(dx, 2) + pow(dy, 2));
    }

    // calcule distance autre vehicule
    public double calculerDistanceVV(Vehicules autreVehicule) {
        double dx = this.axeXV - autreVehicule.getAxeXV();
        double dy = this.axeYV - autreVehicule.getAxeYV();
        return sqrt(pow(dx, 2) + pow(dy, 2));
    }

    // run() control vites vehicule
    @Override
    public void run() {
        try {
            while (true) {
                verifierFeu();  // selon le feu update vites

                if (enMovement && !isTooCloseToVehicleAhead()) { // vehicule peut se deplace si y a distance avec autre vehiucle
                    axeXV += speed;  // distance x
                    if (axeXV > 800) {
                        axeXV = -100;  // si l'ecran finir revient debut
                    }
                    System.out.println("Véhicule se déplace. Position : (" + axeXV + ", " + axeYV + ")");
                } else if (!enMovement) {
                    System.out.println("Véhicule arrêté à la lumière rouge.");
                } else {
                    System.out.println("Véhicule ralentit car il est trop près du véhicule qui le précède.");
                }

                Thread.sleep(16);  // 60 FPS
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // "notify" permet de relancer le mouvement
    public synchronized void reprendreMouvement() {
        enMovement = true;
        notify();
    }
}
