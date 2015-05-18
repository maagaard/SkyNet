package SkyNet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//import SkyNet.PartialStrategy.*;
//import SkyNet.PartialPlanHeuristic.*;
import SkyNet.HTN.DistanceMap;
import SkyNet.HTN.Search;
import SkyNet.HTN.Utils;
import SkyNet.model.Cell;
import SkyNet.model.Level;
import SkyNet.model.Plan;
import SkyNet.Strategy.*;
import SkyNet.Heuristic.*;

public class Main {

    public static void main(String[] args) throws Exception {

        System.err.println("SearchClient initializing.");
        //LIVE
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
        //DEBUG
//        BufferedReader serverMessages = new BufferedReader(new FileReader("levels/SAsimple1.lvl"));
        Level level = LevelReader.ReadLevel(serverMessages);

        Search search = new Search();
        List<Command> commands = search.completeLevel(level);

        //DEBUG distancemap
        Utils util = new Utils();
        DistanceMap distanceMap = new DistanceMap(util);
        Cell start = new Cell(1,1);
        HashMap<String, Integer> distances = distanceMap.calculateDistanceMap(start, level);
        distanceMap.printDistanceMap(distances, level);

        //DEBUG
//        for (Command c : commands) {
//            String act = c.toActionString();
//            System.out.println(act);
//        }

        //LIVE
        /*
        for (Command c : commands) {
            String act = c.toActionString();
            System.out.println(act);
            System.err.println(act);
            String response = serverMessages.readLine();
            if (response.contains("false")) {
                System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                break;
            }
        }
        */


        Cell start2 = new Cell(level.agents.get(0).x, level.agents.get(0).y);
        Cell goal = new Cell(1, 2);

        Utils utils = new Utils();

        System.err.println("--- Move agent from S to G ---");
        utils.printMap(start2, goal, level);

        System.err.println("--- Make boxes into wall!! ---");
        utils.printMap(start2, goal, utils.boxesToWalls(level, level.boxes));


        /*
        // Use stderr to print to console
        System.err.println("SearchClient initializing.");
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
        Level level = LevelReader.ReadLevel(serverMessages);

//        Node state = new Node(null, level.height, level.width);
        Strategy strategy = null;//new StrategyBestFirst(new AStar(state));

        Planner planner = new POP(strategy);    //null; //TODO: Use POP or whatever

        Plan plan = planner.createPlan(level);
//        LinkedList<Node> solution = popClient.solveLevel();

        //Check and output plan
        if (plan.GetPlan().size() == 0) {
            System.err.println("Unable to solve level");
        } else {
            System.err.println("Found solution of length " + plan.GetPlan().size());
            LevelWriter.ExecutePlan(plan, serverMessages);
        }
        */
        System.exit(0);
    }
}
