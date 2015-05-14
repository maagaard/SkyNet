package SkyNet;

import SkyNet.model.Plan;

import java.io.BufferedReader;
import java.io.IOException;

public class LevelWriter {
    public static void ExecutePlan(Plan plan, BufferedReader serverMessages) throws IOException {

        for (Node n : plan.GetPlan()) {
            n.isExecuted = true;
            String act = n.action.toActionString();
            System.out.println(act);
            String response = serverMessages.readLine();
            if (response.contains("false")) {
                System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                System.err.format("%s was attempted in \n%s\n", act, n);
//                break;
                continue;
            }
        }
    }
}
