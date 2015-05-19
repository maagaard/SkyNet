package SkyNet.HTN;

import SkyNet.Genetic.Gene;
import SkyNet.model.Box;
import SkyNet.model.Cell;
import SkyNet.model.Goal;
import SkyNet.model.Level;

import java.util.List;

public class SubGoal extends AbstractMethod implements Comparable {
    public final Box box;
    public final Goal goal;
    private MovePathGenerator movePathGenerator;


    public SubGoal(Box box, Goal goal){
        super();
        this.box = box;
        this.goal = goal;
        this.movePathGenerator = MovePathGenerator.getInstance();
    }

    public SubGoal(int initialScore, List<AbstractMethod> reduction, Box box, Goal goal) {
        super(initialScore, reduction);
        this.box = box;
        this.goal = goal;
    }

    @Override
    public int score(List<AbstractMethod> reduction) {
        return Math.abs(box.x - goal.x) + Math.abs(box.y - goal.y);
    }

    @Override
    public List<AbstractMethod> Decompose(Level lvl) {
        return null;
    }

    @Override
    public int distanceTo(Gene other) {
        SubGoal otherSubGoal = (SubGoal)other;
        List<Cell> agentMovePAth = this.movePathGenerator.findAgentMovePAth(new Cell(this.box.x, this.box.y), new Cell(this.goal.x, this.goal.y));
        return agentMovePAth.size();

//        return Math.abs(this.goal.x - otherSubGoal.box.x) +
//          Math.abs(this.goal.y - otherSubGoal.box.y);
    }

    public boolean areEqual(SubGoal other) {
        return this.box.x == other.box.x &&
          this.box.y == other.box.y &&
          this.goal.x == other.goal.x &&
          this.goal.y == other.goal.y;
    }

    @Override
    public int compareTo(Object o) {
        return this.score - ((SubGoal)o).score;
    }

    @Override
    public String toString(){
        return this.box + " -> " + this.goal;
    }
}

