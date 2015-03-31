package SkyNet;

import SkyNet.model.*;
import SkyNet.Strategy.*;
import SkyNet.Heuristic.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;


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
            return ( runtime.totalMemory() - runtime.freeMemory() ) / mb;
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
            return ( used() / max() > limitRatio );
        }

        public static String stringRep() {
            return String.format( "[Used: %.2f MB, Free: %.2f MB, Alloc: %.2f MB, MaxAlloc: %.2f MB]", used(), free(), total(), max() );
        }
    }


    public Level level;
    public Node initialState = null;



    public void pickGoal() {
        Goal g = new Goal(5, 6);
        Box b = new Box(1, 3);
        extractPartialOrderPlan(g, b);
    }

    private ArrayList<Action> extractPartialOrderPlan(Goal goal, Box box) {
        ArrayList<Action> goalSteps = new ArrayList<Action>();

        Strategy strategy = new StrategyBestFirst(new AStar(initialState));




        return goalSteps;
    }




    public LinkedList< Node > Search( Strategy strategy ) throws IOException {
        System.err.format( "Search starting with strategy %s\n", strategy );
        strategy.addToFrontier( this.initialState );

        int iterations = 0;
        while ( true ) {
//            if ( iterations % 200 == 0 ) {
//                System.err.println( strategy.searchStatus() );
//            }
            if ( Memory.shouldEnd() ) {
                System.err.format( "Memory limit almost reached, terminating search %s\n", Memory.stringRep() );
                return null;
            }
            if ( strategy.timeSpent() > 300 ) { // Minutes timeout
                System.err.format( "Time limit reached, terminating search %s\n", Memory.stringRep() );
                return null;
            }

            if ( strategy.frontierIsEmpty() ) {
                return null;
            }

            Node leafNode = strategy.getAndRemoveLeaf();

            if ( leafNode.isGoalState() ) {
                return null;
//                return leafNode.extractPlan();
            }

            strategy.addToExplored( leafNode );

//            for ( Node n : leafNode.getExpandedNodes() ) {
//                if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
//                    strategy.addToFrontier( n );
//                }
//            }
            iterations++;
        }
    }

}
