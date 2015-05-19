package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.Box;
import SkyNet.model.Cell;
import SkyNet.model.Level;

import java.util.*;

/**
 * Created by michel on 5/19/15.
 */
public class MovePathGenerator {
    private Utils utils;
    private HashMap<CellTuple, List<Cell>> pathMap;
    private Level rigidLevelData;

    private class CellTuple {
        private final int x1;
        private final int y1;
        private final int x2;
        private final int y2;
        private final List<Box> obstacles;

        private CellTuple(int x1, int y1, int x2, int y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.obstacles = new LinkedList<>();
        }

        private CellTuple(int x1, int y1, int x2, int y2, List<Box> obstacles){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.obstacles = new LinkedList<>(obstacles);
        }

        @Override
        public int hashCode(){
            int hash = 71;
            hash += hash * 31 + this.x1;
            hash += hash * 31 + this.y1;
            hash += hash * 31 + this.x2;
            hash += hash * 31 + this.y2;
            hash += hash * 31 + this.obstacles.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object o){
            if(o == null) return false;
            else {
                CellTuple other = (CellTuple)o;
                if(other.hashCode() != this.hashCode()) return false;
            }
            return true;
        }
    }

    private static MovePathGenerator instance = null;
    protected MovePathGenerator() {
        this.utils = new Utils();
        this.pathMap = new HashMap<>();
    }
    public static MovePathGenerator getInstance() {
        if(instance == null)
            instance = new MovePathGenerator();
        return instance;
    }

    public void initLevel(Level lvl){
        this.rigidLevelData = new Level(lvl, new LinkedList<>());
    }

    private class SingleGoalHeuristic implements Comparator<Cell> {
        private Cell goal;
        private Cell init;

        public SingleGoalHeuristic(Cell init, Cell goal) {
            this.init = init;
            this.goal = goal;
        }

        private int h(Cell current) {
            float dx = goal.x - current.x;
            float dy = goal.y - current.y;

            return (int)(Math.sqrt((dx*dx) + (dy*dy)));
        }

        public int f(Cell c) {
            return c.g + h(c);
        }

        @Override
        public int compare(Cell o1, Cell o2) {
            return f(o1) - f(o2);
        }
    }


    public List<Cell> findAgentMovePAth(Cell start, Cell goal) {
        return this.findAgentMovePAth(start, goal, this.rigidLevelData);
    }

    public List<Cell> findAgentMovePAth(Cell start, Cell goal, List<Box> obstacles) {
        Level lvl = new Level(this.rigidLevelData, obstacles);
        return this.findAgentMovePAth(start, goal, lvl);
    }

    public List<Cell> findAgentMovePAth(Cell start, Cell goal, Level lvl) {
        CellTuple key = new CellTuple(start.x, start.y, goal.x, goal.y, lvl.boxes);
        if(this.pathMap.containsKey(key)){
            return this.pathMap.get(key);
        }

        Cell path = findPath(start, goal, lvl);
        if(path != null && path.parent != null){
            List<Cell> result = getOrderedCells(path);
            this.pathMap.put(key, result);
            return result;
        }
        else {
            return Arrays.asList(new Cell(-1, -1));
        }
    }

    private Cell findPath(Cell start, Cell goal, Level level){
        start.g = 0;
        start.parent = null;
        SingleGoalHeuristic h = new SingleGoalHeuristic(start, goal);
        PriorityQueue<Cell> frontier = new PriorityQueue<Cell>(10, h);
        HashSet<Cell> explored = new HashSet<Cell>();
        frontier.add(start);

        while(true) {
            if (frontier.isEmpty())
                return null;

            Cell current = frontier.poll();
            if(current.x == goal.x && current.y == goal.y)
                return current;

            explored.add(current);
            for(Cell c : findNeighbours(current, level))
                if(!explored.contains(c) && !frontier.contains(c))
                    frontier.add(c);
        }
    }

    private List<Cell> getOrderedCells(Cell c) {
        List<Cell> cells = getPath(c);
        Collections.reverse(cells);
        return cells;
    }

    private List<Cell> getPath(Cell c) {
        LinkedList<Cell> path = new LinkedList<Cell>();
        while(c.parent != null) {
            c.success = true;
            path.add(c);
            c = c.parent;
        }
        return path;
    }

    private List<Cell> findNeighbours(Cell current, Level level) {
        LinkedList<Cell> neighbours = new LinkedList<>();
        for( Command c : Command.every) {

            int x = current.x + utils.dirToColChange(c.dir1);
            int y = current.y + utils.dirToRowChange(c.dir1);

            if(c.actType == Command.type.Move) {
                if(level.cellIsFree(y, x)) {
                    Cell cell = new Cell(x,y);
                    cell.parent = current;
                    cell.direction = c.dir1;
                    cell.g = current.g + 1;
                    neighbours.add(cell);
                }
            }
        }
        return neighbours;
    }
}
