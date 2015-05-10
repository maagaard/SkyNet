package SkyNet.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Level {


    public boolean walls[][];
    public ArrayList<Goal> goals;
    public ArrayList<Box> boxes;
    public ArrayList<Agent> agents;

    public HashMap<Integer, Box> boxMap = new HashMap<>();

    public int width;
    public int height;

    public Level() {
    }

    public void createBoxMap() {
        for (Box box : boxes) {
            boxMap.put(box.id, box);
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


}
