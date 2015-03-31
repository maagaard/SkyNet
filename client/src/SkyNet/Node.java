package SkyNet;

import SkyNet.model.Box;
import SkyNet.model.Goal;
import SkyNet.model.Level;
import SkyNet.model.PathFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class Node {

    public Level level;
    public ArrayList<PathFragment> path;
    public ArrayList<Box> boxes;
    public ArrayList<Goal> goals;

    private static Random rnd = new Random( 1 );
    public static int MAX_ROW = 50;     //Default setting
    public static int MAX_COLUMN = 50;  //Default setting

    public int agentRow;
    public int agentCol;

    private int g;
    public int g() {
        return g;
    }


    public boolean isInitialState() {
        return this.path.size() == 0;
    }


    //TODO: FIX
    public boolean isGoalState() {
//        for ( int row = 1; row < MAX_ROW - 1; row++ ) {
//            for ( int col = 1; col < MAX_COLUMN - 1; col++ ) {
//                char g = goals[row][col];
//                char b = Character.toLowerCase( boxes[row][col] );
//                if ( g > 0 && b != g) {
//                    return false;
//                }
//            }
//        }

        int goalCount = 0;

        for (Goal goal : goals) {
            for (Box box : boxes) {
                if (goal.equals(box)) {
                    ++goalCount;
                    break;
                }
            }
        }
        return goalCount == goals.size();
    }




    //TODO: FIX
    public LinkedList< Node > extractPlan() {
        LinkedList< Node > plan = new LinkedList< Node >();
        Node n = this;
        while( !n.isInitialState() ) {
            plan.addFirst( n );
//            n = n.parent;
        }
        return plan;
    }



    //TODO: Must implement methods below
    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
//        result = prime * result + agentCol;
//        result = prime * result + agentRow;
//        result = prime * result + Arrays.deepHashCode(boxes);
//        result = prime * result + Arrays.deepHashCode( goals );
//        result = prime * result + Arrays.deepHashCode( walls );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
//        if ( this == obj )
//            return true;
//        if ( obj == null )
//            return false;
//        if ( getClass() != obj.getClass() )
//            return false;
//        Node other = (Node) obj;
//        if ( agentCol != other.agentCol )
//            return false;
//        if ( agentRow != other.agentRow )
//            return false;
//        if ( !Arrays.deepEquals( boxes, other.boxes ) ) {
//            return false;
//        }
//        if ( !Arrays.deepEquals( goals, other.goals ) )
//            return false;
//        if ( !Arrays.deepEquals( walls, other.walls ) )
//            return false;

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
