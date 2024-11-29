import static java.lang.Math.*;
import java.util.List;

public class Vehicules implements Runnable {
    private double axeXV; // X-coordinate of the vehicle
    private double axeYV; // Y-coordinate of the vehicle
    private Feu trafficLight1; // First traffic light
    private Feu trafficLight2; // Second traffic light
    private boolean enMovement; // Indicates if the vehicle is moving
    private List<Vehicules> vehicles; // List of all vehicles on the road
    private static final double MIN_DISTANCE = 45; // Minimum distance between vehicles
    private double speed; // Speed of the vehicle
    private static final double MIN_Y = 180.0; // Minimum Y boundary
    private static final double MAX_Y = 540.0; // Maximum Y boundary
    private static final double MIN_X = 80.0; // Minimum X boundary
    private static final double MAX_X = 840.0; // Maximum X boundary
    private int directionX = 1; // Movement direction on the X-axis: 1 = right, -1 = left, 0 = no movement
    private int directionY = 0; // Movement direction on the Y-axis: 1 = down, -1 = up, 0 = no movement

    // Constructor to initialize the vehicle's position, traffic lights, and other parameters
    public Vehicules(double axeXV, double axeYV, Feu trafficLight1, Feu trafficLight2, List<Vehicules> vehicles) {
        this.axeXV = axeXV;
        this.axeYV = axeYV;
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;
        this.vehicles = vehicles;
        this.enMovement = true; // The vehicle starts in motion
        this.speed = 5.0; // Initial speed
    }

    // Getter and setter for the X-coordinate
    public double getAxeXV() {
        return axeXV;
    }

    public void setAxeXV(double axeXV) {
        this.axeXV = axeXV;
    }

    // Getter and setter for the Y-coordinate
    public double getAxeYV() {
        return axeYV;
    }

    public void setAxeYV(double axeYV) {
        this.axeYV = axeYV;
    }

    // Getter and setter for the speed
    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
        System.out.println("Vehicle speed set to: " + newSpeed);
    }

    // Checks if the vehicle is too close to another vehicle ahead
    private boolean isTooCloseToVehicleAhead() {
        for (Vehicules vehicle : vehicles) {
            if (vehicle != this && vehicle.getAxeXV() > this.axeXV && vehicle.getAxeXV() - this.axeXV < MIN_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    // Checks the status of the traffic lights and adjusts the vehicle's movement accordingly
    public void verifierFeu() {
        double distanceToTrafficLight1 = Math.abs(axeXV - 370); // Distance to the first traffic light
        double distanceToTrafficLight2 = Math.abs(axeXV - 350); // Distance to the second traffic light

        if (distanceToTrafficLight1 <= 50) { // Near the first traffic light
            if (trafficLight1.getCouleur().equals("Rouge")) {
                this.enMovement = false; // Stop at a red light
            } else if (trafficLight1.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Slow down at a yellow light
            } else {
                this.enMovement = true;
                //this.speed = 2.0; // Normal speed at a green light
            }
        } else if (distanceToTrafficLight2 <= 50) { // Near the second traffic light
            if (trafficLight2.getCouleur().equals("Rouge")) {
                this.enMovement = false; // Stop at a red light
            } else if (trafficLight2.getCouleur().equals("Jaune")) {
                this.enMovement = true;
                this.speed = 1.0; // Slow down at a yellow light
            } else {
                this.enMovement = true;
                //this.speed = 2.0; // Normal speed at a green light
            }
        } else {
            // If the vehicle is far from the lights, maintain normal speed
            this.enMovement = true;
            //this.speed = 2.0;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                verifierFeu(); // Update speed based on traffic light

                if (enMovement && !isTooCloseToVehicleAhead()) { // Vehicle moves if conditions allow
                    if (directionX == 1) { // Moving right
                        axeXV += speed;
                        if (axeXV > MAX_X) { // Right boundary reached
                            axeXV = MAX_X;
                            directionX = 0; // Stop horizontal movement
                            directionY = 1; // Start moving down
                        }
                    } else if (directionX == -1) { // Moving left
                        axeXV -= speed;
                        if (axeXV < MIN_X) { // Left boundary reached
                            axeXV = MIN_X;
                            directionX = 0;
                            directionY = -1; // Start moving up
                        }
                    } else if (directionY == 1) { // Moving down
                        axeYV += speed;
                        if (axeYV > MAX_Y) { // Bottom boundary reached
                            axeYV = MAX_Y;
                            directionY = 0;
                            directionX = -1; // Start moving left
                        }
                    } else if (directionY == -1) { // Moving up
                        axeYV -= speed;
                        if (axeYV < MIN_Y) { // Top boundary reached
                            axeYV = MIN_Y;
                            directionY = 0;
                            directionX = 1; // Start moving right
                        }
                    }

                    System.out.println("Vehicle is moving. Position: (" + axeXV + ", " + axeYV + ")");
                } else if (!enMovement) {
                    System.out.println("Vehicle stopped at a red light.");
                } else {
                    System.out.println("Vehicle is slowing down due to proximity to another vehicle.");
                }

                Thread.sleep(16); // Wait 16 ms for 60 FPS simulation
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Mark thread as interrupted
            System.out.println("Thread interrupted.");
        }
    }

    // Method to resume movement, synchronized for thread safety
    public synchronized void reprendreMouvement() {
        enMovement = true;
        notify();
    }
}
