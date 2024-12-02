import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/*
// Serdar VAROL

Cette simulation se compose de 7 véhicules, 6 piétons et deux feux tricolores.

Le nombre de piétons et de véhicules peut modifié #SimulationPanel.java ligne 41

il y a un chemin de longueur fixe

Les véhicules bougent de 2 pixels par mise à jour c-à-d 16 millisecondes.

    1000ms /16 ms = 62,5 (60 FPS)

    2 pixels/boucle * 62,5 boucles/seconde = 125 pixels/seconde

    1 ms quand ils ralentissent au jaune

    Il n'y a pas de ralentissement dans les virages, il y a de l'attente. pour verifier prochaine ...

il y a 2 groupe pieton  nort speed 2 , sud speed 1

il y a 2 feux idendique mais intial color different avec 3 ms delay


 */

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // creee feu
            Feu trafficLight1 = new Feu(0, "Rouge");
            Feu trafficLight2 = new Feu(3000, "Jaune");

            //cree frame
            JFrame frame = new JFrame("Simulation de trafic");

            // cree panel lancer toutss
            SimulationPanel simulationPanel = new SimulationPanel(trafficLight1, trafficLight2);
            frame.add(simulationPanel);

            // dimnetion
            frame.setSize(942, 942);

            // si ferme fenetre
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //visible
            frame.setVisible(true);
        });
    }
}

