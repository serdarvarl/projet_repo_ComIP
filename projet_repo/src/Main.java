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
            // premier feu
            Feu trafficLight1 = new Feu(0, "Rouge");
            Thread trafficLightThread1 = new Thread(trafficLight1);
            trafficLightThread1.start();

            // deuxiem feu avec delay 3ms
            Feu trafficLight2 = new Feu(3000, "Jaune");
            Thread trafficLightThread2 = new Thread(trafficLight2);
            trafficLightThread2.start();

            // lancer objet simulation avec 2 feu different ,meme compartement
            JFrame frame = new JFrame("Simulation de trafic");
            SimulationPanel simulationPanel = new SimulationPanel(trafficLight1, trafficLight2);
            frame.add(simulationPanel);
            frame.setSize(942, 730);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
