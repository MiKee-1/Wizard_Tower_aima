import java.util.*;

public class TowerState {
    final int x, y;
    final boolean hasPotion;
    final Set<Point> remainingPotions;
    final Set<Point> remainingCreatures;    
    public TowerState(int x, int y, boolean hasPotion, Set<Point> remainingPotions, Set<Point> remainingCreatures) {
        this.x = x;
        this.y = y;
        this.hasPotion = hasPotion;
        this.remainingPotions = remainingPotions;
        this.remainingCreatures = remainingCreatures;
    }   
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TowerState)) return false;
        TowerState other = (TowerState) obj;
        return x == other.x && 
               y == other.y && 
               hasPotion == other.hasPotion &&
               remainingPotions.equals(other.remainingPotions) &&
               remainingCreatures.equals(other.remainingCreatures);
    }   
    @Override
    public int hashCode() {
        return Objects.hash(x, y, hasPotion, remainingPotions, remainingCreatures);
    }   
    @Override
    public String toString() {
        return String.format("State(x=%d, y=%d, hasPotion=%b, remainingPotions=%d, remainingCreatures=%d)",
                x, y, hasPotion, remainingPotions.size(), remainingCreatures.size());
    }
}