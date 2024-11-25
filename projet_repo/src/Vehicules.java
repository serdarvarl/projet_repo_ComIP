import static java.lang.Math.*;
import java.util.List;

public class Vehicules implements Runnable {
    private double axeXV;
    private double axeYV;
    private Feu trafficLight1;
    private Feu trafficLight2;
    private boolean enMovement;
    private List<Vehicules> vehicles;  // liste de vehicules
    private static final double MIN_DISTANCE = 30.0;  // distance minimale entre 2 véhicules
    private double speed; // vitesse du véhicule
    private static final double MIN_Y = 180.0; // limite minimale de l'axe Y
    private static final double MAX_Y = 540.0; // limite maximale de l'axe Y
    private static final double MIN_X = 80.0; // limite minimale de l'axe X
    private static final double MAX_X = 840.0; // limite maximale de l'axe X
    private int directionX = 1; // 1 = vers la droite, -1 = vers la gauche, 0 = haut/bas
    private int directionY = 0; // 1 = vers le bas, -1 = vers le haut, 0 = gauche/droite

    public Vehicules(double axeXV, double axeYV, Feu trafficLight1, Feu trafficLight2, List<Vehicules> vehicles) {
        this.axeXV = axeXV;
        this.axeYV = axeYV;
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;
        this.vehicles = vehicles;
        this.enMovement = true;
        this.speed = 2.0; // vitesse initiale
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

    // contrôle de la distance entre 2 véhicules
    //serdar




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
        double distanceToTrafficLight1 = Math.abs(axeXV - 370);  // 1er feu (420,240)
        double distanceToTrafficLight2 = Math.abs(axeXV - 350);  // 2ieme feu (300,600)


        //// tous les agent relatione diffrent zone avec logique etc etc etc ....
        //serdar
        if (distanceToTrafficLight1 <= 50) { // feu 1 : La réponse est donnée à partir de 50 pixels
            if (trafficLight1.getCouleur().equals("Rouge")) {
                this.enMovement = false; // S'arrête au feu rouge
            } else if (trafficLight1.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Ralentir au feu jaune
            } else {
                this.enMovement = true;
                this.speed = 2.0; // Vitesse normale au feu vert
            }
        } else if (distanceToTrafficLight2 <= 50) { // Feu 2 : La réponse est donnée à partir de 50 pixels
            if (trafficLight2.getCouleur().equals("Rouge")) {
                this.enMovement = false; // S'arrête au feu rouge
            } else if (trafficLight2.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Ralentir au feu jaune
            } else {
                this.enMovement = true;
                this.speed = 2.0; // Vitesse normale au feu vert
            }
        } else {
            // Si les vehicule sont loin de la lumière, continuez à vitesse normale.
            this.enMovement = true;
            this.speed = 2.0;
        }
    }


    //serdar
    @Override
    public void run() {
        try {
            while (true) {
                verifierFeu();  // mettre à jour la vitesse en fonction du feu

                if (enMovement && !isTooCloseToVehicleAhead()) { // le véhicule peut se déplacer s'il y a suffisamment de distance avec le véhicule précédent
                    if (directionX == 1) {
                        // Mouvement vers la droite
                        axeXV += speed;
                        if (axeXV > MAX_X) {
                            axeXV = MAX_X;  // Limite droite atteinte
                            directionX = 0; // Arrêter le mouvement horizontal
                            directionY = 1; // Commencer à se déplacer vers le bas

                            //speed = 3.0;
                        }
                    } else if (directionX == -1) {
                        // Mouvement vers la gauche
                        axeXV -= speed;
                        if (axeXV < MIN_X) {
                            axeXV = MIN_X;  // Limite gauche atteinte
                            directionX = 0; // Arrêter le mouvement horizontal
                            directionY = -1; // Commencer à se déplacer vers le haut

                            //speed = 3.0;
                        }
                    } else if (directionY == 1) {
                        // Mouvement vers le bas
                        axeYV += speed;
                        if (axeYV > MAX_Y) {
                            axeYV = MAX_Y;  // Limite inférieure atteinte
                            directionY = 0; // Arrêter le mouvement vertical
                            directionX = -1; // Commencer à se déplacer vers la gauche

                            //speed = 3.0;
                        }
                    } else if (directionY == -1) {
                        // Mouvement vers le haut
                        axeYV -= speed;
                        if (axeYV < MIN_Y) {
                            axeYV = MIN_Y;  // Limite supérieure atteinte
                            directionY = 0; // Arrêter le mouvement vertical
                            directionX = 1; // Commencer à se déplacer vers la droite

                            //speed = 3.0;
                        }
                    }

                    System.out.println("Véhicule se déplace. Position : (" + axeXV + ", " + axeYV + ")");
                } else if (!enMovement) {
                    System.out.println("Véhicule arrêté à la lumière rouge.");
                } else {
                    System.out.println("Véhicule ralentit car il est trop près du véhicule qui le précède.");
                }

                Thread.sleep(16);  // attendre 16 ms pour 60 FPS
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // marquer le thread comme interrompu
            System.out.println("Thread interrompu.");
        }
    }

    // "notify" permet de relancer le mouvement
    public synchronized void reprendreMouvement() {
        enMovement = true;
        notify();
    }
}
