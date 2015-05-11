package SkyNet;

import SkyNet.model.Box;
import SkyNet.model.Goal;
import SkyNet.model.Level;

import java.util.*;

public abstract class Heuristic implements Comparator<Node> {

    public Node initialState;
    public Level level;
//    private Agent actingAgent;
//    private Goal chosenGoal;
//    private Box chosenBox;
    private Map<Goal, Box> solvedGoals;

    public int goalRow, goalColumn;

    public int partialGoalY, partialGoalX, partialBoxY, partialBoxX;


    public Map<Character, int[]> goalMap = new HashMap<Character, int[]>();
    public Map<Character, int[]> boxMap = new HashMap<Character, int[]>();

    public Heuristic(Node initialState) {
        this.initialState = initialState;

        //TODO: Make some of this dynamic !!!!
        if (initialState.chosenGoal != null && initialState.chosenBox != null) {
            solvedGoals = new HashMap<>();
            if (initialState.level != null) {
                for (Goal goal : initialState.level.goals) {
                    if (goal.isSolved()) {
                        solvedGoals.put(goal, goal.getBox());
//                    Box box = initialState.boxes[goal.y][goal.x];
//                    for (Box box : initialState.level.boxes) {
//                    }
                    }
                }
            }
        }


        //TODO: OLD STUFF - MAYBE RE-WRITE TO BE USED AGAIN
        for (int i = 0; i < initialState.goals.length; i++) {
            for (int j = 0; j < initialState.goals[i].length; j++) {
                if (initialState.goals[i][j] != 0) {
//                    Advanced
//                    goalMap.put(Character.toLowerCase(initialState.goals[i][j]), new int[]{i, j});
//                    goalRow = i;
//                    goalColumn = j;

                    partialGoalY = i;
                    partialGoalX = j;
                }
            }
        }
        for (int i = 0; i < initialState.boxes.length; i++) {
            for (int j = 0; j < initialState.boxes[i].length; j++) {
                if (initialState.boxes[i][j] != 0) {
//                    boxMap.put(Character.toLowerCase(initialState.level.getBox(initialState.boxes[i][j]).name), new int[]{i, j});
                    partialBoxY = i;
                    partialBoxX = j;
                }
            }
        }
    }

    public int compare(Node n1, Node n2) {
        return f(n1) - f(n2);
    }

    public int h2(Node n) {
        ArrayList<Integer> combinedDistances = new ArrayList<Integer>();

        Map<Character, Integer> boxDistances = new HashMap<Character, Integer>();
        for (Character c : boxMap.keySet()) {
            double a = Math.pow(n.agentRow - boxMap.get(c)[0], 2);
            double b = Math.pow(n.agentCol - boxMap.get(c)[1], 2);
            int agentBoxDistance = (int) Math.sqrt(a + b);

            a = Math.pow(boxMap.get(c)[0] - goalMap.get(c)[0], 2);
            b = Math.pow(boxMap.get(c)[1] - goalMap.get(c)[1], 2);
            int boxGoalDistance = (int) Math.sqrt(a + b);

            combinedDistances.add(agentBoxDistance + boxGoalDistance);
        }

//        System.err.println("Distance" + combinedDistances);
        Collections.sort(combinedDistances);
        return combinedDistances.get(0);
    }


    public int completeH(Node n) {



        return 0;
    }

    public int partialH(Node n) {

        int agentBoxDist = (Math.abs(partialBoxY - n.agentRow) + (Math.abs(partialBoxX - n.agentCol)));
        int boxGoalDist = (Math.abs(partialGoalY - partialBoxY) + (Math.abs(partialGoalX - partialBoxX)));
        int agentGoalDist = (Math.abs(partialGoalY - n.agentRow) + (Math.abs(partialGoalX - n.agentCol)));

        return agentBoxDist + boxGoalDist + agentGoalDist;
    }

    public int solvedGoalDistance(Node n) {
        int solvedGoalDistance = 0;
        if (solvedGoals != null && solvedGoals.size() > 0) {
            for (Goal goal : solvedGoals.keySet()) {
//                Box box = solvedGoals.get(goal);
//                System.err.println("Checking solved goal: " + goal.x + "," +goal.y);
                if (n.boxes[goal.y][goal.x] == 0) {
                    solvedGoalDistance += 101;
                }
            }
        }

        return solvedGoalDistance;
    }


    public int h(Node n) {

        if (n.chosenGoal != null && n.chosenBox != null) {

            int agentBoxDist = (Math.abs(n.chosenBox.y - n.agentRow) + (Math.abs(n.chosenBox.x - n.agentCol)));
            int boxGoalDist = (Math.abs(n.chosenGoal.y - n.chosenBox.y) + (Math.abs(n.chosenGoal.x - n.chosenBox.x)));
            int agentGoalDist = (Math.abs(n.chosenGoal.y - n.agentRow) + (Math.abs(n.chosenGoal.x - n.agentCol)));

            int randomBoxMoveDisadvantage = 0;

            if (n.chosenGoal.conflictingBoxes.size() == 0 && n.movingBoxId != 0 && n.movingBoxId != n.chosenBox.id) {
                randomBoxMoveDisadvantage = 100;
            } else if (n.movingBoxId != 0 && n.movingBoxId != n.chosenBox.id) {
                for (Box box : n.chosenGoal.conflictingBoxes) {
                    if (n.movingBoxId == box.id) {
                        randomBoxMoveDisadvantage = 0;
                        break;
                    }
                    randomBoxMoveDisadvantage = 100;
                }
            }

            return agentBoxDist + boxGoalDist + agentGoalDist + solvedGoalDistance(n) + randomBoxMoveDisadvantage;
        } else {
//            return h2(n);
            return partialH(n);
        }
    }

    public abstract int f(Node n);

    public static class AStar extends Heuristic {
        public AStar(Node initialState) {
            super(initialState);
        }

        public int f(Node n) {
//            System.err.println("g: "+n.g()+ " h: "+h(n));
            return n.g() + h(n);
        }

        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends Heuristic {
        private int W;

        public WeightedAStar(Node initialState) {
            super(initialState);
            W = 5; // You're welcome to test this out with different values, but for the reporting part you must at least indicate benchmarks for W = 5
        }

        public int f(Node n) {
            return n.g() + W * h(n);
        }

        public String toString() {
            return String.format("WA*(%d) evaluation", W);
        }
    }

    public static class Greedy extends Heuristic {

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
