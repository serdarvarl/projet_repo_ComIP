// Node class represents each point on the grid for pathfinding
public class Node {
    int x;
    int y;
    double gCost;
    double hCost;
    Node parent;
    boolean isWaypoint; // Yeni özellik: Bu düğüm bir ara nokta mı?

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.gCost = 0.0;
        this.hCost = 0.0;
        this.parent = null;
        this.isWaypoint = false; // Varsayılan olarak ara nokta değil
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
