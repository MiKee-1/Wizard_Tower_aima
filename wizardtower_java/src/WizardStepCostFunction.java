import aima.search.framework.StepCostFunction;

public class WizardStepCostFunction implements StepCostFunction {
    private Tower tower;

    public WizardStepCostFunction(Tower tower) {
        this.tower = tower;
    }

    public Double calculateStepCost(Object fromState, Object toState, String action) {
        return tower.getStepCost(
            (TowerState) fromState,
            new WizardAction(action),
            (TowerState) toState
        );
    }
}