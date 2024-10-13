/*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Feu feu = new Feu();
        feu.changerCouleur("Vert");
        Pietons p=new Pietons(true,12,13);
        Vehicules v=new Vehicules(true,13,15);
        System.out.println(p.testerMovement(feu));
        System.out.println(v.calculerDistance(p));
        System.out.println(v.calculerAngle(p));
        System.out.println(v.verifierArret(p,3,180));

//Travail inspiré de chatgbt.
    }
}
*/

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create JFrame
        JFrame frame = new JFrame("Traffic Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

       // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); /
       // frame.setVisible(true);


        SimulationPanel simulationPanel = new SimulationPanel();
        frame.add(simulationPanel);

        // Set frame visibility
        frame.setVisible(true);

        // Start the simulation
        simulationPanel.startSimulation();



    }
}

/*
Ce code ne fait que produire une image car il manque toujours la fonction updateSimulation().
J'ai produit ce code avec l'aide de ChatGPT pour m'inspirer au début.
je vais contunier .....

image nous monrte que le rectangle vert est feu
                      le rectangle bleu est veichule
                      le circle jaune est pieton

ils ne bougent pas
 */
