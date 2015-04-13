package SkyNet.model;

import java.util.ArrayList;
import java.util.List;

public class Level {


    public boolean walls[][];
    public ArrayList<Goal> goals;

//    public ArrayList<Box> boxes;
//    public int agents[][];

    public Level() {

//        this.goals = goals;
//        this.walls = walls;


//        this.boxes = boxes;
    }

    public boolean celIsFree(int row, int col) {
        return !(this.walls[row][col]);
    }

}
