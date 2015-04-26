package SkyNet.model;

public abstract class ItemAt implements Atom {
    private char item;
    private int x;
    private int y;

    public ItemAt(char item, int x, int y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    public boolean isAt(char item, int x, int y){
        return this.x == x && this.y == y && item == this.item;
    }

    public void setPos(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isRigid() {
        return false;
    }
}



