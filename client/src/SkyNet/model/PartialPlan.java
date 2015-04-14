package SkyNet.model;

import SkyNet.Node;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * client
 * Created by maagaard on 14/04/15.
 * Copyright (c) maagaard 2015.
 */
public class PartialPlan {

    public LinkedList<Node> plan;

    public Goal goal;
    public Box box;
    public Agent agent;

    public int priority = 0;

    public PartialPlan(Agent agent, Goal goal, Box box, LinkedList<Node> partialSolution) {
        this.agent = agent;
        this.goal = goal;
        this.box = box;
        this.plan = partialSolution;
    }

    

}
