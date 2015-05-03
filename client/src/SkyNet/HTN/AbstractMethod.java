package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.Genetic.Gene;
import SkyNet.model.Box;
import SkyNet.model.Goal;
import SkyNet.model.Level;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractMethod {
    final List<AbstractMethod> reduction;
    final int score;

    public AbstractMethod(List<AbstractMethod> reduction){
        this.reduction = reduction;
        this.score = this.score(reduction);
    }
    public AbstractMethod(){
        this.reduction = new LinkedList<>();
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

    public class SubGoal extends AbstractMethod implements Gene {
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

        public int distanceTo(Gene other) {
            SubGoal otherSubGoal = (SubGoal)other;
            return Math.abs(this.goal.x - otherSubGoal.box.x) +
                    Math.abs(this.goal.y - otherSubGoal.box.y);
        }

        public boolean areEqual(SubGoal other) {
            return this.box.x == other.box.x &&
                this.box.y == other.box.y &&
                this.goal.x == other.goal.x &&
                this.goal.y == other.goal.y;
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
