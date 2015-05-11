package SkyNet.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

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
    public HashSet<Box> conflictingBoxes = new HashSet<>();
    public Box suggestedBox = null;
    private Box solved = null;

    public Goal(char name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public void solveGoal(Box box) {
        this.solved = box;
        System.err.println("Goal solved: " + name);
    }

    public boolean isSolved() {
        return solved != null;
    }

    public Box getBox() {
        return solved;
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

        //compare (x, y) = - compare(y,x)
        if (g1.priority < g2.priority) { return -1;}
        else if (g1.priority > g2.priority) { return 1;}
        else { return 0;}

//        return g2.priority-g1.priority;
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
