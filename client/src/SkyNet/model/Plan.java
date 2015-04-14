package SkyNet.model;

import SkyNet.Node;

import java.util.Comparator;
import java.util.LinkedList;

public class Plan implements Comparator<PartialPlan> {
    private LinkedList<Node> plan;

    private LinkedList<PartialPlan> partialPlans;

    public LinkedList<Node> GetPlan() { return plan; }


    public Plan(LinkedList<Node> plan) {
        this.plan = plan;
    }


    //TODO: Also prioritize with regards to initial heuristics as to know which plan is best to solve first


    @Override
    public int compare(PartialPlan p1, PartialPlan p2) {
        return p1.priority - p2.priority;
//        return 0;
    }

}
