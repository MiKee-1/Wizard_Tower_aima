import aima.search.framework.GoalTest;

class WizardGoalTest implements GoalTest {
    private Tower tower;

    public WizardGoalTest(Tower tower) {
        this.tower = tower;
    }

    public boolean isGoalState(Object state) {
        return tower.isGoal((TowerState) state);
    }
}