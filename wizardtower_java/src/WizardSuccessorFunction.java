import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Successor;

public class WizardSuccessorFunction implements SuccessorFunction {
    private Tower tower;

    public WizardSuccessorFunction(Tower tower) {
        this.tower = tower;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getSuccessors(Object state) {
        List successors = new ArrayList();
        TowerState currentState = (TowerState) state;
        List<WizardAction> actions = tower.getActions(currentState);
        
        for (WizardAction action : actions) {
            TowerState newState = tower.getResult(currentState, action);
            successors.add(new Successor(action.toString(), newState));
        }
        
        return successors;
    }
}