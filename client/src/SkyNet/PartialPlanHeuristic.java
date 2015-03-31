package SkyNet;

import java.util.*;

public abstract class PartialPlanHeuristic implements Comparator<Node> {

    public Node initialState;

    public int goalRow, goalColumn;
    public int boxRow, boxColumn;

    public PartialPlanHeuristic(Node initialState) {
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

    public int compare(Node n1, Node n2) {
        return f(n1) - f(n2);
    }

    public int h(Node n) {
        if (n.getClass() == PartialPlanNode.class) {
            PartialPlanNode partialNode = (PartialPlanNode)n;
            return (Math.abs(partialNode.goal.y - partialNode.agent.y) + (Math.abs(partialNode.goal.x - partialNode.agent.x)));
        } else {
            return 1;
//            return (Math.abs(n.goal.y - n.agent.y) + (Math.abs(n.goal.x - n.agent.x)));
        }


    }

    public abstract int f(Node n);

    public static class AStar extends PartialPlanHeuristic {
        public AStar(Node initialState) {
            super(initialState);
        }

        public int f(Node n) {
//            System.err.println("g: "+n.g()+ " h: "+h(n));
            return n.path.size() + h(n);
        }

        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends PartialPlanHeuristic {
        private int W;

        public WeightedAStar(Node initialState) {
            super(initialState);
            W = 5; // You're welcome to test this out with different values, but for the reporting part you must at least indicate benchmarks for W = 5
        }

        public int f(Node n) {
            return n.path.size() + W * h(n);
        }

        public String toString() {
            return String.format("WA*(%d) evaluation", W);
        }
    }

    public static class Greedy extends PartialPlanHeuristic {

        public Greedy(Node initialState) {
            super(initialState);
        }

        public int f(Node n) {
            return h(n);
        }

        public String toString() {
            return "Greedy evaluation";
        }
    }
}
