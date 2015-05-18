package SkyNet;

import SkyNet.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * client
 * Created by maagaard on 10/05/15.
 * Copyright (c) maagaard 2015.
 */
public class MasterPlanner implements Planner {

    private Strategy strategy;
    private Level level;
    private Node currentState;

    private Node lastSolvedGoalState;
    private Box currentSolutionBox;

    private POP partialPlanner;
    private LinkedList<PartialPlan> partialPlans = null;
    private ArrayList<Goal> sortedGoals = new ArrayList<>();
    private int solvedGoalCount = 0;

    public boolean backtracking = false;
    public int recursionCount = 0;

    private BufferedReader serverMessages;

    public MasterPlanner(BufferedReader serverMessages) {
//        this.strategy = strategy;
        this.partialPlanner = new POP();
        this.serverMessages = serverMessages;
    }

    @Override
    public Plan createPlan(Level level) {
        this.level = level;

        //TODO: Do agent choosing in another way than this?
        Agent agent = level.agents.get(0);

        currentState = new Node(null, level.height, level.width);
        currentState.level = level;
        currentState.walls = level.walls;

        //TODO: Do agent choosing in another way than this?
        currentState.agentCol = agent.x;
        currentState.agentRow = agent.y;


        /** Add all goals and boxes to initial state node */
        updateInitialState();

        this.strategy = new Strategy.StrategyBestFirst(new Heuristic.AStar(currentState));

//        int size = 0;
//        for (Goal g : sortedGoals) {
//            size += g.optimalSolutionLength;
//        }
//        System.err.println(size);
//        System.exit(0);


        /** Create full plan */
        for (solvedGoalCount = 0; solvedGoalCount < level.goals.size(); solvedGoalCount++) {

            /** Goal ordering determined by partial plans */
            partialPlans = partialPlanner.createPartialPlans(level);
            sortedGoals = sortGoals();
            System.err.println("Goals: " + sortedGoals.size());
//            updateConflictingBoxes();

            if (sortedGoals.size() == 0) {break;}

            Goal goal = sortedGoals.get(0); // get(solvedGoalCount);

            ArrayList<Box> matchingBoxes = level.getMatchingBoxesForGoal(goal);

            LinkedList<LinkedList<Node>> solutionList = new LinkedList<>();

            for (Box box : matchingBoxes) {
                LinkedList<Node> solution = solveGoalWithBox(null, agent, goal, box);
                solutionList.add(solution);
                int subSolutionLength = solution.size() - currentState.g();

                System.err.println("Found solution: " + subSolutionLength);
                System.err.println("Optimal solution: " + goal.optimalSolutionLength);

                // Check if solution is close to the admissible result - if yes just go with it?
                if (subSolutionLength <= (goal.optimalSolutionLength + 20)) {
                    break;
                } else {
                    System.err.println("Trying next box. " + matchingBoxes.size() + " boxes left");
                }
            }


            if (solutionList.getFirst() == null) {
                // No solutions found - new plan
                //TODO: Move to for loop ??? and FIX THIS !!!
            }

            //Find the shortest of the proposed solutions
            LinkedList<Node> shortest = solutionList.pop();
            for (LinkedList<Node> solution : solutionList) {
                if (solution.size() < shortest.size()) {
                    shortest = solution;
                }
            }

            //Take shortest solution as current state and continue
            currentState = shortest.getLast();
            lastSolvedGoalState = currentState;

            currentState.level.solveGoalWithBox(goal, currentState.chosenBox); // goal.solveGoal(currentState.chosenBox);
            currentState.chosenBox.x = goal.x;
            currentState.chosenBox.y = goal.y;

            agent.x = currentState.agentCol;
            agent.y = currentState.agentRow;

            //TODO: Goal solved. Execute
            try {
                LevelWriter.ExecutePlan(new Plan(currentState.extractPlan()), serverMessages);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //TODO: Update "WORLD" - update the state of the level, and add all new necessary knowledge
            //TODO: WHOLE LEVEL NEEDS TO BE UPDATED !!!!!!

            updateLevel();

//            updateConflictingBoxes();

        }

        return new Plan(currentState.extractPlan());
    }


    private LinkedList<Node> solveGoalWithBox(Strategy strategy, Agent agent, Goal goal, Box box) {
        System.err.println("___________________________________");
        System.err.println("Agent: " + agent.number + ", goal: " + goal.name + ", box: " + box.name + " at " + box.x + "," + box.y);

        LinkedList<Node> partialSolution = extractPlan(strategy, level, agent, goal, box);

        /** Back track from last successful node and solve goal again */
        if (partialSolution == null || partialSolution.size() == 0) {
            System.err.println("Back track");
            //TODO: Something is wrong - back-track
            //TODO: Can we assume, that if frontier is empty, the problem is caused by the last solved goal???
            Node node = currentState.parent;
            node.stupidMoveHeuristics = 300;

            if (solvedGoalCount >= 1) {
                currentState = node;
                solvedGoalCount = sortedGoals.indexOf(currentState.chosenGoal)-1;
                return solveGoalWithBox(this.strategy, agent, currentState.chosenGoal, currentState.chosenBox);
            } else {
                // Backtracked to first goal - skip back-tracking and try another box
                System.err.println("########### I SHOULDN'T BE IN HERE !!!!!! I THINK ############");
                currentState = lastSolvedGoalState;
//                return solveGoalWithBox(this.strategy, agent, sortedGoals.get(solvedGoalCount), box);
            }
        }

        return partialSolution;
    }


    private void updateInitialState() {
        //Add all goals and boxes to initial state
        for (int i = 0; i < level.goals.size(); i++) {
            Goal g = level.goals.get(i);
            currentState.goals[g.y][g.x] = g.id;
        }
        for (int i = 0; i < level.boxes.size(); i++) {
            Box b = level.boxes.get(i);
//            currentState.boxes[b.y][b.x] = b.name;
            currentState.boxes[b.y][b.x] = b.id;
        }
    }

    private void updateLevel() {

        for (int row = 0; row < currentState.boxes.length; row++) {
            for (int col = 0; col < currentState.boxes[0].length; col++) {
                if (currentState.boxes[row][col] != 0) {
                    Box box = currentState.level.getBox(currentState.boxes[row][col]);
                    box.x = col;
                    box.y = row;
                }
            }
        }
    }

    private Node updateCurrentState() {
        Node newState = new Node(null, level.height, level.width);
        newState.level = level;

        for (int row = 0; row < currentState.boxes.length; row++) {
            for (int col = 0; col < currentState.boxes[0].length; col++) {
                if (currentState.boxes[row][col] != 0) {
                    Box box = currentState.level.getBox(currentState.boxes[row][col]);
                    box.x = col;
                    box.y = row;
                }
            }
        }
//        currentState.level

        return newState;
    }

    private void updateGoalSorting() {
        //TODO: Implement this - at least to test performance

    }

    private ArrayList<Goal> sortGoals() {

        //TODO: THIS agent choosing needs re-working
        Agent agent = level.agents.get(0);

        ArrayList<Goal> sortedGoals = sortConflictingGoals(partialPlans);

        //Print goal order
        System.err.format("Goal order: \n");
        for (Goal g : sortedGoals) {
            System.err.format("" + g.name + ", ");
        }
        System.err.format("\n");

        return sortedGoals;
    }


    private ArrayList<Goal> sortConflictingGoals(LinkedList<PartialPlan> partialPlans) {
//        PriorityQueue<Goal> sortedGoals = new PriorityQueue<Goal>(11, new Goal('0', 0, 0));
        ArrayList<Goal> sortedGoals = new ArrayList<>();

        int longestPlan = 0;
        for (PartialPlan p : partialPlans) {
            System.err.println("Plan " + p.goal.name + " length: " + p.size());
            if (p.size() > longestPlan) {
                longestPlan = p.size();
            }
        }

        for (PartialPlan partialPlan : partialPlans) {

            ArrayList<Goal> goals = new ArrayList<>(level.goals);
            goals.remove(partialPlan.goal);

            Set<Goal> conflictGoalBox = new HashSet<>();
            Set<Goal> conflictGoalAgent = new HashSet<>();

            for (Node node : partialPlan.plan) {
                for (Goal goal : goals) {
                    //Goal box movement conflict
                    if (node.boxes[goal.y][goal.x] != 0) {
                        conflictGoalBox.add(goal);
                        goal.conflictingPlans.add(partialPlan.goal);
                        System.err.format("Goal: " + goal.name + " conflicting with plan for " + partialPlan.goal.name + "\n");
                    }
                    // Goal agent conflict
                    if (goal.x == node.agentCol && goal.y == node.agentRow) {
                        conflictGoalAgent.add(goal);
//                        goal.conflictingPlans.add(partialPlan.goal);

                        System.err.format("Goal: " + goal.name + " conflicting with plan for " + partialPlan.goal.name + "\n");
                    }
                }
            }
//            float planSizePriority = (((float) longestPlan / (float) partialPlan.size()) * 10) / (level.goals.size()) * 10;
//            int goalConflictPriority = (conflictGoalBox.size()) * 100 * level.goals.size();
//            partialPlan.goal.priority = goalConflictPriority + (int) planSizePriority;
//            System.err.println("Plan " + partialPlan.goal.name + " size priority: " + planSizePriority + " conflict size: " + conflictGoalBox.size() + " total priority: " + partialPlan.goal.priority);
        }

        for (Goal goal : level.unsolvedGoals) {

            goal.sizePriority = (int)(((float) longestPlan / (float) goal.optimalSolutionLength) * 10) / (level.goals.size()) * 10;
            goal.conflictPriority = (goal.conflictingPlans.size()) * 100 * level.goals.size();

//            int goalConflictPriority = 0;
//            if (goal.conflictingPlans.size() > 0) {
//            } else {
//
//            }
//
//            goal.priority = goalConflictPriority + (int) planSizePriority;

            System.err.println("Plan " + goal.name + " size priority: " + goal.sizePriority + " conflict size: " + goal.conflictingPlans.size() + " total priority: " + (goal.sizePriority + goal.conflictPriority));

            sortedGoals.add(goal);
        }

        Collections.sort(sortedGoals);
        return sortedGoals;
    }


    public void shit(LinkedList<PartialPlan> partialPlans) {

        for (PartialPlan partialPlan : partialPlans) {
            ArrayList<Goal> goals = new ArrayList<>(level.goals);
            goals.remove(partialPlan.goal);

            for (Node node : partialPlan.plan) {

//                int x = node.agentCol;
//                int y = node.agentRow;


                for (Goal goal : goals) {
                    int x = goal.x;
                    int y = goal.y;

                    if (node.boxes[y][x] != 0) {
                        // Horizontal issue
                        if (node.walls[y-1][x] && node.walls[y+1][x] && (node.action.dir1 == Command.dir.E || node.action.dir1 == Command.dir.W)) {

                        }
                        else if (node.walls[y][x+1] && node.walls[y][x+1] && (node.action.dir1 == Command.dir.S || node.action.dir1 == Command.dir.N)) {

                        }



                    }

                }

            }


        }

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
//                        System.err.println("Box: " + box.name + " interfering with plan for " + partialPlan.box.name);
                        conflictingBoxes.add(box);
                    }
                }
            }
            partialPlan.goal.conflictingBoxes = new HashSet<>(conflictingBoxes);
        }
    }


    private LinkedList<Node> extractPlan(Strategy strategy, Level level, Agent agent, Goal goal, Box box) {

        //TODO: State should contain all walls, boxes, goals and agents - however,
        //TODO: chosen agent, goal and box should also be known

//        currentState.actingAgent = agent;
        currentState.chosenGoal = goal;
        currentState.chosenBox = box;
        System.err.println("Goal: " + goal.x + "," + goal.y + " box: " + box.x + "," + box.y);
//        currentState.agentCol = agent.x;
//        currentState.agentRow = agent.y;

        if (strategy == null) {
            this.strategy = new Strategy.StrategyBestFirst(new Heuristic.AStar(currentState));
        }

        try {
            LinkedList<Node> partialPlan = Search(this.strategy, currentState);
            if (partialPlan == null) return null;
//            System.err.format("Search starting with strategy %s\n", this.strategy);
            return partialPlan;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.format("Error");
            return null;
        }
    }


    public LinkedList<Node> Search(Strategy strategy, Node state) throws IOException {
        System.err.format("Search starting with strategy %s\n", strategy);
        Heuristic.AStar h = new Heuristic.AStar(currentState);
        Node lastNode = null;

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

                //TODO: Back Track

                return null;
//                return lastNode.extractPlan();
            }

            Node leafNode = strategy.getAndRemoveLeaf();
            lastNode = leafNode;

            if (leafNode.isGoalState()) {
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode);
            if (leafNode.destroyingGoal != 0) {

            }

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
