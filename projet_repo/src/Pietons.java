public class Pietons implements Runnable {
    double axeXP;
    double axeYP;
    private Feu trafficLight;
    private int waitTime;

    public Pietons(double axeXP, double axeYP, Feu trafficLight) {
        this.axeXP = axeXP;
        this.axeYP = axeYP;
        this.trafficLight = trafficLight;
        this.waitTime = 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (trafficLight.getCouleur().equals("Rouge")) {  // pieton passe au rouge
                    axeYP -= 2;
                    if (axeYP < -50) {
                        axeYP = 600;  // si le pieton quitte l'ecran, revient au debut
                    }
                }

                // pieton attendent au vert
                Thread.sleep(16);  // 60 FPS
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
