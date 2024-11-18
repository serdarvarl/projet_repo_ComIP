//majorite par L'IA
import java.util.*;

public class AStarPathfinding {
    private final int[][] grid; // Yol ağı
    private final int gridWidth, gridHeight;

    public AStarPathfinding(int[][] grid) {
        this.grid = grid;
        this.gridWidth = grid.length;
        this.gridHeight = grid[0].length;
    }

    // trouver plus court chemin depart entre arrive
    public List<Node> findPath(Node startNode, Node endNode) {
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();

        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.get(0);

            // plus bas cost
            for (Node node : openList) {
                if (node.getFCost() < currentNode.getFCost() ||
                        (node.getFCost() == currentNode.getFCost() && node.hCost < currentNode.hCost)) {
                    currentNode = node;
                }
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            // si on arrive node cible cree le chemin
            if (currentNode.x == endNode.x && currentNode.y == endNode.y) {
                return reconstructPath(currentNode);
            }

            // node voisine
            for (Node neighbor : getNeighbors(currentNode)) {
                if (isWalkable(neighbor) && !closedList.contains(neighbor)) {
                    double newMovementCostToNeighbor = currentNode.gCost + getDistance(currentNode, neighbor);
                    if (newMovementCostToNeighbor < neighbor.gCost || !openList.contains(neighbor)) {
                        neighbor.gCost = newMovementCostToNeighbor;
                        neighbor.hCost = getDistance(neighbor, endNode);
                        neighbor.parent = currentNode;

                        if (!openList.contains(neighbor)) openList.add(neighbor);
                    }
                }
            }
        }

        return null; // aucune chemin
    }

    // cree chemin commnecer cible
    private List<Node> reconstructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        return path;
    }

    //
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        // ajoter voisine node
        if (node.x - 1 >= 0) neighbors.add(new Node(node.x - 1, node.y));
        if (node.x + 1 < gridWidth) neighbors.add(new Node(node.x + 1, node.y));
        if (node.y - 1 >= 0) neighbors.add(new Node(node.x, node.y - 1));
        if (node.y + 1 < gridHeight) neighbors.add(new Node(node.x, node.y + 1));

        return neighbors;
    }

    // tester node
    private boolean isWalkable(Node node) {
        // verifie coordone dans grille
        if (node.x < 0 || node.x >= grid.length || node.y < 0 || node.y >= grid[0].length) {
            return false;
        }
        return grid[node.x][node.y] == 0; // Yürüme alanını temsil eden değer
    }

    // (Manhattan heuristic) calcule distance
    private double getDistance(Node a, Node b) {
        int distX = Math.abs(a.x - b.x);
        int distY = Math.abs(a.y - b.y);
        return distX + distY;
    }
}