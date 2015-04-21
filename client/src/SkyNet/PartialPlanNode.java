package SkyNet;

import SkyNet.model.*;
import SkyNet.Command.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class PartialPlanNode extends Node {

    public Level level;

    public Box box;
    public Goal goal;
    public Agent agent;

    public ArrayList<PathFragment> path = new ArrayList<PathFragment>();

    private static Random rnd = new Random(1);

    public PartialPlanNode(Node parent, int rows, int columns) {
        super(parent, rows, columns);
    }

//    public PartialPlanNode(PartialPlanNode node) {
//        super(node);
//
//    }

    public PartialPlanNode(Level level, Agent agent, Goal goal, Box box) {
        super(null, 0, 0);
        this.level = level;
        this.agent = agent;
        this.goal = goal;
        this.box = box;
    }

    public boolean isInitialState() {
        return this.path.size() == 0;
    }


    public boolean isGoalState() {
        if (box.equals(goal)) {
            return true;
        }
        return false;
    }

    public boolean isGoalState(PathFragment pathFragment) {
        if (pathFragment.boxLocation.x == pathFragment.goalLocation.x && pathFragment.boxLocation.y == pathFragment.goalLocation.y) {
            return true;
        }
        return false;
    }


    public ArrayList<PathFragment> getExpandedPaths() {
        ArrayList<PathFragment> expandedPaths = new ArrayList<PathFragment>(Command.every.length);

        for (Command c : Command.every) {
            // Determine applicability of action
            int newAgentRow = this.agent.y + dirToRowChange(c.dir1);
            int newAgentCol = this.agent.x + dirToColChange(c.dir1);

            if (c.actType == type.Move) {
                // Check if there's a wall or box on the cell to which the agent is moving
                if (this.level.celIsFree(newAgentRow, newAgentCol)) {
                    Agent agent = new Agent(this.agent.number, newAgentCol, newAgentRow);
                    expandedPaths.add(new PathFragment(agent, this.box, this.goal, c, this.path.size() + 1));
                }
            } else if (c.actType == type.Push) {
                // Make sure that there's actually a box to move
                if (box.y == newAgentRow && box.x == newAgentCol) {
                    int newBoxRow = newAgentRow + dirToRowChange(c.dir2);
                    int newBoxCol = newAgentCol + dirToColChange(c.dir2);
                    // .. and that new cell of box is free
                    if (this.level.celIsFree(newBoxRow, newBoxCol)) {
                        Agent agent = new Agent(this.agent.number, newAgentCol, newAgentRow);
                        Box box = new Box(this.box.name, newBoxCol, newBoxRow);
                        expandedPaths.add(new PathFragment(agent, box, this.goal, c, this.path.size() + 1));
                    }
                }
            } else if (c.actType == type.Pull) {
                // Cell is free where agent is going
                if (this.level.celIsFree(newAgentRow, newAgentCol)) {
                    int boxRow = this.agent.y + dirToRowChange(c.dir2);
                    int boxCol = this.agent.x + dirToColChange(c.dir2);
                    // .. and there's a box in "dir2" of the agent
                    if (box.y == boxRow && box.x == boxCol) {
                        Agent agent = new Agent(this.agent.number, newAgentCol, newAgentRow);
                        Box box = new Box(this.box.name, this.agent.x, this.agent.y);
                        expandedPaths.add(new PathFragment(agent, box, this.goal, c, this.path.size() + 1));

                    }
                }
            }
        }
        Collections.shuffle(expandedPaths, rnd);

        //TODO: THIS can't happen
        //TODO: Instead, the chosen path should be added to this.path
//        this.path = expandedPaths;

        return expandedPaths;
    }

    private int dirToRowChange(dir d) {
        return (d == dir.S ? 1 : (d == dir.N ? -1 : 0)); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange(dir d) {
        return (d == dir.E ? 1 : (d == dir.W ? -1 : 0)); // East is left one column (1), west is right one column (-1)
    }


    //TODO: FIX
//    @Override
//    public LinkedList<PartialPlanNode> extractPartialPlan() {
//        LinkedList<PartialPlanNode> plan = new LinkedList<PartialPlanNode>();
//        Node n = this;
//        while( !n.isInitialState() ) {
////            plan.addFirst(n);
////            n = n.parent;
//        }
//        return plan;
//    }

    public LinkedList<Command> extractPartialPlan() {
        LinkedList<Command> actions = new LinkedList<Command>();
//        Node n = this;
        for (PathFragment p : this.path) {
            actions.add(p.action);
        }

        return actions;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
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

    public void update(PathFragment leafPath) {
        this.path.add(leafPath);
        this.agent.x = leafPath.agentLocation.x;
        this.agent.y = leafPath.agentLocation.y;
        this.box.x = leafPath.boxLocation.x;
        this.box.y = leafPath.boxLocation.y;
    }
}
