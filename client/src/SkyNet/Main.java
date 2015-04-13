package SkyNet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import SkyNet.PartialStrategy.*;
import SkyNet.PartialPlanHeuristic.*;

public class Main {


    public static void main(String[] args) throws Exception {
        /*
        - Read level from stdin
        - Plan level
        - Write level to stdout
         */


        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Read level and create the initial state of the problem
//        SearchClient client = new SearchClient(serverMessages);
        POP popClient = new POP(serverMessages);

//		strategy = new StrategyBFS();
        // Ex 1:
//		strategy = new StrategyDFS();
        // Ex 3:
//        PartialStrategy strategy = new StrategyBestFirst(new AStar(popClient.initialPath), null);

//        LinkedList<Node> solution = client.Search(strategy);

//        popClient.pickGoal();

        LinkedList<Node> solution = popClient.solveLevel();

        if (solution == null) {
            System.err.println("Unable to solve level");
            System.exit(0);
        } else {
//            System.err.println("\nSummary for " + strategy);
            System.err.println("Found solution of length " + solution.size());
//            System.err.println(strategy.searchStatus());

            for (Node n : solution) {
                String act = n.action.toActionString();
                System.out.println(act);
                String response = serverMessages.readLine();
                if (response.contains("false")) {
                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                    System.err.format("%s was attempted in \n%s\n", act, n);
                    break;
                }
            }

        }

    }
}
