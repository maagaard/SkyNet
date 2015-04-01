package SkyNet;

import SkyNet.model.PathFragment;

import java.util.*;

public abstract class PartialPlanHeuristic implements Comparator<PathFragment> {

    public PathFragment initialState;

    public int goalRow, goalColumn;
    public int boxRow, boxColumn;

    public PartialPlanHeuristic(PathFragment initialState) {
        this.initialState = initialState;

//        for (int i = 0; i < partialInitialState.goals.length; i++) {
//            for (int j = 0; j < partialInitialState.goals[i].length; j++) {
//                if (partialInitialState.goals[i][j] != 0) {
//                    goalRow = i;
//                    goalColumn = j;
//                }
//            }
//        }
//        for (int i = 0; i < partialInitialState.boxes.length; i++) {
//            for (int j = 0; j < partialInitialState.boxes[i].length; j++) {
//                if (partialInitialState.boxes[i][j] != 0) {
//                    boxMap.put(Character.toLowerCase(partialInitialState.boxes[i][j]), new int[]{i, j});
//                }
//            }
//        }
    }

    public int compare(PathFragment n1, PathFragment n2) {
        return f(n1) - f(n2);
    }

    public int h(PathFragment n) {
        int agentBoxDist = (Math.abs(n.boxLocation.y - n.agentLocation.y) + (Math.abs(n.boxLocation.x - n.agentLocation.x)));
        int boxGoalDist = (Math.abs(n.goalLocation.y - n.boxLocation.y) + (Math.abs(n.goalLocation.x - n.boxLocation.x)));

        return agentBoxDist + boxGoalDist;

//        if (n.getClass() == PartialPlanPathFragment.class) {
//            PartialPlanPathFragment partialPathFragment = (PartialPlanPathFragment)n;
//            return (Math.abs(partialPathFragment.goal.y - partialPathFragment.agent.y) + (Math.abs(partialPathFragment.goal.x - partialPathFragment.agent.x)));
//        } else {
//            return 1;
////            return (Math.abs(n.goal.y - n.agent.y) + (Math.abs(n.goal.x - n.agent.x)));
//        }
    }

    public abstract int f(PathFragment n);

    public static class AStar extends PartialPlanHeuristic {
        public AStar(PathFragment initialState) {
            super(initialState);
        }

        public int f(PathFragment n) {
//            System.err.println("g: "+n.g()+ " h: "+h(n));
            return n.pathLength + h(n);
        }

        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends PartialPlanHeuristic {
        private int W;

        public WeightedAStar(PathFragment initialState) {
            super(initialState);
            W = 5; // You're welcome to test this out with different values, but for the reporting part you must at least indicate benchmarks for W = 5
        }

        public int f(PathFragment n) {
            return n.pathLength + W * h(n);
        }

        public String toString() {
            return String.format("WA*(%d) evaluation", W);
        }
    }

    public static class Greedy extends PartialPlanHeuristic {

        public Greedy(PathFragment initialState) {
            super(initialState);
        }

        public int f(PathFragment n) {
            return h(n);
        }

        public String toString() {
            return "Greedy evaluation";
        }
    }
}
