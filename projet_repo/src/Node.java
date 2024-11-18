// Node class represente chauqe point en grid pour pathfinding
public class Node {
    int x;
    int y;
    double gCost;
    double hCost;
    Node parent;
    boolean isWaypoint; // c'est un point ?

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.gCost = 0.0;
        this.hCost = 0.0;
        this.parent = null;
        this.isWaypoint = false; // Ce n'est pas un point :/
    }

    public double getFCost() {
        return this.gCost + this.hCost;
    }

    public void setWaypoint(boolean isWaypoint) {
        this.isWaypoint = isWaypoint;
    }

    public boolean isWaypoint() {
        return isWaypoint;
    }
}
