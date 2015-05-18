package SkyNet.model;

import SkyNet.LOG;

import java.util.*;

/**
 * client
 * Created by maagaard on 24/03/15.
 * Copyright (c) maagaard 2015.
 */

public class Goal implements Comparable<Goal> {//Comparator<Goal> {

    public char name;
    public int x;
    public int y;
    public int conflictPriority = 0;
    public int sizePriority = 0;
    static int enumChar = 0;
    public int id = 0;

    public HashSet<Box> conflictingBoxes = new HashSet<>();
    public HashSet<Goal> conflictingPlans = new HashSet<>();
    public ArrayList<ProposedSolution> suggestedBoxOrder = new ArrayList<>();
    public Box suggestedBox;

    public int optimalSolutionLength = 0;
    public PartialPlan partialPlan;

    private Box solved = null;

    public Goal(char name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;

        enumChar++;
        id = enumChar;
    }

    public void solveGoal(Box box) {
        if (box == null) {
            this.solved = null;
            LOG.D("Goal destroyed: " + name);
            return;
        }
        this.solved = box;
    }

    public boolean isSolved() {
        return solved != null;
    }

    public Box getBox() {
        return solved;
    }


    //Priority queue with boxes for solving goal
    public void addSolutionBox(Box box, int length) {

        suggestedBoxOrder.add(new ProposedSolution(box, length));

//        suggestedBoxOrder.add(box);
//        if (boxQueue == null) {
//            boxQueue = null;
//        }
//
    }

    public ArrayList<ProposedSolution> getProposedSolutions() {
        Collections.sort(suggestedBoxOrder);
        return suggestedBoxOrder;
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

//    @Override
//    public int compare(Goal g1, Goal g2) {
//        //compare (x, y) = - compare(y,x)
////        if (g1.priority < g2.priority) { return -1;}
////        else if (g1.priority > g2.priority) { return 1;}
////        else { return 0;}
//        System.err.println("g2(" + g2.name+"): " + g2.priority + ", g1("+g1.name+"): " + g1.priority);
//        return g2.priority-g1.priority;
//    }


    @Override
    public int compareTo(Goal g) {
        LOG.D("Comparing this: " + this.name + " to " + g.name);
        if (this.conflictingPlans.contains(g) || g.conflictingPlans.contains(this)) {
            //Mutual conflict
            LOG.D("This conflicts: " + this.conflictPriority+ " and g: "+ g.conflictPriority);
            return this.conflictPriority - g.conflictPriority;
        } else {
            LOG.D("This size: " + this.sizePriority + " and g: "+ g.sizePriority);
            return g.sizePriority - this.sizePriority;
        }
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


class ProposedSolution implements Comparable<ProposedSolution> {

    Box box;
    int length;

    ProposedSolution(Box box, int length) {
        this.box = box;
        this.length = length;
    }

    @Override
    public int compareTo(ProposedSolution solution) {
        return this.length - solution.length;
    }
}