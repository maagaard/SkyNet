package SkyNet;

import SkyNet.model.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class NinjaPlanner implements Planner {
    private List<Atom> Atoms;

    public NinjaPlanner(Level level){
        for(Agent agent : level.agents)
            Atoms.add(new AgentAt(agent.number, agent.x, agent.y));
        for(Box box : level.boxes)
            Atoms.add(new BoxAt(box.name, box.x, box.y));
        for(Goal goal : level.goals)
            Atoms.add(new GoalAt(goal.name, goal.x, goal.y));
        /*
        for(Cell cell : level.cells)
            Terms.add(new Term(cell.x + "," + cell.y));
        Atoms.add(new Atom("AgentAt",
                new LinkedList<Term>(Arrays.asList(new Term(), new Term()))));
                */
    }

    @Override
    public Plan createPlan(Level level) {
        //Node initNode = new PartialPlanNode(level, LevelReader)

        return null;
    }
}
