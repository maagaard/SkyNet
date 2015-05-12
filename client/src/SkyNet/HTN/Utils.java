package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.*;

import java.util.*;

public class Utils implements HTNUtils{

    private class Heuristic implements Comparator<Cell>{
        private Cell goal;
        private Cell init;

        public Heuristic(Cell init, Cell goal) {
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


    @Override
    public List<Command> findAgentMovePath(Cell start, Cell goal, Level level) {
        start.g = 0;
        start.parent = null;
        Heuristic h = new Heuristic(start, goal);
        PriorityQueue<Cell> frontier = new PriorityQueue<Cell>(10, h);
        HashSet<Cell> explored = new HashSet<Cell>();

        List<Command> commands = new LinkedList<Command>();
        frontier.add(start);

        while(true) {
            if (frontier.isEmpty()) {
                return null;
            }

            Cell current = frontier.poll();
            if(current.x == goal.x && current.y == goal.y) {
                return getPath(current, goal, level);
            }

            explored.add(current);
            for(Cell c : findNeighbours(current, level)) {
                if(!explored.contains(c) && !frontier.contains(c)) {
                    frontier.add(c);
                }
            }
        }
    }

    @Override
    public Level boxesToWalls(Level level, List<Box> boxes) {
        Level rtnLevel = new Level();
        boolean[][] walls = new boolean[level.walls.length][level.walls.length];
        rtnLevel.height = level.height;
        rtnLevel.width = level.width;
        rtnLevel.cells = (ArrayList<Cell>) level.cells.clone();
        rtnLevel.goals = (ArrayList<Goal>) level.goals.clone();
        rtnLevel.boxes = new ArrayList<Box>();
        rtnLevel.agents = (ArrayList<Agent>) level.agents.clone();

        for(int i = 0; i < level.walls.length; i++) {
            walls[i] = Arrays.copyOf(level.walls[i], level.walls[i].length);
        }

        for(Box box: level.boxes) {
            walls[box.y][box.x] = true;
        }

        rtnLevel.walls = walls;
        return rtnLevel;
    }

    private List<Command> getPath(Cell c, Cell goal, Level level) {
        LinkedList<Command> commands = new LinkedList<Command>();
        while(c.parent != null) {
            commands.add(new Command(c.direction));
            c = c.parent;
        }
        Collections.reverse(commands);
        return commands;
    }

    public void printMap(Cell start, Cell goal, Level level) {
          for(int h = 0; h < level.height; h++) {
              for(int w = 0; w < level.width; w++) {
                  if(level.walls[h][w]) {
                      System.err.print("#");
                  } else if(start.x == w && start.y == h) {
                      System.err.print("S");
                  } else if(goal.x == w && goal.y == h) {
                      System.err.print("G");
                  } else {
                      System.err.print(" ");
                  }

              }
              System.err.print(System.getProperty("line.separator"));
          }
    }

    private List<Cell> findNeighbours(Cell current, Level level) {
        LinkedList<Cell> neighbours = new LinkedList<>();
        for( Command c : Command.every) {

            int x = current.x + dirToColChange(c.dir1);
            int y = current.y + dirToRowChange(c.dir1);

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

    private int dirToRowChange( Command.dir d ) {
        return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange( Command.dir d ) {
        return ( d == Command.dir.E ? 1 : ( d == Command.dir.W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
    }
}
