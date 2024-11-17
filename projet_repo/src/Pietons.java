import static java.lang.Math.*;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Pietons implements Runnable {
    private double axeXP;
    private double axeYP;
    private double targetX;
    private double targetY;
    private double speed; // Yeni eklenen hız değişkeni
    private boolean enMovementP;
    private Feu trafficLight1;
    private Feu trafficLight2;
    private List<Pietons> pedestrians;
    private List<Vehicules> vehicles;
    private Color color;
    private boolean collided;
    private static final double MIN_DISTANCE = 20.0;
    private SimulationPanel panel;  // pour verifier accident

    // Hareket sırası kontrolü için kilit
    private static final Object sequentialLock = new Object();

    // Ara noktalar
    private double[][] waypoints = {
            {240, 640}, {240, 460}, // 7 numaralı ışığın olduğu yaya geçidi
            {320, 360}, {460, 320}, // İki yaya geçidi arasındaki alan
            {480, 300}, {480, 140}  // 3 numaralı ışığın olduğu yaya geçidi
    };
    private int currentWaypointIndex = 0;

    public Pietons(double axeXP, double axeYP, double targetX, double targetY, double speed, Feu trafficLight1, Feu trafficLight2, List<Vehicules> vehicles, List<Pietons> pedestrians, SimulationPanel panel) {
        this.axeXP = axeXP;
        this.axeYP = axeYP;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed; // Hız değişkenini yapıcıda başlatıyoruz
        this.trafficLight1 = trafficLight1;
        this.trafficLight2 = trafficLight2;
        this.vehicles = vehicles;
        this.pedestrians = pedestrians;
        this.panel = panel;
        this.enMovementP = false;
        this.collided = false;

        // Rastgele renk atama
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

    // Çarpışma kontrolü
    private boolean detectCollision() {
        for (Vehicules vehicle : vehicles) {
            if (Math.abs(this.axeXP - vehicle.getAxeXV()) <= 20 && Math.abs(this.axeYP - vehicle.getAxeYV()) <= 20) {
                collided = true;
                panel.setCollisionOccurred(true, (int) this.axeXP, (int) this.axeYP); // Çarpışma pozisyonu
                return true;
            }
        }
        collided = false;
        return false;
    }

    @Override
    public void run() {
        try {
            // Yayaların 5 saniye aralıklarla çıkış yapması için bekleme süresi ekle
            Thread.sleep(5000);
            System.out.println("Pedestrian starting to move..."); // Test için başlangıç mesajı

            while (true) {
                if (!panel.isCollisionActive()) {
                    // Yaya geçidine yaklaşma kontrolü (7 numaralı ve 3 numaralı ışık noktaları)
                    boolean isNearTrafficLight7 = Math.abs(axeXP - 300) <= 20 && Math.abs(axeYP - 600) <= 20; // 7 numaralı ışığın olduğu yer
                    boolean isNearTrafficLight3 = Math.abs(axeXP - 420) <= 20 && Math.abs(axeYP - 240) <= 20; // 3 numaralı ışığın olduğu yer

                    if (isNearTrafficLight7 || isNearTrafficLight3) {
                        // Trafik ışığı kontrolleri
                        String trafficLightColor1 = trafficLight1.getCouleur();
                        String trafficLightColor2 = trafficLight2.getCouleur();

                        if (trafficLightColor1.equals("Vert") || trafficLightColor2.equals("Vert")) {
                            // Trafik ışığı yeşilse yayalar durmalı
                            enMovementP = false;
                            System.out.println("Pedestrian stopped due to green light at a crosswalk.");
                        } else {
                            // Trafik ışığı kırmızı veya sarıysa yayalar geçebilir
                            enMovementP = true;
                            System.out.println("Pedestrian is moving due to red/yellow light at a crosswalk.");
                        }
                    } else {
                        // Yaya geçidine yaklaşmamışsa normal ilerle
                        enMovementP = true;
                    }

                    // Hareket mantığı
                    if (enMovementP) {
                        moveTowardsNextWaypoint();
                    }

                    // Çarpışma kontrolü
                    if (detectCollision()) {
                        enMovementP = false;
                        System.out.println("Collision detected. Pedestrian is stopping.");
                        Thread.sleep(3000); // Çarpışma sonrası bekleme süresi
                    }
                }
                Thread.sleep(16); // 60 FPS
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void moveTowardsNextWaypoint() {
        if (currentWaypointIndex < waypoints.length) {
            double nextX = waypoints[currentWaypointIndex][0];
            double nextY = waypoints[currentWaypointIndex][1];

            // Bir sonraki waypoint'e ilerle
            if (Math.abs(axeXP - nextX) < 1 && Math.abs(axeYP - nextY) < 1) {
                currentWaypointIndex++;
            } else {
                if (axeXP < nextX) axeXP += 1;
                else if (axeXP > nextX) axeXP -= 1;

                if (axeYP < nextY) axeYP += 1;
                else if (axeYP > nextY) axeYP -= 1;

                // Işığın üzerinde durmasını engellemek için ek kontrol
                if (isNearTrafficLight(axeXP, axeYP)) {
                    System.out.println("Avoiding traffic light zone.");
                    currentWaypointIndex++;
                }
            }
        } else {
            // Hedefe ulaşıldığında başlangıç noktasına geri dön veya bekle
            currentWaypointIndex = 0;
        }
    }

    private boolean isNearTrafficLight(double x, double y) {
        return (Math.abs(x - 300) <= 20 && Math.abs(y - 600) <= 20) || (Math.abs(x - 420) <= 20 && Math.abs(y - 240) <= 20);
    }


}
