public class Feu implements Runnable {
    private String couleur;
    private int delay; // Gecikme süresi

    public Feu(int delay, String initialColor) {
        this.delay = delay;
        this.couleur = initialColor;
    }

    public String getCouleur() {
        return this.couleur;
    }

    public void changerCouleur(String nouvelleCouleur) {
        this.couleur = nouvelleCouleur;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay); // Başlangıçta gecikme
            while (true) {
                if (couleur.equals("Vert")) {
                    Thread.sleep(5000);  // 5 saniye vert
                    changerCouleur("Jaune");
                } else if (couleur.equals("Jaune")) {
                    Thread.sleep(1000);  // 1 saniye jaune
                    changerCouleur("Rouge");
                } else if (couleur.equals("Rouge")) {
                    Thread.sleep(3000);  // 3 saniye rouge
                    changerCouleur("Vert");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}