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

    public Box getBox(int row, int column) {
        for (Box box : boxes) {
            if (box.x == column && box.y == row) return box;
        }
        return null;
    }

}
