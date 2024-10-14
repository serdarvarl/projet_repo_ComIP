import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Plan {
    private int[][] grid;

    public Plan(String filePath) throws IOException {
        readCSV(filePath);
    }

    private void readCSV(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        List<int[]> rows = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] values = line.split(","); // Valeurs séparées par des virgules
            int[] row = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                row[i] = Integer.parseInt(values[i].trim());
            }
            rows.add(row);
        }

        // Convertir la liste de lignes en tableau
        grid = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            grid[i] = rows.get(i);
        }

        br.close();
    }

    public int[][] getGrid() {
        return grid;
    }
}
