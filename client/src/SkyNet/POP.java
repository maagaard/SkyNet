package SkyNet;

import SkyNet.model.*;
//import SkyNet.Strategy.*;
import SkyNet.PartialStrategy.*;
import SkyNet.PartialPlanNode;
import SkyNet.PartialPlanHeuristic.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;


//class Strategy {
//    public String searchStatus() {
////        return String.format( "#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", countExplored(), countFrontier(), timeSpent(), Memory.stringRep() );
//        return "dummy";
//    }
//
//    public float timeSpent() {
////        return ( System.currentTimeMillis() - startTime ) / 1000f;
//        return 0.0f;
//    }
//
//    public HashSet< Node > explored;
//    public void addToExplored( Node n ) {
//        explored.add( n );
//    }
//
//    private PriorityQueue<Node> frontier;
//
//    public Node getAndRemoveLeaf() {
//        Node node = frontier.poll();
//        return node;
//    }
//
//    public void addToFrontier( Node n ) {
//        frontier.add(n);
//    }
//
//    public int countFrontier() {
//        return frontier.size();
//    }
//
//    public boolean frontierIsEmpty() {
//        return frontier.isEmpty();
//    }
//
//    public boolean inFrontier( Node n ) {
//        return frontier.contains(n);
//    }
//
//    public String toString() {
//        return "Best-first Search (PriorityQueue) using ";
//    }
//}

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
    public Node initialState = null;


    public void pickGoal() {
        Goal g = new Goal('a', 5, 6);
        Box b = new Box('A', 1, 3);
        Agent agent = new Agent(0, 1, 4);

        extractPartialOrderPlan(agent, g, b);
    }

    private ArrayList<Action> extractPartialOrderPlan(Agent agent, Goal goal, Box box) {

        PartialPlanNode partialInitialState = new PartialPlanNode(level, agent, goal, box);

        ArrayList<Action> goalSteps = new ArrayList<Action>();

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
        strategy.addToFrontier(this.initialState.path.get(0));

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
