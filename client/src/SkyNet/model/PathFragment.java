package SkyNet.model;

import SkyNet.Command;

import java.util.Arrays;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class PathFragment {

    public class Position {
        public final int x, y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public Command action;
    public Position goalLocation;
    public Position boxLocation;
    public Position agentLocation;
    public int pathLength;

    public PathFragment(Agent agent, Box box, Goal goal, Command action, int length) {
        this.goalLocation = new Position(goal.x, goal.y);
        this.agentLocation = new Position(agent.x, agent.y);
        this.boxLocation = new Position(box.x, box.y);
        this.action = action;
        this.pathLength = length;
    }


    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        if (action == null) {
            return result;
        }
        result = prime * result + action.dir1.ordinal();

        if (action.dir2 != null) {
            result = prime * result + action.dir2.ordinal();
        }

        result = prime * result + action.actType.ordinal();
        result = prime * result + agentLocation.x;
        result = prime * result + agentLocation.y;
        result = prime * result + boxLocation.x;
        result = prime * result + boxLocation.y;
        result = prime * result + goalLocation.x + goalLocation.y;
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        //TODO: Modify below if statement to include where box or goal
        if ( getClass() != obj.getClass() )
            return false;

        if (this.hashCode() == obj.hashCode())
            return true;

        return false;
    }

//    @Override
//    public boolean equals( Object obj ) {
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
//        return true;
//    }
}
