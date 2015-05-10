package SkyNet;

import SkyNet.model.*;
import SkyNet.Strategy.*;
//import SkyNet.PartialStrategy.*;
//import SkyNet.PartialPlanNode;
//import SkyNet.PartialPlanHeuristic.*;
import SkyNet.Heuristic.*;

import java.io.IOException;
import java.util.*;


public class POP implements Planner {

    private Strategy strategy;
    private Level level;
    private Node initialState;
    private LinkedList<PartialPlan> partialPlans = null;

    public POP(Strategy strategy) throws Exception {
        this.strategy = strategy;
    }


    private void updateInitialState() {
        //Add all goals and boxes to initial state
        for (int i = 0; i < level.goals.size(); i++) {
            Goal g = level.goals.get(i);
            initialState.goals[g.y][g.x] = g.name;
        }
        for (int i = 0; i < level.boxes.size(); i++) {
            Box b = level.boxes.get(i);
//            initialState.boxes[b.y][b.x] = b.name;
            initialState.boxes[b.y][b.x] = b.id;
        }
    }

    public void updateLevel() {

        for (int row = 0; row < initialState.boxes.length; row++) {
            for (int col = 0; col < initialState.boxes[0].length; col++) {
                if (initialState.boxes[row][col] != 0) {
                    Box box = initialState.level.getBox(initialState.boxes[row][col]);
                    box.x = col;
                    box.y = row;
                }
            }
        }
    }

    @Override
    public Plan createPlan(Level level) {
        this.level = level;
        LinkedList<Node> fullSolution = new LinkedList<>();

        //TODO: Do something else than this?
        Agent agent = level.agents.get(0);


        initialState = new Node(null, level.height, level.width);
        initialState.level = level;
        initialState.walls = level.walls;

        //TODO: Do something else than this?
        initialState.agentCol = agent.x;
        initialState.agentRow = agent.y;


        /** Goal ordering determined by partial plans */
        PriorityQueue<Goal> sortedGoals = sortGoals();

        /** Add all goals and boxes to initial state node */
        updateInitialState();


        /** Full plan */
        for (Goal goal : sortedGoals) {

            LinkedList<LinkedList<Node>> solutionList = new LinkedList<>();

            //TODO: Only solve for one box if there are more - best solution should have been found above in "sortGoals()"
            for (Box box : level.boxes) {
                if (Character.toLowerCase(goal.name) == Character.toLowerCase(box.name)) {
                    System.err.println("Agent: " + agent.number + ", goal: " + goal.name + ", box: " + box.name);
                    solutionList.add(extractPlan(level, agent, goal, box));
                }
            }

            LinkedList<Node> shortest = solutionList.pop();
            for (LinkedList<Node> solution : solutionList) {
                if (solution.size() < shortest.size()) {
                    shortest = solution;
                }
            }

            fullSolution.addAll(shortest);

            //TODO: Update "WORLD" - update the state of the level, and add all new necessary knowledge

            initialState = shortest.getLast();
            goal.solveGoal(initialState.chosenBox);
            initialState.chosenBox.x = goal.x;
            initialState.chosenBox.y = goal.y;

            //TODO: WHOLE LEVEL NEEDS TO BE UPDATED !!!!!!
            updateLevel();
            addConflictingBoxes(partialPlans);

        }

        return new Plan(initialState.extractPlan());
    }



    private PriorityQueue<Goal> sortGoals() {

        //TODO: THIS needs re-working
        Agent agent = level.agents.get(0);

        partialPlans = createPartialPlans(agent);
        addConflictingBoxes(partialPlans);
        PriorityQueue<Goal> sortedGoals = sortConflictingGoals(partialPlans);

        //Print goal order
        System.err.format("Goal order: \n");
        for (Goal g : sortedGoals) {
            System.err.format("" + g.name + ", ");
        }
        System.err.format("\n");

        return sortedGoals;
    }


    private LinkedList<PartialPlan> createPartialPlans(Agent agent) {
        LinkedList<PartialPlan> partialPlans = new LinkedList<>();

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
            partialPlans.add(shortestPlan);
        }
        return partialPlans;
    }

    private LinkedList<Node> extractSubgoalSolution(Level level, Agent agent, Goal goal, Box box) {

        Node state = new Node(null, level.height, level.width);
        state.level = level;

        state.walls = initialState.walls;
        state.agentCol = agent.x;
        state.agentRow = agent.y;
        state.goals[goal.y][goal.x] = goal.name;
        state.boxes[box.y][box.x] = box.id;

        strategy = new StrategyBestFirst(new AStar(state));

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

    private PriorityQueue<Goal> sortConflictingGoals(LinkedList<PartialPlan> partialPlans) {
        PriorityQueue<Goal> sortedGoals = new PriorityQueue<Goal>(11, new Goal('0', 0, 0));

        int longestPlan = 0;
        for (PartialPlan p : partialPlans) {
            if (p.size() > longestPlan) {
                longestPlan = p.size();
            }
        }

        for (PartialPlan partialPlan : partialPlans) {

            ArrayList<Goal> goals = new ArrayList<>(level.goals);

            goals.remove(partialPlan.goal);
            Set<Goal> conflictingGoals = new HashSet<>();

            for (Node node : partialPlan.plan) {

                for (Goal goal : goals) {
                    if (goal.x == node.agentCol && goal.y == node.agentRow) {
                        conflictingGoals.add(goal);
                        System.err.format("Goal: " + goal.name + " conflicting with plan for " + partialPlan.goal.name + "\n");
                    }
                }
            }

            float planSizePriority = partialPlan.size() / (float) longestPlan * 10;
            int goalConflictPriority = conflictingGoals.size() * 20;
            partialPlan.goal.priority = goalConflictPriority + (int) planSizePriority;

            sortedGoals.add(partialPlan.goal);
        }

        return sortedGoals;
    }

    private void addConflictingBoxes(LinkedList<PartialPlan> partialPlans) {
        for (PartialPlan partialPlan : partialPlans) {

            if (partialPlan.goal.isSolved()) {
                continue;
            }

            ArrayList<Box> boxes = new ArrayList<>(level.boxes);
            boxes.remove(partialPlan.box);
            Set<Box> conflictingBoxes = new HashSet<>();

            for (Node node : partialPlan.plan) {
                for (Box box : boxes) {
                    if (box.x == node.agentCol && box.y == node.agentRow) {
                        System.err.println("Box: " + box.name + " interfering with plan for " + partialPlan.box.name);
                        conflictingBoxes.add(box);
                    }
                }
            }
            partialPlan.goal.conflictingBoxes = new HashSet<>(conflictingBoxes);
        }
    }


    private LinkedList<Node> extractPlan(Level level, Agent agent, Goal goal, Box box) {

        //TODO: State should contain all walls, boxes, goals and agents - however,
        //TODO: chosen agent, goal and box should also be known

//        initialState.actingAgent = agent;
        initialState.chosenGoal = goal;
        initialState.chosenBox = box;
//        initialState.agentCol = agent.x;
//        initialState.agentRow = agent.y;


        System.err.format("State: \n%s\n", initialState);

        strategy = new StrategyBestFirst(new AStar(initialState));

        try {
            LinkedList<Node> partialPlan = Search(strategy, initialState);
            if (partialPlan == null) return null;
            System.err.format("Search starting with strategy %s\n", strategy);
            return partialPlan;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.format("Error");
            return null;
        }
    }


    public LinkedList<Node> Search(Strategy strategy, Node state) throws IOException {
        System.err.format("Search starting with strategy %s\n", strategy);
        Heuristic.AStar h = new Heuristic.AStar(initialState);

        strategy.addToFrontier(state);

        int iterations = 0;
        while (true) {
            if (iterations % 1000 == 0) {
                System.err.println(strategy.searchStatus());
            }
            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.timeSpent() > 1200) { // 20 Minutes timeout
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
//                    System.err.println("H: " + h.f(n));
                }
            }

            iterations++;
        }
    }

}
