package SkyNet;

import SkyNet.model.Agent;
import SkyNet.model.Box;
import SkyNet.model.Goal;
import SkyNet.model.Level;
import SkyNet.Command.*;

import java.util.*;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class Node {

    public Level level;
//    public ArrayList<Box> boxes;
//    public ArrayList<Goal> goals;

    public boolean[][] walls; // = new boolean[MAX_ROW][MAX_COLUMN];
    public char[][] boxes;// = new char[MAX_ROW][MAX_COLUMN];
    public char[][] goals;//; = new char[MAX_ROW][MAX_COLUMN];

    public Goal chosenGoal = null;
    public Box chosenBox;
    public char movingBox = 0;
    public Agent actingAgent;
    public Node parent;
    public Command action;

    private static Random rnd = new Random(1);
    public static int MAX_ROW = 50;     //Default setting
    public static int MAX_COLUMN = 50;  //Default setting

    public int agentRow;
    public int agentCol;
    public char agentNumber;

    private int g;

//    public Node(Node parent) {
//        boxes = new char[MAX_ROW][MAX_COLUMN];
//
//        this.parent = parent;
//        if (parent == null) {
//            g = 0;
//            goals = new char[MAX_ROW][MAX_COLUMN];
//            ;
//            walls = new boolean[MAX_ROW][MAX_COLUMN];
//            ;
//        } else {
//            g = parent.g() + 1;
//            walls = parent.walls;
//            goals = parent.goals;
//        }
//    }

    public Node(Node parent, int rows, int columns) {

        MAX_ROW = rows;
        MAX_COLUMN = columns;

        boxes = new char[rows][columns];

        this.parent = parent;
        if (parent == null) {
            g = 0;
            goals = new char[rows][columns];
            walls = new boolean[rows][columns];
        } else {
            g = parent.g() + 1;
            chosenBox = parent.chosenBox;
            chosenGoal = parent.chosenGoal;
            walls = parent.walls;
            goals = parent.goals;
//            boxes = parent.boxes;
        }
    }

    public int g() {
        return g;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean isGoalState() {
        if (chosenGoal != null && chosenBox != null) {
//            return chosenGoal.x == chosenBox.x && chosenGoal.y == chosenBox.y;
            for (int row = 1; row < MAX_ROW - 1; row++) {
                for (int col = 1; col < MAX_COLUMN - 1; col++) {

                    char g = goals[row][col];
                    char b = Character.toLowerCase(boxes[row][col]);
                    char chosenB = Character.toLowerCase(chosenBox.name);

//                    if (g == chosenGoal.name && b == chosenB)

                    if (g != chosenGoal.name) {
                        continue;
                    }

                    if (b != chosenB) {
                        continue;
                    }
//                    if (b == Character.toLowerCase(chosenBox.name) && g == b)
                    if (b == g) {
                        return true;
                    }

                }
            }
            return false;
        }
        else {
            for (int row = 1; row < MAX_ROW - 1; row++) {
                for (int col = 1; col < MAX_COLUMN - 1; col++) {
                    char g = goals[row][col];
                    char b = Character.toLowerCase(boxes[row][col]);
                    if (g > 0 && b != g) {
                        return false;
                    }
                }
            }
            return true;
        }
    }


    public LinkedList<Node> extractPlan() {
        LinkedList<Node> plan = new LinkedList<Node>();
        Node n = this;
        while (!n.isInitialState()) {
            plan.addFirst(n);
            n = n.parent;
        }
        return plan;
    }


    public ArrayList<Node> getExpandedNodes() {
        ArrayList<Node> expandedNodes = new ArrayList<Node>(Command.every.length);
        for (Command c : Command.every) {
            // Determine applicability of action
            int newAgentRow = this.agentRow + dirToRowChange(c.dir1);
            int newAgentCol = this.agentCol + dirToColChange(c.dir1);

            if (c.actType == type.Move) {
                // Check if there's a wall or box on the cell to which the agent is moving
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    Node n = this.ChildNode();
                    n.action = c;
                    n.agentRow = newAgentRow;
                    n.agentCol = newAgentCol;
                    expandedNodes.add(n);
                }
            } else if (c.actType == type.Push) {
                // Make sure that there's actually a box to move
                if (boxAt(newAgentRow, newAgentCol)) {
                    int newBoxRow = newAgentRow + dirToRowChange(c.dir2);
                    int newBoxCol = newAgentCol + dirToColChange(c.dir2);
                    // .. and that new cell of box is free
                    if (cellIsFree(newBoxRow, newBoxCol)) {
                        Node n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[newBoxRow][newBoxCol] = this.boxes[newAgentRow][newAgentCol];
                        n.boxes[newAgentRow][newAgentCol] = 0;
                        n.movingBox = n.boxes[newBoxRow][newBoxCol];
//                        n.movingBox
//                        n.level.getBox()




//                        if (chosenBox != null) {
//                            n.chosenBox.x = newBoxCol;
//                            n.chosenBox.y = newBoxRow;
//                        }
                        expandedNodes.add(n);
                    }
                }
            } else if (c.actType == type.Pull) {
                // Cell is free where agent is going
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    int boxRow = this.agentRow + dirToRowChange(c.dir2);
                    int boxCol = this.agentCol + dirToColChange(c.dir2);
                    // .. and there's a box in "dir2" of the agent
                    if (boxAt(boxRow, boxCol)) {
                        Node n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[this.agentRow][this.agentCol] = this.boxes[boxRow][boxCol];
                        n.boxes[boxRow][boxCol] = 0;
                        n.movingBox = n.boxes[this.agentRow][this.agentCol];

                        if (chosenBox != null) {
//                            char b = n.boxes[this.agentRow][this.agentCol];
//                            if (chosenBox.name != b) {
//                                System.err.println("touching wrong box");
//                            }

//                            n.chosenBox.x = this.agentCol;
//                            n.chosenBox.y = this.agentRow;
//                            System.err.println("" + n.chosenBox.x + ":" + this.chosenBox.x + " - " + n.chosenBox.y + ":" + this.chosenBox.y);
                        }

                        expandedNodes.add(n);
                    }
                }
            }
        }
        Collections.shuffle(expandedNodes, rnd);
        return expandedNodes;
    }

    private boolean cellIsFree(int row, int col) {
        return (!this.walls[row][col] && this.boxes[row][col] == 0);
    }

    private boolean boxAt(int row, int col) {
        return this.boxes[row][col] > 0;
    }


    private int dirToRowChange(dir d) {
        return (d == dir.S ? 1 : (d == dir.N ? -1 : 0)); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange(dir d) {
        return (d == dir.E ? 1 : (d == dir.W ? -1 : 0)); // East is left one column (1), west is right one column (-1)
    }

    private Node ChildNode() {
        Node copy = new Node(this, MAX_ROW, MAX_COLUMN);
        for (int row = 0; row < MAX_ROW; row++) {
            System.arraycopy(this.boxes[row], 0, copy.boxes[row], 0, MAX_COLUMN);
        }
//        copy.chosenBox = new
        return copy;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + agentCol;
        result = prime * result + agentRow;
        result = prime * result + Arrays.deepHashCode(boxes);
        result = prime * result + Arrays.deepHashCode(goals);
        result = prime * result + Arrays.deepHashCode(walls);
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
        Node other = (Node) obj;
        if (agentCol != other.agentCol)
            return false;
        if (agentRow != other.agentRow)
            return false;
        if (!Arrays.deepEquals(boxes, other.boxes)) {
            return false;
        }
        if (!Arrays.deepEquals(goals, other.goals))
            return false;
        if (!Arrays.deepEquals(walls, other.walls))
            return false;
        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < MAX_ROW; row++) {
            if (!this.walls[row][0]) {
                break;
            }
            for (int col = 0; col < MAX_COLUMN; col++) {
                if (this.boxes[row][col] > 0) {
                    s.append(this.boxes[row][col]);
                } else if (this.goals[row][col] > 0) {
                    s.append(this.goals[row][col]);
                } else if (this.walls[row][col]) {
                    s.append("+");
                } else if (row == this.agentRow && col == this.agentCol) {
                    s.append("0");
                } else {
                    s.append(" ");
                }
            }

            s.append("\n");
        }
        return s.toString();
    }


}
