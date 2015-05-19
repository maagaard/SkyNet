package SkyNet.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Level {
    public boolean walls[][];
    public ArrayList<Goal> goals;
    public ArrayList<Box> boxes;
    public ArrayList<Agent> agents;
    public int width;
    public int height;

    public Level() {
    }

    public Level(Level lvl, Goal goal){
        this.walls = lvl.walls;
        this.goals = new ArrayList<>(lvl.goals);
        this.goals.add(goal);
        this.width = lvl.width;
        this.height = lvl.height;
        this.agents = lvl.agents;
        this.boxes = lvl.boxes;
    }

    public Level(Level lvl, List<Box> boxes, Goal goal){
        this.walls = lvl.walls;
        this.goals = new ArrayList<>(lvl.goals);
        this.goals.add(goal);
        this.width = lvl.width;
        this.height = lvl.height;
        this.agents = lvl.agents;
        this.boxes = new ArrayList<>(boxes);
    }

    public Level(Level lvl, List<Box> boxes){
        this.walls = lvl.walls;
        this. goals = lvl.goals;
        this.width = lvl.width;
        this.height = lvl.height;
        this.agents = lvl.agents;
        this.boxes = new ArrayList<>(boxes);
    }

    public Level(Level lvl, List<Box> boxes, List<Goal> goals){
        this.walls = lvl.walls;
        this. goals = new ArrayList<>(goals);
        this.width = lvl.width;
        this.height = lvl.height;
        this.agents = lvl.agents;
        this.boxes = new ArrayList<>(boxes);
    }

    public boolean cellIsFree(int row, int col) {
        return !this.walls[row][col] &&
            !this.boxes.stream().anyMatch(box -> box.x == col && box.y == row);
    }

    @Override
    public String toString(){
        return this.boxes.toString();
    }
}
