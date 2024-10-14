public class Feu implements Runnable {
    private String couleur;

    public Feu() {
        this.couleur = "Rouge";  // initial feu rouge
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
            while (true) {
                changerCouleur("Vert");  // feu vert
                Thread.sleep(10000);  // 10 second
                changerCouleur("Jaune");  // feu jaune
                Thread.sleep(5000);  // 5 second
                changerCouleur("Rouge");  // feu rouge
                Thread.sleep(5000);  // 5 secon
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
