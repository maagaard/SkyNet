package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.Genetic.GeneticAlgorithm;
import SkyNet.Genetic.PopulationMember;
import SkyNet.model.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.reverse;

public class Search {
    final HTNUtils utils;
    MovePathGenerator movePathGenerator;

    private class CommandWithEffects {
        private final Command cmd;
        private final List<Box> boxes;
        private final int x;
        private final int y;

        private CommandWithEffects(Command cmd, List<Box> boxes, int x, int y){
            this.cmd = cmd;
            this.boxes = boxes;
            this.x = x;
            this.y = y;
        }
    }

    public Search(Level lvl){
        this.utils = new Utils();
        this.movePathGenerator = MovePathGenerator.getInstance();
        this.movePathGenerator.initLevel(lvl);
    }

    private List<HTNSubGoal> calcOptimalSubGoalOrder(Level lvl){
        //single agent for now
        Agent agent = lvl.agents.get(0);

        Stream<Stream<HTNSubGoal>> sgss = lvl.goals.stream().map(g ->
                lvl.boxes.stream()
                        .filter(b -> b.name == g.name - 32)
                        .map(b -> new HTNSubGoal(
                                b, g,
                                lvl.boxes)));

        PopulationMember bestSubGoals =
                GeneticAlgorithm.GenerateSolution(agent.x, agent.y,
                        sgss.map(sgs -> sgs.findFirst().get()).collect(Collectors.toList()));

        return bestSubGoals.genes.stream()
          .map(g -> (HTNSubGoal) g)
          .collect(Collectors.toList());
    }

    public List<Command> completeLevel(Level lvl){
        //single agent for now...
        Agent agent = lvl.agents.get(0);

        List<HTNSubGoal> subGoals = calcOptimalSubGoalOrder(lvl);

        //the first goal is to move the agent to the first box
        Box firstgoal = subGoals.get(0).box;
        List<Box> obstacles = lvl.boxes.stream()
                .filter(b -> !(b.x == firstgoal.x && b.y == firstgoal.y))
                .collect(Collectors.toList());

        Cell
                agentCell = new Cell(agent.x, agent.y),
                firstGoalCell = new Cell(firstgoal.x,  firstgoal.y);
        List<Cell> agentMovePath = this.movePathGenerator.findAgentMovePAth(
                agentCell,
                firstGoalCell, new Level(lvl, obstacles));
        agentMovePath.remove(agentMovePath.size()-1);
        List < Command > movePath = agentMovePath.stream()
                .map(x -> new Command(x.direction))
                .collect(Collectors.toList());

        Cell lastPos = null;
        // agentMovePath length greater than zero means that a path exists
        if(agentMovePath.size() > 0)
            lastPos = agentMovePath.get(agentMovePath.size()-1);
        agent =
                (lastPos != null)
                ? new Agent(agent.number, lastPos.x, lastPos.y)
                : agent;

        List<Command> commands = new LinkedList<>();
        lvl = new Level(lvl, lvl.boxes, new LinkedList<>());
        for(HTNSubGoal sg : subGoals) {
            List<CommandWithEffects> solvedSg = solveSubgoal(sg, agent, lvl);
            reverse(solvedSg);
            CommandWithEffects lastCommand = solvedSg.get(solvedSg.size() - 1);
            commands.addAll(solvedSg.stream().map(c -> c.cmd).collect(Collectors.toList()));
            agent = new Agent(agent.number, lastCommand.x, lastCommand.y);
            lvl = new Level(lvl, lastCommand.boxes, sg.goal);
        }
        movePath.addAll(commands);
        return movePath;
    }

    private List<CommandWithEffects> solveSubgoal(HTNSubGoal sg, final Agent agent, Level lvl){
        Node node = this.utils.accomplishLevel(agent, new Level(lvl, sg.goal));
        return extractPathFromNode(node);
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

    private List<CommandWithEffects> extractPathFromNode(Node n){
        LinkedList<CommandWithEffects> path = new LinkedList<>();
        while(n.parent != null) {
            path.add(new CommandWithEffects(n.command, n.level.boxes, n.agent.x, n.agent.y));
            n = n.parent;
        }
        return path;
    }
}
