package SkyNet;

import SkyNet.model.*;
import SkyNet.Strategy.*;
//import SkyNet.PartialStrategy.*;
//import SkyNet.PartialPlanNode;
//import SkyNet.PartialPlanHeuristic.*;
import SkyNet.Heuristic.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


public class POP implements Planner {

    public POP() throws Exception {
    }

    @Override
    public Plan createPlan(Level level) {

        Agent agent = level.agents.get(0);

        LinkedList<Node> fullSolution = new LinkedList<Node>();

        for (Goal goal : level.goals) {

            LinkedList<LinkedList<Node>> solutionList = new LinkedList<>();
//            Box box = null;
            for (Box box : level.boxes) {
                if (Character.toLowerCase(goal.name) == Character.toLowerCase(box.name)) {
//                    box = b;
                    System.err.println("Agent: " + agent.number + ", goal: " + goal.name + ", box: " + box.name);
                    LinkedList<Node> solution = extractPartialOrderPlan(agent, goal, box);
                    solutionList.add(solution);
                }
            }

            LinkedList<Node> shortest = solutionList.pop();
            for (LinkedList<Node> solution : solutionList) {
                if (solution.size() < shortest.size()) {
                    shortest = solution;
                }
            }

            fullSolution.addAll(shortest);
//            return fullSolution;

            Node endNode = shortest.getLast();

            agent.x = endNode.agentCol;
            agent.y = endNode.agentRow;

        }

        return new Plan(fullSolution);
    }


//    public LinkedList<Node> pickGoal() throws IOException {
//
//        Goal g = level.goals.get(0);
//
//        Box box = null;
//        for (Box b : boxes) {
//            if (Character.toLowerCase(g.name) == Character.toLowerCase(b.name)) {
//                box = b;
//                break;
//            }
//        }
//
//        Agent agent = agents.get(0);
//
//        System.err.println("Agent: " + agent.number + ", goal: " + g.name + ", box: " + box.name);
//
//        LinkedList<Node> solution = extractPartialOrderPlan(agent, g, box);
//
//        if (solution == null) {
//            System.err.println("Unable to solve level");
//            System.exit(0);
//        } else {
//            System.err.println("\nSummary for " + strategy);
//            System.err.println("Found solution of length " + solution.size());
//            System.err.println(strategy.searchStatus());
//
//            for (Node n : solution) {
//                String act = n.action.toActionString();
//                System.out.println(act);
//                String response = serverMessages.readLine();
//                if (response.contains("false")) {
//                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
//                    System.err.format("%s was attempted in \n%s\n", act, n);
//                    break;
//                }
//            }
//
//        }
//
//        return solution;
//

//        if (solution == null) {
//            System.err.println("Unable to solve level");
//            System.exit(0);
//        } else {
////            System.err.println("\nSummary for " + strategy);
//            System.err.println("Found solution of length " + solution.size());
//            System.err.println(strategy.searchStatus());
//
//            if (solution.get(0) == null) {
//                solution.removeFirst();
//            }
////            for (int i = 0; i < 20; i++) {
////                System.err.format("Action: %s\n", solution.get(i));
////            }
//
//            System.err.format("Start\n");
//
//            for (Command c : solution) {
//                if (c == null) { continue; }
//                System.err.format("Action: %s\n", c);
//                String act = c.toActionString();
//                System.out.println(act);
//                String response = this.serverMessages.readLine();
//                if (response.contains("false")) {
//                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
//                    System.err.format("%s was attempted in \n%s\n", act, c);
//                    continue;
////                    break;
//                }
//            }
//            System.err.format("Done\n");
//        }


//    }

    private LinkedList<Node> extractPartialOrderPlan(Agent agent, Goal goal, Box box) {

//        PartialPlanNode partialInitialState = new PartialPlanNode(level, agent, goal, box);
//        partialInitialState.path.add(new PathFragment(agent, box, goal, null, 0));

//        System.err.format("Initial state length: " + this.initialState.boxes);


        Node state = new Node(null, initialState.boxes.length, initialState.boxes[0].length);
        state.goals[goal.y][goal.x] = goal.name;
        state.boxes[box.y][box.x] = box.name;
        state.walls = initialState.walls;
        state.agentCol = agent.x;
        state.agentRow = agent.y;

        strategy = new StrategyBestFirst(new AStar(state));

        LinkedList<Node> partialPlan = null;

        try {
            partialPlan = PartialSearch(strategy, state);
            if (partialPlan == null) return null;
            System.err.format("Search starting with strategy %s\n", strategy);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.format("Error");
        }

        return partialPlan;
    }


    public LinkedList<Node> PartialSearch(Strategy strategy, Node state) throws IOException {
        System.err.format("Search starting with strategy %s\n", strategy);
//        strategy.addToFrontier(partialNode.path.get(0));

        strategy.addToFrontier(state);

        int iterations = 0;
        while (true) {
            if ( iterations % 1000 == 0 ) {
                System.err.println(strategy.searchStatus());
            }
            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.timeSpent() > 300) { // Minutes timeout
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.frontierIsEmpty()) {
                System.err.format("Frontier is empty");
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
