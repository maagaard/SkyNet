package SkyNet.model;

import SkyNet.Command;

public class Cell{

    public int g;
    public Cell parent;
    public Command.dir direction;
    public final int x;
    public final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
