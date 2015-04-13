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
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        Level level = LevelReader.ReadLevel(new BufferedReader(new InputStreamReader(System.in)));

        // Plan plan = Planner.plan(level);
        Plan plan = new Plan(new LinkedList<>());
        LinkedList<Node> solution = popClient.solveLevel();

        //Check and output plan
        if (plan.GetPlan().size() == 0) {
            System.err.println("Unable to solve level");
        } else {
            LevelWriter.ExecutePlan(plan);
        }

        System.exit(0);
    }
}
