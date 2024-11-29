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
    // Main method to start the simulation application
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure the GUI creation is done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create traffic lights with initial delays and colors
            Feu trafficLight1 = new Feu(0, "Rouge");
            Feu trafficLight2 = new Feu(3000, "Jaune");

            // Create the main frame for the simulation
            JFrame frame = new JFrame("Simulation de trafic");

            // Create the simulation panel and add it to the frame
            SimulationPanel simulationPanel = new SimulationPanel(trafficLight1, trafficLight2);
            frame.add(simulationPanel);

            // Set the size of the frame
            frame.setSize(942, 942);

            // Set the default close operation to exit the application when the frame is closed
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Make the frame visible
            frame.setVisible(true);
        });
    }
}

