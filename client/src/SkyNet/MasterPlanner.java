package SkyNet;

import SkyNet.model.*;

import java.io.IOException;
import java.util.*;

/**
 * client
 * Created by maagaard on 10/05/15.
 * Copyright (c) maagaard 2015.
 */
public class MasterPlanner implements Planner {

    private Strategy strategy;
    private Level level;
    private Node initialState;
    private POP partialPlanner;
    private LinkedList<PartialPlan> partialPlans = null;

    public MasterPlanner(Strategy strategy) {
        this.strategy = strategy;
        this.partialPlanner = new POP();
    }

    @Override
    public Plan createPlan(Level level) {
        this.level = level;

        //TODO: Do agent choosing in another way than this?
        Agent agent = level.agents.get(0);

        initialState = new Node(null, level.height, level.width);
        initialState.level = level;
        initialState.walls = level.walls;

        //TODO: Do agent choosing in another way than this?
        initialState.agentCol = agent.x;
        initialState.agentRow = agent.y;


        /** Goal ordering determined by partial plans */
        partialPlans = partialPlanner.createPartialPlans(level);
        PriorityQueue<Goal> sortedGoals = sortGoals();

        updateConflictingBoxes();

        /** Add all goals and boxes to initial state node */
        updateInitialState();

        /** Create full plan */
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

            //TODO: Update "WORLD" - update the state of the level, and add all new necessary knowledge

            initialState = shortest.getLast();
            goal.solveGoal(initialState.chosenBox);
            initialState.chosenBox.x = goal.x;
            initialState.chosenBox.y = goal.y;

            //TODO: WHOLE LEVEL NEEDS TO BE UPDATED !!!!!!
            updateLevel();
            updateConflictingBoxes();

        }

        return new Plan(initialState.extractPlan());
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

    private void updateGoalSorting() {

    }

    private PriorityQueue<Goal> sortGoals() {

        //TODO: THIS agent choosing needs re-working
        Agent agent = level.agents.get(0);

        PriorityQueue<Goal> sortedGoals = sortConflictingGoals(partialPlans);

        //Print goal order
        System.err.format("Goal order: \n");
        for (Goal g : sortedGoals) {
            System.err.format("" + g.name + ", ");
        }
        System.err.format("\n");

        return sortedGoals;
    }


    private PriorityQueue<Goal> sortConflictingGoals(LinkedList<PartialPlan> partialPlans) {
        PriorityQueue<Goal> sortedGoals = new PriorityQueue<Goal>(11, new Goal('0', 0, 0));

        int longestPlan = 0;
        for (PartialPlan p : partialPlans) {
            System.err.println("Plan "+ p.goal.name +" length: "+ p.size());
            if (p.size() > longestPlan) {
                longestPlan = p.size();
            }
        }

        for (PartialPlan partialPlan : partialPlans) {

            ArrayList<Goal> goals = new ArrayList<>(level.goals);

            goals.remove(partialPlan.goal);
            Set<Goal> conflictingGoals = new HashSet<>();

//            System.err.println("________________________________________________");
//            System.err.println(partialPlan.goal);

            for (Node node : partialPlan.plan) {

//                if (partialPlan.goal.name == 'h') {
//                    System.err.println("Agent: " + node.agentCol + "," + node.agentRow);
//                }

                for (Goal goal : goals) {

                    //Goal box movement conflict
                    if (node.boxes[goal.y][goal.x] != 0) {
                        conflictingGoals.add(goal);
                        System.err.format("Goal: " + goal.name + " conflicting with plan for " + partialPlan.goal.name + "\n");
                    }

                    // Goal agent conflict
//                    if (goal.x == node.agentCol && goal.y == node.agentRow) {
//                        conflictingGoals.add(goal);
//                        System.err.format("Goal: " + goal.name + " conflicting with plan for " + partialPlan.goal.name + "\n");
//                    }
                }
            }

//            System.err.println("________________________________________________");

            float planSizePriority = ((float)longestPlan / (float)partialPlan.size()) * 10;
            int goalConflictPriority = conflictingGoals.size() * 10 * level.goals.size();
            partialPlan.goal.priority = goalConflictPriority + (int) planSizePriority;

            System.err.println("Plan "+ partialPlan.goal.name +" size priority: "+ planSizePriority + " conflict size: " + conflictingGoals.size() + " total priority: " + partialPlan.goal.priority);

            sortedGoals.add(partialPlan.goal);
        }

        return sortedGoals;
    }

    private void updateConflictingBoxes() {
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

        strategy = new Strategy.StrategyBestFirst(new Heuristic.AStar(initialState));

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
