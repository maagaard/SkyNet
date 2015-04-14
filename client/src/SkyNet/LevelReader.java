package SkyNet;

import SkyNet.model.Agent;
import SkyNet.model.Box;
import SkyNet.model.Goal;
import SkyNet.model.Level;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LevelReader {

    public static void error(String msg) throws Exception {
        throw new Exception("GSCError: " + msg);
    }

    public static Level ReadLevel(BufferedReader serverMessages) throws Exception {
        ArrayList<Box> boxes = new ArrayList<>();
        ArrayList<Agent> agents = new ArrayList<>();

        Map<Character, String> colors = new HashMap<>();
        String line, color;

        int agentCol = -1, agentRow = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ((line = serverMessages.readLine()).matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
            line = line.replaceAll("\\s", "");
            String[] colonSplit = line.split(":");
            color = colonSplit[0].trim();

            for (String id : colonSplit[1].split(",")) {
                colors.put(id.trim().charAt(0), color);
            }
            colorLines++;
        }

        if (colorLines > 0) {
            error("Box colors not supported");
        }


        int MAX_SIZE = 100;

        boolean[][] walls = new boolean[MAX_SIZE][MAX_SIZE];
//        char[][] boxes = new char[MAX_SIZE][MAX_SIZE];
//        char[][] goals = new char[MAX_SIZE][MAX_SIZE];
        int longestLine = 0;

        ArrayList<Goal> goals = new ArrayList<Goal>();

        while (!line.equals("")) {
            int length = line.length();
            if (length > longestLine) longestLine = length;

            for (int i = 0; i < length; i++) {
                char chr = line.charAt(i);
                if ('+' == chr) { // Walls
                    walls[levelLines][i] = true;
                } else if ('0' <= chr && chr <= '9') { // Agents
                    if (agentCol != -1 || agentRow != -1) {
                        error("Not a single agent level");
                    }
                    agents.add(new Agent(chr, i, levelLines));

                } else if ('A' <= chr && chr <= 'Z') { // Boxes
//                    boxes[levelLines][i] = chr;
                    boxes.add(new Box(chr, i, levelLines));
                } else if ('a' <= chr && chr <= 'z') { // Goal cells
//                    goals[levelLines][i] = chr;
                    goals.add(new Goal(chr, i, levelLines));

                }
            }
            line = serverMessages.readLine();
            levelLines++;
        }


        Level level = new Level();
        level.walls = walls;
        level.goals = goals;
        level.agents = agents;
        level.boxes = boxes;
        level.width = longestLine;
        level.height = levelLines;

        return level;
    }
}
