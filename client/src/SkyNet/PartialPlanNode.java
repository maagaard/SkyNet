package SkyNet;

import SkyNet.model.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class PartialPlanNode {

    public Level level;

    public Box box;
    public Goal goal;
    public Agent agent;

    public ArrayList<PathFragment> path;

    private static Random rnd = new Random( 1 );
    public static int MAX_ROW = 50;     //Default setting
    public static int MAX_COLUMN = 50;  //Default setting

    public int agentRow;
    public int agentCol;

    private int g;
    public int g() {
        return g;
    }


    public PartialPlanNode(Level level, Agent agent, Goal goal, Box box) {
        this.level = level;

        this.agent = agent;
        this.goal = goal;
        this.box =  box;

    }

    public boolean isInitialState() {
        return this.path.size() == 0;
    }


    public boolean isGoalState() {
        if (box.equals(goal)) {
            return true;
        }
        return false;
//        for ( int row = 1; row < MAX_ROW - 1; row++ ) {
//            for ( int col = 1; col < MAX_COLUMN - 1; col++ ) {
//                char g = goals[row][col];
//                char b = Character.toLowerCase( boxes[row][col] );
//                if ( g > 0 && b != g) {
//                    return false;
//                }
//            }
//        }
//        return true;
    }








    //TODO: Must implement methods below
    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + box.hashCode();
        result = prime * result + agent.hashCode();
        result = prime * result + goal.hashCode();
        result = prime * result + level.hashCode();
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;

        PartialPlanNode other = (PartialPlanNode) obj;
        if (agent != other.agent)
            return false;
        if (box != other.box)
            return false;
        if (goal != other.goal)
            return false;
        if (level != other.level)
            return false;

        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
//        for ( int row = 0; row < MAX_ROW; row++ ) {
//            if ( !this.walls[row][0] ) {
//                break;
//            }
//            for ( int col = 0; col < MAX_COLUMN; col++ ) {
//                if ( this.boxes[row][col] > 0 ) {
//                    s.append( this.boxes[row][col] );
//                } else if ( this.goals[row][col] > 0 ) {
//                    s.append( this.goals[row][col] );
//                } else if ( this.walls[row][col] ) {
//                    s.append( "+" );
//                } else if ( row == this.agentRow && col == this.agentCol ) {
//                    s.append( "0" );
//                } else {
//                    s.append( " " );
//                }
//            }
//
//            s.append( "\n" );
//        }
        return s.toString();
    }

}
