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

    private Strategy strategy;
    private Level level;
    private Node initialState;

    public POP(Strategy strategy) throws Exception {
        this.strategy = strategy;
    }

    @Override
    public Plan createPlan(Level level) {
        this.level = level;

//        initialState.goals[goal.y][goal.x] = goal.name;
//        initialState.boxes[box.y][box.x] = box.name;
//        initialState.walls = level.walls;

        Agent agent = level.agents.get(0);

        initialState = new Node(null, level.height, level.width);
        initialState.walls = level.walls;
        initialState.agentCol = agent.x;
        initialState.agentRow = agent.y;

//        this.strategy = new StrategyBestFirst(new AStar(initialState));

        //TODO: determine ordering constraints for goals
        PriorityQueue<Goal> sortedGoals = sortGoals();


        //TODO: Create plan based on ordering constraints

        LinkedList<Node> fullSolution = new LinkedList<>();
        LinkedList<Plan> partialPlans = new LinkedList<>();

        for (Goal goal : sortedGoals) {

            LinkedList<LinkedList<Node>> solutionList = new LinkedList<>();

            for (Box box : level.boxes) {
                if (Character.toLowerCase(goal.name) == Character.toLowerCase(box.name)) {

                    System.err.println("Agent: " + agent.number + ", goal: " + goal.name + ", box: " + box.name);
                    LinkedList<Node> solution = extractPartialOrderPlan(level, agent, goal, box);
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

            partialPlans.add(new Plan(shortest));
            Node endNode = shortest.getLast();

            agent.x = endNode.agentCol;
            agent.y = endNode.agentRow;

        }


//        return resolveConflicts(level, partialPlans);
        return new Plan(fullSolution);
    }


    private PriorityQueue<Goal> sortGoals() {
//        ArrayList<Goal> sortedGoals = new ArrayList<>();

        PriorityQueue<Goal> sortedGoals = new PriorityQueue<Goal>(11, new Goal('0',0,0));

        Agent agent = level.agents.get(0);

        LinkedList<PartialPlan> partialPlans = new LinkedList<>();

        for (Goal goal : level.goals) {

            LinkedList<PartialPlan> solutionList = new LinkedList<>();

            for (Box box : level.boxes) {
                if (Character.toLowerCase(goal.name) == Character.toLowerCase(box.name)) {

                    System.err.println("Agent: " + agent.number + ", goal: " + goal.name + ", box: " + box.name);
                    LinkedList<Node> solution = extractPartialOrderPlan(level, agent, goal, box);

                    solutionList.add(new PartialPlan(agent, goal, box, solution));
                }
            }

            PartialPlan shortestPlan = solutionList.pop();
            for (PartialPlan plan : solutionList) {
                if (plan.plan.size() < shortestPlan.plan.size()) {
                    shortestPlan = plan;
                }
            }
//            partialPlans.add(new Plan(shortest));
            partialPlans.add(shortestPlan);
        }


        //TODO: Ordering constraints --> Check if any goals interfere with other plans

        for (PartialPlan partialPlan : partialPlans) {
            //TODO: Find partial plan goal - do not check if this goal is in the way for the actions
            //TODO: Or find goals to check for

            ArrayList<Goal> goals = new ArrayList<>(level.goals);
            goals.remove(partialPlan.goal);

            for (Node node : partialPlan.plan) {
                //TODO: Check if agent passes other partial plan goals
                for (Goal goal : goals) {
                    if (goal.x == node.agentCol && goal.y == node.agentRow) {
                        //TODO: indicate conflict and given cell
                        //TODO: find plan that solves goal in conflict - order to happen after iterated plan
//                        partialPlan.priority--;
                        partialPlan.goal.priority--;
                        System.err.format("Plan for: " + partialPlan.goal.name + " conflicting with " + goal.name + "\n");
                    }
                }
            }

            sortedGoals.add(partialPlan.goal);
        }

        return sortedGoals;
    }

    private Plan resolveConflicts(Level level, LinkedList<Plan> partialPlans) {


        // Ordering constraints --> Check if any goals interfere with other plans

        for (Plan partialPlan : partialPlans) {
            //TODO: Find partial plan goal - do not check if this goal is in the way for the actions
            //TODO: Or find goals to check for

            ArrayList<Goal> goals = new ArrayList<>(level.goals);

            goals.remove(partialPlan.GetPlan().get(0).pursuedGoal);

            for (Node node : partialPlan.GetPlan()) {
                //TODO: Check if agent passes other partial plan goals
                for (Goal goal : goals) {
                    if (goal.x == node.agentCol || goal.y == node.agentRow) {
                        //TODO: indicate conflict and given cell
                        //TODO: find plan that solves goal in conflict - order to happen after iterated plan

                    }
                }
            }

//        this.level.goals
        }

        //TODO: Return a merged plan
        return new Plan(new LinkedList<>());
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

    private LinkedList<Node> extractPartialOrderPlan(Level level, Agent agent, Goal goal, Box box) {

//        PartialPlanNode partialInitialState = new PartialPlanNode(level, agent, goal, box);
//        partialInitialState.path.add(new PathFragment(agent, box, goal, null, 0));

//        System.err.format("Initial state length: " + this.initialState.boxes);
//        Node state = new Node(null, level.boxes.size(), level.boxes[0].length);


        Node state = new Node(null, level.height, level.width);   //new Node(null, level.height, level.width);

        state.walls = initialState.walls;
//        state.agentCol = initialState.agentCol;
//        state.agentRow = initialState.agentRow;
        state.agentCol = agent.x;
        state.agentRow = agent.y;

        state.goals[goal.y][goal.x] = goal.name;
        state.boxes[box.y][box.x] = box.name;


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
