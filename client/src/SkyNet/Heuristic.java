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

    public int goalY, goalX, initialBoxY, initialBoxX;


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

        //TODO: Make dynamic maybe??

        //TODO: OLD STUFF - MAYBE RE-WRITE TO BE USED AGAIN
        for (int i = 0; i < initialState.goals.length; i++) {
            for (int j = 0; j < initialState.goals[i].length; j++) {
                if (initialState.goals[i][j] != 0) {
//                    Advanced
//                    goalMap.put(Character.toLowerCase(initialState.goals[i][j]), new int[]{i, j});
//                    goalRow = i;
//                    goalColumn = j;

                    goalY = i;
                    goalX = j;
                }
            }
        }
        for (int i = 0; i < initialState.boxes.length; i++) {
            for (int j = 0; j < initialState.boxes[i].length; j++) {
                if (initialState.boxes[i][j] != 0) {
//                    boxMap.put(Character.toLowerCase(initialState.level.getBox(initialState.boxes[i][j]).name), new int[]{i, j});
                    initialBoxY = i;
                    initialBoxX = j;
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


    public int fullH(Node n) {



        return 0;
    }


    public int partialH(Node n) {

        if (n.action.actType == Command.type.Move) {
            // return distance to box and distance from box to goal
            if (n.boxes[initialBoxY][initialBoxX] > 0) {
                int agentBoxDist = Math.abs(initialBoxY - n.agentRow) + Math.abs(initialBoxX - n.agentCol);
                int boxGoalDist = Math.abs(goalY - initialBoxY) + Math.abs(goalX - initialBoxX);
                return boxGoalDist + agentBoxDist + 5;
            } else {
                int agentBoxDist = Math.abs(n.movingBoxY - n.agentRow) + Math.abs(n.movingBoxX - n.agentCol);
                int boxGoalDist = Math.abs(goalY - n.movingBoxY) + Math.abs(goalX - n.movingBoxX);
                return boxGoalDist + agentBoxDist + 5;
            }
        } else { //if (n.action.actType == Command.type.Push) {
            //Return distance from box to goal
            return Math.abs(goalY - n.movingBoxY) + Math.abs(goalX - n.movingBoxX);
        }
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

            if (n.action.actType == Command.type.Move) {
                // return distance to box and distance from box to goal
                int agentBoxDist = Math.abs(n.chosenBox.y - n.agentRow) + Math.abs(n.chosenBox.x - n.agentCol);
                int boxGoalDist = Math.abs(n.chosenGoal.y - n.chosenBox.y) + Math.abs(n.chosenGoal.x - n.chosenBox.x);

                return boxGoalDist + agentBoxDist + 9;

            } else {
                //When pushing and pulling

                if (n.movingBoxId != n.chosenBox.id) {

                    boolean isBoxInConflict = n.chosenGoal.conflictingBoxes.contains(n.level.getBox(n.movingBoxId));

                    int agentBoxDist = Math.abs(n.chosenBox.y - n.agentRow) + Math.abs(n.chosenBox.x - n.agentCol);
                    int boxGoalDist = Math.abs(n.chosenGoal.y - n.chosenBox.y) + Math.abs(n.chosenGoal.x - n.chosenBox.x);

                    if (isBoxInConflict) {
                        //Park box elsewhere
//                        if (n.level.isParkingCell(n.movingBoxX, n.movingBoxY)) {
//                        }

                        return boxGoalDist + agentBoxDist + 5 + n.destroyingGoal;
                    } else {
                        return boxGoalDist + agentBoxDist + 31 + n.destroyingGoal;
                    }
                } else {

                    if (n.action.actType == Command.type.Push) {

                    }

                    int agentBoxDist = Math.abs(n.movingBoxY - n.agentRow) + Math.abs(n.movingBoxX - n.agentCol); //Always 1
                    int boxGoalDist = Math.abs(n.chosenGoal.y - n.movingBoxY) + Math.abs(n.chosenGoal.x - n.movingBoxX);
                    return boxGoalDist + agentBoxDist;
                }


                //TODO: Courage moving of correct box towards goal

                //TODO: Correlate with setting of chosen box in Node

            }


        } else {
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
