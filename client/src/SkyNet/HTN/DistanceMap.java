package SkyNet.HTN;

import SkyNet.model.Cell;
import SkyNet.model.Level;

import java.util.HashMap;

/**
 * Created by Martin on 18-05-2015.
 */
public class DistanceMap {

    private HTNUtils utils;
    private MovePathGenerator movePathGenerator;

    public DistanceMap(HTNUtils utils) {
        this.utils = utils;
        this.movePathGenerator = MovePathGenerator.getInstance();
    }

    /**
     * Calculates a distance map from a given
     * level and cell. Then returns all the
     * distances in a hashmap.
     *
     * @param Cell start
     * @param Level level
     * @return HashMap<String, Integer>
     */
    public HashMap<String, Integer> calculateDistanceMap(Cell start, Level level) {
        int width = level.width;
        int height = level.height;
        HashMap<String, Integer> map = new HashMap<>();
        System.err.println("calculate distance");

        for (int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                if(!level.walls[row][col]) {
                    Cell cell = new Cell(col, row);
                    int distance = this.movePathGenerator.findAgentMovePAth(start, cell, level).size();
                    map.put("" + cell.x + cell.y, distance);
                }
            }
        }

        return map;
    }

    public void printDistanceMap(HashMap<String, Integer> distanceMap, Level level) {
        for (int row = 0; row < level.height; row++) {
            for(int col = 0; col < level.width; col++) {
                if(!level.walls[row][col]) {
                    String cell = "" + col + row;
                    if(distanceMap.containsKey(cell)) {
                        int distanceint = distanceMap.get(cell);
                        String distance = (distanceint < 10)? "00" + distanceint : ((distanceint < 100)? "0" + distanceint : "" + distanceint);
                        System.err.print("(" + distance + ")");
                    } else {
                        System.err.print("(" + cell + ")");
                    }
                } else {
                    System.err.print("(###)");
                }
            }
            System.err.print(System.getProperty("line.separator"));
        }
    }
}
