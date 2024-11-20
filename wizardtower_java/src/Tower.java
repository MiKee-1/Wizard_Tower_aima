import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Tower {
    private final int rows, cols;
    private final int startX, startY;
    private final char[][] grid;
    private boolean useVariant;
    
        public Tower(int rows, int cols, int potions, int creatures, int startX, int startY, char[][] grid) {
            this.rows = rows;
            this.cols = cols;
            this.startX = startX;
            this.startY = startY;
            this.grid = grid;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\nUtilizzare la variante? (si/no):");
            try {
                String input = reader.readLine();
                if (input.equalsIgnoreCase("si")) {  
                    System.out.println("utilizzo della variante");
                    this.useVariant = true;
                } else {
                    System.out.println("variante non utilizzata");
                    this.useVariant = false;
                }
            } catch (IOException e) {
                System.out.println("Errore di input/output: " + e.getMessage());
                this.useVariant = false;  
        }
    }

    public TowerState getInitialState() {
        Set<Point> remainingPotions = new HashSet<>();
        Set<Point> remainingCreatures = new HashSet<>();
        
        // Trova tutte le pozioni e creature nella griglia
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'M') {
                    remainingPotions.add(new Point(i, j));
                } else if (grid[i][j] == 'C') {
                    remainingCreatures.add(new Point(i, j));
                }
            }
        }
        
        return new TowerState(startX, startY, false, remainingPotions, remainingCreatures);
    }

    public List<WizardAction> getActions(TowerState state) {
        List<WizardAction> actions = new ArrayList<>();

        // Controlla le mosse possibili in tutte le direzioni
        checkMove(state, state.x - 1, state.y, "UP", actions);
        checkMove(state, state.x + 1, state.y, "DOWN", actions);
        checkMove(state, state.x, state.y - 1, "LEFT", actions);
        checkMove(state, state.x, state.y + 1, "RIGHT", actions);

        // Azione di raccolta pozione
        Point currentPos = new Point(state.x, state.y);
        if (!state.hasPotion && state.remainingPotions.contains(currentPos)) {
            actions.add(new WizardAction("PICK_POTION"));
        }

        return actions;
    }

    private void checkMove(TowerState state, int newX, int newY, String direction, List<WizardAction> actions) {
        if (isValidPosition(newX, newY)) {
            boolean canMove = false;
            
            if (grid[newX][newY] == 'C') {
                if (state.hasPotion || useVariant) {
                    canMove = true;
                }
            } else if (grid[newX][newY] != 'B') {
                canMove = true;
            }
            
            if (canMove) {
                actions.add(new WizardAction("MOVE_" + direction));
            }
        }
    }

    public TowerState getResult(TowerState state, WizardAction action) {
        int newX = state.x;
        int newY = state.y;
        boolean hasPotion = state.hasPotion;
        Set<Point> remainingPotions = new HashSet<>(state.remainingPotions);
        Set<Point> remainingCreatures = new HashSet<>(state.remainingCreatures);

        switch (action.getName()) {
            case "MOVE_UP": newX--; break;
            case "MOVE_DOWN": newX++; break;
            case "MOVE_LEFT": newY--; break;
            case "MOVE_RIGHT": newY++; break;
            case "PICK_POTION":
                hasPotion = true;
                remainingPotions.remove(new Point(state.x, state.y));
                break;
        }

        // Gestione dell'incontro con una creatura
        Point newPos = new Point(newX, newY);
        if (remainingCreatures.contains(newPos)) {
            if (hasPotion) {
                remainingCreatures.remove(newPos);
                hasPotion = false;
            }
        }

        return new TowerState(newX, newY, hasPotion, remainingPotions, remainingCreatures);
    }

    public boolean isGoal(TowerState state) {
        return state.remainingCreatures.isEmpty() && grid[state.x][state.y] == 'P';
    }

    public double getStepCost(TowerState state, WizardAction action, TowerState nextState) {
        if (useVariant) {
            if (grid[nextState.x][nextState.y] == 'C' && !state.hasPotion) {
                return 2.0;
            }
        }
        return 1.0;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }
    
    public int getrows() {
        return this.rows;
    }

    public int getcols(){
        return this.cols;
    }

    public char[][] getgrid(){
        return this.grid;
    }
}