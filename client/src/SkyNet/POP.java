package SkyNet;

import SkyNet.model.*;
//import SkyNet.Strategy.*;
import SkyNet.PartialStrategy.*;
import SkyNet.PartialPlanNode;
import SkyNet.PartialPlanHeuristic.*;

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

    public Level level;
    public ArrayList<Box> boxes = new ArrayList<Box>();
    public ArrayList<Agent> agents = new ArrayList<Agent>();
    public Node initialState = null;
    public PathFragment initialPath = null;

    public static void error(String msg) throws Exception {
        throw new Exception("GSCError: " + msg);
    }

    public POP(BufferedReader serverMessages) throws Exception {

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
//        char[][] boxes = new char[MAX_SIZE][MAX_SIZE];
//        char[][] goals = new char[MAX_SIZE][MAX_SIZE];
        int longestLine = 0;

        ArrayList<Goal> goals = new ArrayList<Goal>();

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

                } else if ('A' <= chr && chr <= 'Z') { // Boxes
//                    boxes[levelLines][i] = chr;
                    boxes.add(new Box(chr, i, levelLines));
                } else if ('a' <= chr && chr <= 'z') { // Goal cells
//                    goals[levelLines][i] = chr;
                    goals.add(new Goal(chr, i, levelLines));

                }
            }
            line = serverMessages.readLine();
            levelLines++;
        }


        level = new Level();
        level.walls = walls;
        level.goals = goals;

    }

    public void pickGoal() {

        Goal g = level.goals.get(0);

        Box box = null;
        for (Box b : boxes) {
            if (Character.toLowerCase(g.name) == Character.toLowerCase(b.name)) {
                box = b;
                break;
            }
        }

        Agent agent = agents.get(0);
        extractPartialOrderPlan(agent, g, box);
    }

    private ArrayList<Action> extractPartialOrderPlan(Agent agent, Goal goal, Box box) {

        PartialPlanNode partialInitialState = new PartialPlanNode(level, agent, goal, box);

        ArrayList<Action> goalSteps = new ArrayList<Action>();

        partialInitialState.path.add(new PathFragment(agent, box, goal, 1));
        PartialStrategy strategy = new StrategyBestFirst(new AStar(partialInitialState.path.get(0)), level);

        LinkedList<PartialPlanNode> partialPlan;
        try {
            partialPlan = PartialSearch(strategy, partialInitialState);
            System.err.format("Search starting with strategy %d\n", partialPlan.get(0).path.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return goalSteps;
    }


    public LinkedList<PartialPlanNode> PartialSearch(PartialStrategy strategy, PartialPlanNode partialNode) throws IOException {
        System.err.format("Search starting with strategy %s\n", strategy);
        strategy.addToFrontier(partialNode.path.get(0));

        int iterations = 0;
        while (true) {
//            if ( iterations % 200 == 0 ) {
//                System.err.println( strategy.searchStatus() );
//            }
            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.timeSpent() > 300) { // Minutes timeout
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.frontierIsEmpty()) {
                return null;
            }

            PathFragment leafPath = strategy.getAndRemoveLeaf();
//            PartialPlanNode leafNode = (PartialPlanNode) strategy.getAndRemoveLeaf();

            if (partialNode.isGoalState(leafPath)) {
                return partialNode.extractPartialPlan(leafPath);
            }

            strategy.addToExplored(leafPath);

            for (PathFragment p : partialNode.getExpandedPaths()) {
                if (!strategy.isExplored(p) && !strategy.inFrontier(p)) {
                    strategy.addToFrontier(p);
                }
            }
//            for ( Node n : leafNode.getExpandedNodes() ) {
//                if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
//                    strategy.addToFrontier( n );
//                }
//            }
            iterations++;
        }
    }

}
