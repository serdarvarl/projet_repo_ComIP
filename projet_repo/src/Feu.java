public class Feu implements Runnable {
    private String couleur;

    public Feu() {
        this.couleur = "Vert";  // initial colour
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
                if (couleur.equals("Vert")) {
                    Thread.sleep(5000);  // 5 second vert
                    changerCouleur("Jaune");
                } else if (couleur.equals("Jaune")) {
                    Thread.sleep(1000);  // 1 second jaune
                    changerCouleur("Rouge");
                } else if (couleur.equals("Rouge")) {
                    Thread.sleep(3000);  // 3 second rouge
                    changerCouleur("Vert");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
