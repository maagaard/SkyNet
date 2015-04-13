package SkyNet;

import SkyNet.model.*;
import SkyNet.Strategy.*;
//import SkyNet.PartialStrategy.*;
//import SkyNet.PartialPlanNode;
//import SkyNet.PartialPlanHeuristic.*;
import SkyNet.Heuristic.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class POP {

    public static class Memory {
        public static Runtime runtime = Runtime.getRuntime();
        public static final float mb = 1024 * 1024;
        public static final float limitRatio = .9f;
        public static final int timeLimit = 180;

        public static float used() {
            return (runtime.totalMemory() - runtime.freeMemory()) / mb;
        }

        public static float free() {
            return runtime.freeMemory() / mb;
        }

        public static float total() {
            return runtime.totalMemory() / mb;
        }

        public static float max() {
            return runtime.maxMemory() / mb;
        }

        public static boolean shouldEnd() {
            return (used() / max() > limitRatio);
        }

        public static String stringRep() {
            return String.format("[Used: %.2f MB, Free: %.2f MB, Alloc: %.2f MB, MaxAlloc: %.2f MB]", used(), free(), total(), max());
        }
    }

    public BufferedReader serverMessages;
    public Level level;
    public ArrayList<Box> boxes = new ArrayList<Box>();
    public ArrayList<Agent> agents = new ArrayList<Agent>();
    public Node initialState = null;
    public PathFragment initialPath = null;
    public Strategy strategy = null;

    public static void error(String msg) throws Exception {
        throw new Exception("GSCError: " + msg);
    }

    public POP(BufferedReader serverMessages) throws Exception {
        this.serverMessages = serverMessages;

        Map<Character, String> colors = new HashMap<Character, String>();
        String line, color = "";

        int agentCol = -1, agentRow = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ((line = serverMessages.readLine()).matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
            line = line.replaceAll("\\s", "");
            String[] colonSplit = line.split(":");
            color = colonSplit[0].trim();

            for (String id : colonSplit[1].split(",")) {
                colors.put(id.trim().charAt(0), color);
            }
            colorLines++;
        }

        if (colorLines > 0) {
            error("Box colors not supported");
        }

        int MAX_SIZE = 100;

        boolean[][] walls = new boolean[MAX_SIZE][MAX_SIZE];
        char[][] nodeBoxes = new char[MAX_SIZE][MAX_SIZE];
        char[][] nodeGoals = new char[MAX_SIZE][MAX_SIZE];
        int longestLine = 0;

        ArrayList<Goal> goals = new ArrayList<Goal>();

        int initialAgentRow = 0;
        int initialAgentCol = 0;

        while (!line.equals("")) {
            int length = line.length();
            if (length > longestLine) longestLine = length;

            for (int i = 0; i < length; i++) {
                char chr = line.charAt(i);
                if ('+' == chr) { // Walls
                    walls[levelLines][i] = true;
                } else if ('0' <= chr && chr <= '9') { // Agents
                    if (agentCol != -1 || agentRow != -1) {
                        error("Not a single agent level");
                    }
                    agents.add(new Agent(chr, i, levelLines));
                    initialAgentRow = levelLines;
                    initialAgentCol = i;
                } else if ('A' <= chr && chr <= 'Z') { // Boxes
                    nodeBoxes[levelLines][i] = chr;
                    boxes.add(new Box(chr, i, levelLines));
                } else if ('a' <= chr && chr <= 'z') { // Goal cells
                    nodeGoals[levelLines][i] = chr;
                    goals.add(new Goal(chr, i, levelLines));

                }
            }
            line = serverMessages.readLine();
            levelLines++;
        }


        level = new Level();
        level.walls = walls;
        level.goals = goals;
//        this.initialState.level = level;


        //Initialize new agent
        initialState = new Node(null, levelLines + 1, longestLine + 1);

        System.err.format("Column size: %d, row size: %d\n", Node.MAX_COLUMN, Node.MAX_ROW);
        //Copy walls, boxes and goals into smaller array
        for (int i = 0; i < Node.MAX_ROW; ++i) {
            for (int j = 0; j < Node.MAX_COLUMN; j++) {
                initialState.walls[i][j] = walls[i][j];
                initialState.goals[i][j] = nodeGoals[i][j];
                initialState.boxes[i][j] = nodeBoxes[i][j];
            }
        }

        initialState.agentCol = initialAgentCol;
        initialState.agentRow = initialAgentRow;
    }

    public void pickGoal() throws IOException {

        Goal g = level.goals.get(0);

        Box box = null;
        for (Box b : boxes) {
            if (Character.toLowerCase(g.name) == Character.toLowerCase(b.name)) {
                box = b;
                break;
            }
        }

        Agent agent = agents.get(0);

        System.err.println("Agent: " + agent.number + ", goal: " + g.name + ", box: " + box.name);

        LinkedList<Node> solution = extractPartialOrderPlan(agent, g, box);

        if (solution == null) {
            System.err.println("Unable to solve level");
            System.exit(0);
        } else {
            System.err.println("\nSummary for " + strategy);
            System.err.println("Found solution of length " + solution.size());
            System.err.println(strategy.searchStatus());

            for (Node n : solution) {
                String act = n.action.toActionString();
                System.out.println(act);
                String response = serverMessages.readLine();
                if (response.contains("false")) {
                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                    System.err.format("%s was attempted in \n%s\n", act, n);
                    break;
                }
            }

        }


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


    }

    private LinkedList<Node> extractPartialOrderPlan(Agent agent, Goal goal, Box box) {

//        PartialPlanNode partialInitialState = new PartialPlanNode(level, agent, goal, box);
//        partialInitialState.path.add(new PathFragment(agent, box, goal, null, 0));

        strategy = new StrategyBestFirst(new AStar(this.initialState));

        LinkedList<Node> partialPlan = null;

        try {
            partialPlan = PartialSearch(strategy);
            if (partialPlan == null) return null;
            System.err.format("Search starting with strategy %s\n", strategy);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.format("Error");
        }

        return partialPlan;
    }


    public LinkedList<Node> PartialSearch(Strategy strategy) throws IOException {
        System.err.format("Search starting with strategy %s\n", strategy);
//        strategy.addToFrontier(partialNode.path.get(0));
        strategy.addToFrontier(this.initialState);

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
