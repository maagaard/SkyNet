package SkyNet.model;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Level(Level lvl, List<Box> boxes){
        this.walls = lvl.walls;
        this. goals = lvl.goals;
        this.width = lvl.width;
        this.height = lvl.height;
        this.agents = lvl.agents;
        this.boxes = new ArrayList<>(boxes);
    }

    public Level(Level lvl, List<Agent> agents, List<Box> boxes){
        this.walls = lvl.walls;
        this. goals = lvl.goals;
        this.width = lvl.width;
        this.height = lvl.height;
        this.agents = new ArrayList<>(agents);
        this.boxes = new ArrayList<>(boxes);
    }

    public boolean cellIsFree(int row, int col) {
        return !(this.walls[row][col] ||
            this.boxes.stream().anyMatch(box -> box.x == row && box.y == col));
    }
}
