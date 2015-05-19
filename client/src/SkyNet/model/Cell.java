package SkyNet.model;

import SkyNet.Command;

public class Cell{

    public int g;
    public Cell parent;
    public Command.dir direction;
    public final int x;
    public final int y;
    public boolean success;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.success = false;
    }

    public Cell(int x, int y, boolean success) {
        this.x = x;
        this.y = y;
        this.success = success;
    }

    @Override
    public String toString(){
        return "Move(" + direction + ") - " + x + "," + y;
    }
}
