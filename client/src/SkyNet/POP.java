package SkyNet;

import SkyNet.model.*;
import SkyNet.Strategy.*;
//import SkyNet.PartialStrategy.*;
//import SkyNet.PartialPlanNode;
//import SkyNet.PartialPlanHeuristic.*;
import SkyNet.Heuristic.*;

import java.io.IOException;
import java.util.*;


public class POP {//} implements Planner {

    public POP() {
    }

    public LinkedList<PartialPlan> createPartialPlans(Level level) {
        LinkedList<PartialPlan> partialPlans = new LinkedList<>();

        //TODO: Update to handle multiple agents
        Agent agent = level.agents.get(0);

        for (Goal goal : level.goals) {
            LinkedList<PartialPlan> solutionList = new LinkedList<>();

            for (Box box : level.boxes) {
                if (Character.toLowerCase(goal.name) == Character.toLowerCase(box.name)) {
                    System.err.println("Agent: " + agent.number + ", goal: " + goal.name + ", box: " + box.name);

                    LinkedList<Node> solution = extractSubgoalSolution(level, agent, goal, box);
                    solutionList.add(new PartialPlan(agent, goal, box, solution));

                    if (solution == null) {
                        System.err.format("No solution found\n");
                    } else if (solution.size() == 0) {
                        System.err.println("Solution of length 0 is wrong");
                    }
                }
            }

            PartialPlan shortestPlan = solutionList.pop();
            for (PartialPlan plan : solutionList) {
                if (plan.plan.size() < shortestPlan.plan.size()) {
                    shortestPlan = plan;
                }
            }

            //TODO: Set suggested box to solve goal
            shortestPlan.goal.suggestedBox = shortestPlan.box;

            //TODO: See if any goals uses the same box for shortest solution - and solve problem?



            partialPlans.add(shortestPlan);
        }
        return partialPlans;
    }

    private LinkedList<Node> extractSubgoalSolution(Level level, Agent agent, Goal goal, Box box) {

        Node state = new Node(null, level.height, level.width);
        state.level = level;

        state.walls = level.walls;
        state.agentCol = agent.x;
        state.agentRow = agent.y;
        state.goals[goal.y][goal.x] = goal.id;
        state.boxes[box.y][box.x] = box.id;

        Strategy strategy = new StrategyBestFirst(new AStar(state));

        try {
            LinkedList<Node> partialPlan = PartialSearch(strategy, state);
            if (partialPlan == null) return null;
            System.err.format("Search starting with strategy %s\n", strategy);
            return partialPlan;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.format("Error");
            return null;
        }
    }

    public LinkedList<Node> PartialSearch(Strategy strategy, Node state) throws IOException {
        System.err.format("Search starting with strategy %s\n", strategy);

        strategy.addToFrontier(state);

        int iterations = 0;
        while (true) {
            if (iterations % 1000 == 0) { System.err.println(strategy.searchStatus()); }
            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.timeSpent() > 600) { // Minutes timeout
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.frontierIsEmpty()) {
                System.err.format("Frontier is empty\n");
                return null;
            }

            Node leafNode = strategy.getAndRemoveLeaf();

            if (leafNode.isGoalState()) {
                System.err.format("Goal state reached\n");
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode);
            for (Node n : leafNode.getExpandedNodes()) {
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }

            iterations++;
        }
    }

}
