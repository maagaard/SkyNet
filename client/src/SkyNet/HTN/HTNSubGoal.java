package SkyNet.HTN;

import SkyNet.Genetic.Gene;
import SkyNet.model.Box;
import SkyNet.model.Cell;
import SkyNet.model.Goal;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michel on 5/19/15.
 */

public class HTNSubGoal extends Gene {
    public final Box box;
    public final Goal goal;
    private final List<Box> obstacles;
    private MovePathGenerator movePathGenerator;

    public HTNSubGoal(Box box, Goal goal, List<Box> obstacles) {
        super();
        this.box = box;
        this.goal = goal;
        this.obstacles = new LinkedList<>(obstacles);
        this.movePathGenerator = MovePathGenerator.getInstance();
    }

    @Override
    public int distanceTo(Gene other) {
        HTNSubGoal otherSubGoal = (HTNSubGoal) other;
        List<Box> obstacles = this.obstacles.stream()
                .filter(b -> !(b.x == otherSubGoal.box.x &&
                        b.y == otherSubGoal.box.y
                        && b.name == otherSubGoal.box.name) &&
                        !(b.x == this.box.x &&
                        b.y == this.box.y &&
                        b.name == this.box.name))
                .collect(Collectors.toList());
        List<Cell> agentMovePAth = this.movePathGenerator.findAgentMovePAth(
                new Cell(this.goal.x, this.goal.y),
                new Cell(otherSubGoal.box.x, otherSubGoal.box.y),
                obstacles);
        if (agentMovePAth.get(0).success)
            return agentMovePAth.size();
        else //no path could be found
            return Integer.MAX_VALUE;
    }

    @Override
    public String toString(){
        return this.box + " -> " + this.goal;
    }
}
