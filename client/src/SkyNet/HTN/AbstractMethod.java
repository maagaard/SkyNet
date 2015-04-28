package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.Genetic.PopulationMemberGene;
import SkyNet.model.Box;
import SkyNet.model.Goal;
import SkyNet.model.Level;
import fj.data.List;
import static fj.data.List.list;

public abstract class AbstractMethod {
    final List<AbstractMethod> reduction;
    final int score;

    public AbstractMethod(List<AbstractMethod> reduction){
        this.reduction = reduction;
        this.score = this.score(reduction);
    }
    public AbstractMethod(){
        this.reduction = list();
        this.score = 0;
    }

    abstract int score(List<AbstractMethod> reduction);
    abstract List<AbstractMethod> Decompose(Level lvl);

    /*
        -- Implementations --
     */

    public class SolveGoals extends AbstractMethod {
        public SolveGoals(List<AbstractMethod> reduction){
            super(reduction);
        }

        @Override
        public int score(List<AbstractMethod> reduction) {
            return 0;
        }

        @Override
        public List<AbstractMethod> Decompose(Level lvl) {
            return null;
        }
    }

    public class SubGoal extends AbstractMethod implements PopulationMemberGene {
        private final Box box;
        private final Goal goal;
        public SubGoal(List<AbstractMethod> reduction, Box box, Goal goal) {
            super(reduction);
            this.box = box;
            this.goal = goal;
        }

        @Override
        public int score(List<AbstractMethod> reduction) {
            return 0;
        }

        @Override
        public List<AbstractMethod> Decompose(Level lvl) {
            return null;
        }

        public int distanceTo(PopulationMemberGene other) {
            SubGoal otherSubGoal = (SubGoal)other;
            return Math.abs(this.goal.x - otherSubGoal.box.x) +
                    Math.abs(this.goal.y - otherSubGoal.box.y);
        }

        @Override
        public boolean areEqual(PopulationMemberGene other) {
            return this.box.x == ((SubGoal)other).box.x &&
                this.box.y == ((SubGoal)other).box.y &&
                this.goal.x == ((SubGoal)other).goal.x &&
                this.goal.y == ((SubGoal)other).goal.y;
        }
    }

    public class Action extends AbstractMethod {
        private final Command cmd;
        public Action(List<AbstractMethod> reduction, Command cmd) {
            super(reduction);
            this.cmd = cmd;
        }

        @Override
        public int score(List<AbstractMethod> reduction) {
            return 0;
        }

        @Override
        public List<AbstractMethod> Decompose(Level lvl) {
            return null;
        }
    }
}
