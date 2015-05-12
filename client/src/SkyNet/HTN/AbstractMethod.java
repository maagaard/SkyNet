package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.Genetic.Gene;
import SkyNet.model.Level;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractMethod extends Gene {
    final List<AbstractMethod> reduction;
    final int score;

    public AbstractMethod(int initialScore, List<AbstractMethod> reduction){
        this.reduction = reduction;
        this.score = initialScore + this.score(reduction);
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
            super(0, reduction);
        }

        @Override
        public int score(List<AbstractMethod> reduction) {
            return 0;
        }

        @Override
        public List<AbstractMethod> Decompose(Level lvl) {
            return null;
        }

        @Override
        public int distanceTo(Gene other) {
            return 0;
        }
    }

    public class Action extends AbstractMethod {
        private final Command cmd;
        public Action(List<AbstractMethod> reduction, Command cmd) {
            super(0, reduction);
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

        @Override
        public int distanceTo(Gene other) {
            return 0;
        }
    }
}
