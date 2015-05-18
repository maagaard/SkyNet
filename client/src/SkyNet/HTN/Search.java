package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.Genetic.GeneticAlgorithm;
import SkyNet.Genetic.PopulationMember;
import SkyNet.model.Agent;
import SkyNet.model.Box;
import SkyNet.model.Cell;
import SkyNet.model.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.reverse;

public class Search {
    final HTNUtils utils;

    public Search(){
        this.utils = new Utils();
    }

    private List<SubGoal> calcOptimalSubGoalOrder(Level lvl){
        //single agent for now
        Agent agent = lvl.agents.get(0);

        Stream<Stream<SubGoal>> sgss = lvl.goals.stream().map(g ->
                lvl.boxes.stream()
                        .filter(b -> b.name == g.name - 32)
                        .map(b -> new SubGoal(b, g))
                        .sorted(SubGoal::compareTo));

        PopulationMember bestSubGoals =
                GeneticAlgorithm.GenerateSolution(agent.x, agent.y,
                        sgss.map(sgs -> sgs.findFirst().get()).collect(Collectors.toList()));

        return bestSubGoals.genes.stream()
          .map(g -> (SubGoal) g)
          .collect(Collectors.toList());
    }

    public List<Command> completeLevel(Level lvl){
        //single agent for now...
        Agent agent = lvl.agents.get(0);

        List<SubGoal> subGoals = calcOptimalSubGoalOrder(lvl);

        //the first goal is to move the agent to the first box
        Box firstgoal = subGoals.get(0).box;
        List<Cell> agentMovePAth = this.utils.findAgentMovePAth(
                new Cell(agent.x, agent.y),
                new Cell(firstgoal.x,  firstgoal.y),
                lvl);

        List<Command> movePath = agentMovePAth.stream()
                .map(x -> new Command(x.direction))
                .collect(Collectors.toList());
        //reverse(movePath);

        Cell lastPos = null;
        if(agentMovePAth.size() > 0)
            lastPos = agentMovePAth.get(agentMovePAth.size()-1);
        agent =
                (lastPos != null)
                ? new Agent(agent.number, lastPos.x, lastPos.y)
                : agent;

        List<Command> commands = new LinkedList<>();
        for(SubGoal sg : subGoals) {
            List<Command> solvedSg = solveSubgoal(sg, agent, lvl);
            reverse(solvedSg);
            commands.addAll(solvedSg);
        }
        //reverse(commands);
        movePath.addAll(commands);
        return movePath;
    }

    private List<Command> solveSubgoal(SubGoal sg, final Agent agent, Level lvl){
        Level lvlWithOneGoal = new Level(lvl, lvl.boxes, Arrays.asList(sg.goal));
        return extractPathFromNode(this.utils.accomplishLevel(agent, lvlWithOneGoal));
    }

    private List<Command> extractPathFromCell(Cell c){
        LinkedList<Command> path = new LinkedList<Command>();
        while(c.parent != null) {
            path.add(new Command(c.direction));
            c = c.parent;
        }
        return path;
    }

    private <T> List<T> addAllToList(List<T> xs, List<T> ys){
        xs.addAll(ys);
        return xs;
    }

    private List<Command> extractPathFromNode(Node n){
        LinkedList<Command> path = new LinkedList<Command>();
        while(n.parent != null) {
            path.add(n.command);
            n = n.parent;
        }
        return path;
    }
}
