public class Feu implements Runnable {
    private String couleur;
    private int delay; // Délai de changement

    public Feu(int delay, String initialColor) {
        this.delay = delay;
        this.couleur = initialColor;
    }
    //hazem + serdar
    public String getCouleur() {
        return this.couleur;
    }

    public void changerCouleur(String nouvelleCouleur) {
        this.couleur = nouvelleCouleur;
    }

    public void setDelay(int newDelay) {
        this.delay = newDelay;
    }

    public int getDelay() {
        return this.delay;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay); // Délai initial
            while (true) {
                if (couleur.equals("Vert")) {
                    Thread.sleep((5000 + delay));  // 5 secondes vert
                    changerCouleur("Jaune");
                } else if (couleur.equals("Jaune")) {
                    Thread.sleep((1000 + delay));  // 1 seconde jaune
                    changerCouleur("Rouge");
                } else if (couleur.equals("Rouge")) {
                    Thread.sleep((3000 + delay));  // 3 secondes rouge
                    changerCouleur("Vert");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
