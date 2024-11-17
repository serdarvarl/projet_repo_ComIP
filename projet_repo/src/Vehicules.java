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
    private boolean isTooCloseToVehicleAhead() {
        for (Vehicules vehicle : vehicles) {
            if (vehicle != this && vehicle.getAxeXV() > this.axeXV && vehicle.getAxeXV() - this.axeXV < MIN_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    // Trafik ışığını kontrol eden metot
    public void verifierFeu() {
        double distanceToTrafficLight1 = Math.abs(axeXV - 370);  // İlk trafik ışığı koordinatı (420,240)
        double distanceToTrafficLight2 = Math.abs(axeXV - 350);  // İkinci trafik ışığı koordinatı (300,600)

        if (distanceToTrafficLight1 <= 50) { // İlk trafik ışığına 200 piksel kala tepki ver
            if (trafficLight1.getCouleur().equals("Rouge")) {
                this.enMovement = false; // Kırmızı ışıkta dur
            } else if (trafficLight1.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Sarı ışıkta yavaşlama
            } else {
                this.enMovement = true;
                this.speed = 2.0; // Yeşil ışıkta normal hız
            }
        } else if (distanceToTrafficLight2 <= 50) { // İkinci trafik ışığına 200 piksel kala tepki ver
            if (trafficLight2.getCouleur().equals("Rouge")) {
                this.enMovement = false; // Kırmızı ışıkta dur
            } else if (trafficLight2.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Sarı ışıkta yavaşlama
            } else {
                this.enMovement = true;
                this.speed = 2.0; // Yeşil ışıkta normal hız
            }
        } else {
            // Işıktan uzaktaysa normal hızda devam et
            this.enMovement = true;
            this.speed = 2.0;
        }
    }

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
                        }
                    } else if (directionX == -1) {
                        // Mouvement vers la gauche
                        axeXV -= speed;
                        if (axeXV < MIN_X) {
                            axeXV = MIN_X;  // Limite gauche atteinte
                            directionX = 0; // Arrêter le mouvement horizontal
                            directionY = -1; // Commencer à se déplacer vers le haut
                        }
                    } else if (directionY == 1) {
                        // Mouvement vers le bas
                        axeYV += speed;
                        if (axeYV > MAX_Y) {
                            axeYV = MAX_Y;  // Limite inférieure atteinte
                            directionY = 0; // Arrêter le mouvement vertical
                            directionX = -1; // Commencer à se déplacer vers la gauche
                        }
                    } else if (directionY == -1) {
                        // Mouvement vers le haut
                        axeYV -= speed;
                        if (axeYV < MIN_Y) {
                            axeYV = MIN_Y;  // Limite supérieure atteinte
                            directionY = 0; // Arrêter le mouvement vertical
                            directionX = 1; // Commencer à se déplacer vers la droite
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
