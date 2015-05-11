package SkyNet.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Level {


    public boolean walls[][];
    public ArrayList<Goal> goals;
    public ArrayList<Box> boxes;
    public ArrayList<Agent> agents;

    public HashMap<Integer, Box> boxMap = new HashMap<>();
    public HashMap<Integer, Goal> goalMap = new HashMap<>();

    public HashMap<Integer, Goal> solvedGoals = new HashMap<>();
    public ArrayList<Goal> unsolvedGoals = new ArrayList<>();
//    public HashMap<Integer, Goal> unsolvedGoals = new HashMap<>();

    public int width;
    public int height;

    public Level() {
    }

    public void createBoxMap() {
        for (Box box : boxes) {
            boxMap.put(box.id, box);
        }
    }

    public void createUnsolvedMap() {
        for (Goal goal: goals) {
            unsolvedGoals.add(goal);
        }
    }


    public boolean celIsFree(int row, int col) {
        return !(this.walls[row][col]);
    }

//    public Box getBox(int row, int column) {
//        for (Box box : boxes) {
//            if (box.x == column && box.y == row) return box;
//        }
//        return null;
//    }

    public Box getBox(Integer id) {
//        System.err.println("get box for id: " + id);
        return boxMap.get(id);
    }

    public void unsolveGoal(Goal goal) {
        goal.solveGoal(null);
        solvedGoals.remove(goal);
        unsolvedGoals.add(goal);
    }



    public void solveGoalWithBox(Goal goal, Box box) {
        System.err.println("Solving goal: " + goal.name);
        goal.solveGoal(box);
        solvedGoals.put(box.id, goal);
        unsolvedGoals.remove(goal);
    }

    public Goal hasSolvedGoal(Box box) {
        return solvedGoals.get(box);
    }
    public Goal hasSolvedGoal(Integer boxId) {
        return solvedGoals.get(boxId);
    }

}
