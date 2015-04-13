package SkyNet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;

import SkyNet.PartialStrategy.*;
import SkyNet.PartialPlanHeuristic.*;
import SkyNet.model.Level;
import SkyNet.model.Plan;

public class Main {


    public static void main(String[] args) throws Exception {

        // Use stderr to print to console
        System.err.println("SearchClient initializing.");
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
        Level level = LevelReader.ReadLevel(serverMessages);

        Planner planner = null; //TODO: Use POP or whatever

        Plan plan = planner.createPlan(level);
//        LinkedList<Node> solution = popClient.solveLevel();

        //Check and output plan
        if (plan.GetPlan().size() == 0) {
            System.err.println("Unable to solve level");
        } else {
            System.err.println("Found solution of length " + plan.GetPlan().size());
            LevelWriter.ExecutePlan(plan, serverMessages);
        }

        System.exit(0);
    }
}
