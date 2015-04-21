package SkyNet;

import SkyNet.Strategy.*;
import SkyNet.Heuristic.*;

public class Main {


    public static void main(String[] args) throws Exception {

        // Use stderr to print to console
        System.err.println("SearchClient initializing.");


        Benchmarks b = new Benchmarks("TEST", new StrategyBestFirst(new AStar(null)));
        b.setBefore();
        b.setAfter();
        System.out.println(b.prettyPrint());
        b.printToFile("TESTOUTPUT");

//        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
//        Level level = LevelReader.ReadLevel(serverMessages);
//
////        Node state = new Node(null, level.height, level.width);
//        Strategy strategy = null;//new StrategyBestFirst(new AStar(state));
//
//        Planner planner = new POP(strategy);    //null; //TODO: Use POP or whatever
//
//        //Memory
//        //Time
//        //Plan size
//        Plan plan = planner.createPlan(level);
////        LinkedList<Node> solution = popClient.solveLevel();
//
//        //Check and output plan
//        if (plan.GetPlan().size() == 0) {
//            System.err.println("Unable to solve level");
//        } else {
//            System.err.println("Found solution of length " + plan.GetPlan().size());
//            LevelWriter.ExecutePlan(plan, serverMessages);
//        }

        System.exit(0);
    }
}
