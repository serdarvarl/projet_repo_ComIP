public class Vehicules implements Runnable {
    double axeXV;
    double axeYV;
    private Feu trafficLight;
    private boolean moving;

    public Vehicules(double axeXV, double axeYV, Feu trafficLight) {
        this.axeXV = axeXV;
        this.axeYV = axeYV;
        this.trafficLight = trafficLight;
        this.moving = true;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (!trafficLight.getCouleur().equals("Rouge")) {  // veichule attendre au rouge
                    axeXV += 2;
                    if (axeXV > 800) {
                        axeXV = -100;  // si le veichle quitte l'ecran, revint au debut
                    }
                }
                Thread.sleep(16);  // 60 FPS
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
