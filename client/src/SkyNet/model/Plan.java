package SkyNet.model;

import SkyNet.Command;

import java.util.LinkedList;

public class Plan {
    private LinkedList<Command> plan;

    public LinkedList<Command> GetPlan() { return plan; }

    public Plan(LinkedList<Command> _plan) {
        plan = _plan;
    }
}
