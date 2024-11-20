import aima.search.framework.HeuristicFunction;
import java.util.*;

class WizardHeuristicFunction implements HeuristicFunction {
    private final Tower tower;

    public WizardHeuristicFunction(Tower tower) {
        this.tower = tower;
    }

    @Override
    public double getHeuristicValue(Object state) {
        TowerState currentState = (TowerState) state;
        Point portalPosition = findPortalPosition();
        
        // Se non ci sono creature rimanenti, si punta al portale.
        if (currentState.remainingCreatures.isEmpty()) {
            return manhattanDistance(currentState.x, currentState.y, portalPosition.x, portalPosition.y);
        }
        
        // Individua la creatura più vicina
        Point nearestCreature = findNearestCreature(currentState);
        
        // Individua la pozione più vicina, se esiste
        Optional <Point> nearestPotion = findNearestPotion(currentState);
        
        // Valuta se conviene raggiungere la creatura più vicina o la pozione più vicina
        int distanceToCreature = manhattanDistance(currentState.x, currentState.y, nearestCreature.x, nearestCreature.y);
        int distanceToPotion = nearestPotion.map(potion -> manhattanDistance(currentState.x, currentState.y, potion.x, potion.y)).orElse(Integer.MAX_VALUE);
        
        // Se una pozione è vicina e serve al mago, consideriamo quella come priorità
        return Math.min(distanceToCreature, distanceToPotion);
    }

    private Point findPortalPosition() {
        for (int i = 0; i < tower.getrows(); i++) {
            for (int j = 0; j < tower.getcols(); j++) {
                if (tower.getgrid()[i][j] == 'P') {
                    return new Point(i, j);
                }
            }
        }
        throw new IllegalStateException("Il portale non è stato trovato nella griglia.");
    }

    private Point findNearestCreature(TowerState state) {
        Point currentPos = new Point(state.x, state.y);
        return state.remainingCreatures.stream()
            .min(Comparator.comparingInt(creature -> manhattanDistance(currentPos.x, currentPos.y, creature.x, creature.y)))
            .orElseThrow(() -> new IllegalStateException("Non ci sono creature rimanenti."));
    }
    
    private Optional<Point> findNearestPotion(TowerState state) {
        Point currentPos = new Point(state.x, state.y);
        return state.remainingPotions.stream()
            .min(Comparator.comparingInt(potion -> manhattanDistance(currentPos.x, currentPos.y, potion.x, potion.y)));
    }

    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
