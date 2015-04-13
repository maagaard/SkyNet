package SkyNet.model;

import SkyNet.Node;

import java.util.LinkedList;

public class Plan {
    private LinkedList<Node> plan;

    public LinkedList<Node> GetPlan() { return plan; }

    public Plan(LinkedList<Node> _plan) {
        plan = _plan;
    }
}
