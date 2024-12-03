import static java.lang.Math.*;
import java.util.List;
//hazem
public class Vehicules implements Runnable {
    private double axeXV; //
    private double axeYV; //
    private Feu trafficLight1; // First traffic light
    private Feu trafficLight2; // Second traffic light
    private boolean enMovement; //
    private List<Vehicules> vehicles; // list de vehucile
    private static final double MIN_DISTANCE = 45; // Minimum distance entre vehicules
    private double speed; // Speed of the vehicle
    private static final double MIN_Y = 180.0; // limite minimale de l'axe Y
    private static final double MAX_Y = 540.0; // limite maximale de l'axe Y
    private static final double MIN_X = 80.0; // limite minimale de l'axe X
    private static final double MAX_X = 840.0; // limite maximale de l'axe X
    private int directionX = 1;// 1 = vers la droite, -1 = vers la gauche, 0 = haut/bas
    private int directionY = 0;// 1 = vers le bas, -1 = vers le haut, 0 = gauche/droite


    public Vehicules(double axeXV, double axeYV, Feu trafficLight1, Feu trafficLight2, List<Vehicules> vehicles) {
        this.axeXV = axeXV;
        this.axeYV = axeYV;
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;
        this.vehicles = vehicles;
        this.enMovement = true; // commence movement
        this.speed = 5.0; // Initial vites
    }

    //X
    public double getAxeXV() {
        return axeXV;
    }

    public void setAxeXV(double axeXV) {
        this.axeXV = axeXV;
    }

    //Y
    public double getAxeYV() {
        return axeYV;
    }

    public void setAxeYV(double axeYV) {
        this.axeYV = axeYV;
    }

    //
    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
        System.out.println("Vehicle speed set to: " + newSpeed);
    }

    //Serar
    // to close entre vehucile
    private boolean isTooCloseToVehicleAhead() {
        for (Vehicules vehicle : vehicles) {
            if (vehicle != this && vehicle.getAxeXV() > this.axeXV && vehicle.getAxeXV() - this.axeXV < MIN_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    // feu control methode
    //serdar
    public void verifierFeu() {
        double distanceToTrafficLight1 = Math.abs(axeXV - 370); // 1er feu (420,240)
        double distanceToTrafficLight2 = Math.abs(axeXV - 350); // 2ieme feu (300,600)



        //// tous les agent relatione diffrent zone avec logique etc etc etc ....
        //serdar
        if (distanceToTrafficLight1 <= 50) { //  feu 1 : La réponse est donnée à partir de 50 pixels
            if (trafficLight1.getCouleur().equals("Rouge")) {
                this.enMovement = false; // S'arrête au feu rouge
            } else if (trafficLight1.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Ralentir au feu jaune
            } else {
                this.enMovement = true;
                //this.speed = 2.0; // Vitesse normale au feu vert
            }
        } else if (distanceToTrafficLight2 <= 50) { // Feu 2 : La réponse est donnée à partir de 50 pixels
            if (trafficLight2.getCouleur().equals("Rouge")) {
                this.enMovement = false; // S'arrête au feu rouge
            } else if (trafficLight2.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Ralentir au feu jaune
            } else {
                this.enMovement = true;
                //this.speed = 2.0; // Vitesse normale au feu vert
            }
        } else {
            //Si les vehicule sont loin de la lumière, continuez à vitesse normale.
            this.enMovement = true;
            //this.speed = 2.0;
        }
    }
    //serdar
    // voiture movement chemin
    @Override
    public void run() {
        try {
            while (true) {
                verifierFeu(); // mettre à jour la vitesse en fonction du feu

                if (enMovement && !isTooCloseToVehicleAhead()) { // le véhicule peut se déplacer s'il y a suffisamment de distance avec le véhicule précédent
                    if (directionX == 1) { // Mouvement vers la droite
                        axeXV += speed;
                        if (axeXV > MAX_X) {
                            axeXV = MAX_X;  // Limite droite atteinte
                            directionX = 0;  // Limite droite atteinte
                            directionY = 1;  // Arrêter le mouvement horizontal
                        }
                    } else if (directionX == -1) { // Mouvement vers la gauche
                        axeXV -= speed;
                        if (axeXV < MIN_X) {
                            axeXV = MIN_X;// Limite gauche atteinte
                            directionX = 0;// Arrêter le mouvement horizontal
                            directionY = -1;  // Commencer à se déplacer vers le haut
                        }
                    } else if (directionY == 1) { // Mouvement vers le bas
                        axeYV += speed;
                        if (axeYV > MAX_Y) {
                            axeYV = MAX_Y;// Limite inférieure atteinte
                            directionY = 0;// Arrêter le mouvement vertical
                            directionX = -1;  // Commencer à se déplacer vers la gauche
                        }
                    } else if (directionY == -1) { // Mouvement vers le haut
                        axeYV -= speed;
                        if (axeYV < MIN_Y) {
                            axeYV = MIN_Y;// Limite supérieure atteinte
                            directionY = 0;// Arrêter le mouvement vertical
                            directionX = 1; // Commencer à se déplacer vers la droite
                        }
                    }

                    System.out.println("Vehicle is moving. Position: (" + axeXV + ", " + axeYV + ")");
                } else if (!enMovement) {
                    System.out.println("Vehicle stopped at a red light.");
                } else {
                    System.out.println("Vehicle is slowing down due to proximity to another vehicle.");
                }

                Thread.sleep(16); //// attendre 16 ms pour 60 FPS
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // // marquer le thread comme interrompu
            System.out.println("Thread interrupted.");
        }
    }
    //hazem
    // "notify" permet de relancer le mouvement
    public synchronized void reprendreMouvement() {
        enMovement = true;
        notify();
    }
}
