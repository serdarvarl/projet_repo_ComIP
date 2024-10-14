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
                // Yeşil ışık
                if (couleur.equals("Vert")) {
                    Thread.sleep(5000);  // 5 seconde
                    changerCouleur("Jaune");
                }
                // Sarı ışık
                else if (couleur.equals("Jaune")) {
                    Thread.sleep(2000);  // 2 seconde
                    changerCouleur("Rouge");
                }
                // Kırmızı ışık
                else if (couleur.equals("Rouge")) {
                    Thread.sleep(5000);  // 5 seconde
                    changerCouleur("Vert");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
