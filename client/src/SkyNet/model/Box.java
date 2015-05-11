package SkyNet.model;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class Box {

    public char name;
    public char lowerCaseName;
    public int x;
    public int y;

    static int enumChar = 0;
    public int id = 0;

    public Box(char name, int x, int y) {
        this.name = name;
        this.lowerCaseName = Character.toLowerCase(name);
        this.x = x;
        this.y = y;

        enumChar++;
        id = enumChar;
    }


    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + Character.toLowerCase(name);
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        //TODO: Modify below if statement to include where box or goal
//        if ( getClass() != obj.getClass() )
//            return false;
        if (this.hashCode() == obj.hashCode())
            return true;


        return false;
    }
}
