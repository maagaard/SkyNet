package SkyNet.model;

import java.util.ArrayList;
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

    public boolean celIsFree(int row, int col) {
        return !(this.walls[row][col]);
    }
}
