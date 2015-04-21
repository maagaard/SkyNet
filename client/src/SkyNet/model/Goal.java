package SkyNet.model;

import java.util.Comparator;

/**
 * client
 * Created by maagaard on 24/03/15.
 * Copyright (c) maagaard 2015.
 */

public class Goal implements Comparator<Goal> {

    public char name;
    public int x;
    public int y;
    public int priority = 0;

    public Goal(char name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
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
    public int compare(Goal g1, Goal g2) {
        return g1.priority-g2.priority;
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
