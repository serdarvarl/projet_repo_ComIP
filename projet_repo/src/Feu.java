public class Feu {
    private String couleur;
    public Feu(){
        this.couleur="rouge";

    }

    public String getCouleur(){
        return this.couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
    public void changerCouleur(String nouvelleCouleur){
        this.couleur=nouvelleCouleur;
    }
}
