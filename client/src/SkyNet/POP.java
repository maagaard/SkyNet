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
        initialState.level = level;
        initialState.walls = level.walls;
        initialState.agentCol = agent.x;
        initialState.agentRow = agent.y;

//        this.strategy = new StrategyBestFirst(new AStar(initialState));

        //TODO: determine ordering constraints for goals
        PriorityQueue<Goal> sortedGoals = sortGoals();

        System.err.format("Goal order: \n");
        for (Goal g : sortedGoals) {
            System.err.format("" + g.name + ", ");
        }
        System.err.format("\n");


        //TODO: Create plan based on ordering constraints

        LinkedList<Node> fullSolution = new LinkedList<>();
        LinkedList<Plan> partialPlans = new LinkedList<>();



        //Add all goals and boxes to initial state
        for (int i = 0; i < level.goals.size(); i++) {
            Goal g = level.goals.get(i);
            initialState.goals[g.y][g.x] = g.name;
        }
        for (int i = 0; i < level.boxes.size(); i++) {
            Box b = level.boxes.get(i);
            initialState.boxes[b.y][b.x] = b.name;
        }


        //Create full plan
        for (Goal goal : sortedGoals) {

            LinkedList<LinkedList<Node>> solutionList = new LinkedList<>();

            //TODO: Only solve for one box if there are more - best solution should have been found above in "sortGoals()"
            for (Box box : level.boxes) {
                if (Character.toLowerCase(goal.name) == Character.toLowerCase(box.name)) {
                    System.err.println("Agent: " + agent.number + ", goal: " + goal.name + ", box: " + box.name);
//                    System.err.println("Agent location: " + agent.x + "," + agent.y);

                    LinkedList<Node> solution = extractPlan(level, agent, goal, box);
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

            //TODO: Update "WORLD" - update the state of the level, and add all new necessary knowledge
            //TODO: OR change the
            initialState = shortest.getLast();
            initialState.level = level;
            goal.solveGoal(initialState.chosenBox);

            System.err.println("Solved goal: " + goal);

//            agent.x = endNode.agentCol;
//            agent.y = endNode.agentRow;
        }
//        return resolveConflicts(level, partialPlans);
//        return new Plan(fullSolution);
        return new Plan(initialState.extractPlan());
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
                    LinkedList<Node> solution = extractSubgoalSolution(level, agent, goal, box);
                    solutionList.add(new PartialPlan(agent, goal, box, solution));

                    if (solution == null) {
                        System.err.format("No solution found\n");
                    }
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

        int longestPlan = 0;
        for (PartialPlan p : partialPlans) {
            if (p.size() > longestPlan) {
                longestPlan = p.size();
            }
        }

        for (PartialPlan partialPlan : partialPlans) {
            //TODO: Find partial plan goal - do not check if this goal is in the way for the actions
            //TODO: Or find goals to check for

            ArrayList<Goal> goals = new ArrayList<>(level.goals);
            goals.remove(partialPlan.goal);

            Set<Goal> conflictingGoals = new HashSet<>();

            for (Node node : partialPlan.plan) {
                for (Goal goal : goals) {
                    if (goal.x == node.agentCol && goal.y == node.agentRow) {
                        //TODO: indicate conflict and given cell
//                        partialPlan.priority--;
//                        partialPlan.goal.priority--;

                        conflictingGoals.add(goal);
                        System.err.format("Goal: " + goal.name + " conflicting with plan for " + partialPlan.goal.name + "\n");
                    }
                }
            }

            float planSizePriority = partialPlan.size() / (float)longestPlan * 10;
            int goalConfliftPriority = conflictingGoals.size() * 20;
            partialPlan.goal.priority = goalConfliftPriority + (int)planSizePriority;

            sortedGoals.add(partialPlan.goal);
        }

//        for (Goal g : sortedGoals) {
//            System.err.println("Goal and priority: " + g.name + " - " + g.priority);
//        }

        return sortedGoals;
    }

    private Plan resolveConflicts(Level level, LinkedList<Plan> partialPlans) {


        // Ordering constraints --> Check if any goals interfere with other plans

        for (Plan partialPlan : partialPlans) {
            //TODO: Find partial plan goal - do not check if this goal is in the way for the actions
            //TODO: Or find goals to check for

            ArrayList<Goal> goals = new ArrayList<>(level.goals);

            goals.remove(partialPlan.GetPlan().get(0).chosenGoal);

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



    private LinkedList<Node> extractSubgoalSolution(Level level, Agent agent, Goal goal, Box box) {


        Node state = new Node(null, level.height, level.width);
        state.walls = initialState.walls;
//        state.actingAgent = agent;
//        state.chosenGoal = goal;
//        state.chosenBox = box;
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



    //TODO: Make extraction plan for whole level - based on goal sorting
    private LinkedList<Node> extractPlan(Level level, Agent agent, Goal goal, Box box) {

        //TODO: State should contain all walls, boxes, goals and agents - however,
        //TODO: chosen agent, goal and box should also be known

//        initialState.actingAgent = agent;
        initialState.chosenGoal = goal;
        initialState.chosenBox = box;
//        initialState.agentCol = agent.x;
//        initialState.agentRow = agent.y;

//        System.err.println("Is initial state: " + initialState.isInitialState());
        System.err.format("State: \n%s\n", initialState);

        strategy = new StrategyBestFirst(new AStar(initialState));

        LinkedList<Node> partialPlan = null;

        try {
            partialPlan = PartialSearch(strategy, initialState);
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
