public class Feu implements Runnable {
    private String couleur;

    public Feu() {
        this.couleur = "Vert";  // Başlangıç rengi yeşil
    }

    public String getCouleur() {
        return this.couleur;
    }

    public void changerCouleur(String nouvelleCouleur) {
        this.couleur = nouvelleCouleur;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // vert
                if (couleur.equals("Vert")) {
                    Thread.sleep(2000);  // 5 seconde
                    changerCouleur("Jaune");
                }
                // jaune
                else if (couleur.equals("Jaune")) {
                    Thread.sleep(750);  // 2 seconde
                    changerCouleur("Rouge");
                }
                // rouge
                else if (couleur.equals("Rouge")) {
                    Thread.sleep(1000);  // 5 seconde
                    changerCouleur("Vert");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
