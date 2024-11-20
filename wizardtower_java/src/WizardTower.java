import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.framework.GraphSearch;
import aima.search.informed.AStarSearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;

public class WizardTower {
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {     
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nInserire il nome del file contenente la griglia:");
        String file=reader.readLine();
        
        Tower tower = loadTower("/home/mike/Desktop/fondamenti_IA/lab_aima/wizardtower_java/wizardtower_java/"+file);    
        Problem problem = new Problem(
            tower.getInitialState(),
            new WizardSuccessorFunction(tower),
            new WizardGoalTest(tower),
            new WizardStepCostFunction(tower),
            new WizardHeuristicFunction(tower)
        );

        
        Search search = new AStarSearch(new GraphSearch());
        SearchAgent agent = new SearchAgent(problem, search);

        // Output della soluzione
        List actions = agent.getActions();
        System.out.println("Passi per risolvere il problema:");
        for (Object action : actions) {
            System.out.println(action);
        }
        
        // Stampa statistiche
        System.out.println("\nStatistiche di ricerca:");
        Properties properties = agent.getInstrumentation();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String property = properties.getProperty(key);
            System.out.println(key + ": " + property);
        }
    }

    private static Tower loadTower(String fileName) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            int potions = scanner.nextInt();
            int creatures = scanner.nextInt();
            int startX = scanner.nextInt();
            int startY = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline

            char[][] grid = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line = scanner.nextLine();
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = line.charAt(j);
                }
            }
            return new Tower(rows, cols, potions, creatures, startX, startY, grid);
        }
    }
}