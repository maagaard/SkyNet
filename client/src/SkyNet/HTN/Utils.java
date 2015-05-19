package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class Utils implements HTNUtils {

    public Utils(){
    }

    private int manhatten_dist(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private boolean goalAchieved(List<Box> boxes, Goal g){
        return boxes.stream().anyMatch(b ->
                (int) g.name - 32 == (int) b.name &&
                        b.x == g.x && b.y == g.y);
    }

    private class LevelHeuristic implements Comparator<Node>{

        private MovePathGenerator movePathGenerator;

        public LevelHeuristic() {
            this.movePathGenerator = MovePathGenerator.getInstance();
        }

        private int h(Level lvl, Agent a) {
            List<Integer> dists = new LinkedList<>();
            LinkedList<Integer> agentToBoxesDists = new LinkedList<>();

            for(Goal g : lvl.goals){
                //skipping completed goals
                if(goalAchieved(lvl.boxes, g))
                    continue;

                List<Box> boxes = lvl.boxes.stream().filter(b -> (int) g.name - 32 == (int) b.name).collect(Collectors.toList());

                int closestBoxToGoal = 0;
                LinkedList<Integer> boxesDists = new LinkedList<>();
                Box closestBoxToAgent = boxes.get(0);

                for(Box box : boxes){
                    //int distToGoal = manhatten_dist(box.x, box.y, g.x, g.y);
                    List<Box> obstacles = lvl.boxes.stream()
                            .filter(b -> b.x == box.x && b.y == box.y)
                            .collect(Collectors.toList());
                    List<Cell> agentMovePAth =
                            this.movePathGenerator.findAgentMovePAth(
                                    new Cell(box.x, box.y),
                                    new Cell(g.x, g.y),
                                    obstacles);
                    int distToGoal = agentMovePAth.size();
                    if(agentMovePAth.size() == 1 && agentMovePAth.get(0).x == -1)
                        distToGoal = agentMovePAth.size();

                    boxesDists.add(distToGoal);

                    if(boxesDists.get(closestBoxToGoal) > distToGoal){
                        closestBoxToGoal = boxesDists.size() - 1;
                        closestBoxToAgent = box;
                    }
                }
                int agentToBoxDist = manhatten_dist(closestBoxToAgent.x,
                        closestBoxToAgent.y, a.x, a.y);
                agentToBoxesDists.add(agentToBoxDist);
                dists.add(boxesDists.get(closestBoxToGoal));
            }
            if(agentToBoxesDists.size() > 0) {
                return dists.stream().reduce((acc, d) -> acc + d).get() +
                        agentToBoxesDists.stream().min(Integer::min).get();
            } else {
                if (dists.size() > 0)
                    return dists.stream().reduce((acc, x) -> acc + x).get();
                else
                    return 0;
            }
        }

        public int f(Node n) {
            return n.g + h(n.level, n.agent);
        }

        @Override
        public int compare(Node o1, Node o2) {
            return f(o1) - f(o2);
        }
    }

    @Override
    public Node accomplishLevel(Agent agent, Level lvl) {
        Node start = new Node(lvl, agent);
        LevelHeuristic h = new LevelHeuristic();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(10, h);
        HashSet<Node> explored = new HashSet<Node>();

        frontier.add(start);

        while(true) {
            if (frontier.isEmpty()) {
                return null;
            }

            Node current = frontier.poll();
            if(current.level.goals.stream().allMatch(g -> goalAchieved(current.level.boxes, g))){
                return current;
            }

            explored.add(current);
            for(Node c : findNeighbours(current)) {
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

              } System.err.print(System.getProperty("line.separator"));
          }
    }

    private List<Node> findNeighbours(Node current) {
        List<Node> neighbours = new LinkedList<>();
        for( Command c : Command.every) {

            Agent agent = current.agent;
            int newAgentCol = agent.x + dirToColChange(c.dir1);
            int newAgentRow = agent.y + dirToRowChange(c.dir1);

            if(c.actType == Command.type.Move) {
                if(current.level.cellIsFree(newAgentRow, newAgentCol)) {
                    Agent newAgent = new Agent(agent.number, newAgentCol, newAgentRow);
                    Node n = new Node(current, current.level, c, newAgent);
                    neighbours.add(n);
                }
            } else if ( c.actType == Command.type.Push ) {
                // Make sure that there's actually a box to move
                Optional<Box> anyBox = boxAt(current.level, newAgentCol, newAgentRow);
                if (anyBox.isPresent()) {
                    int newBoxRow = newAgentRow + dirToRowChange( c.dir2 );
                    int newBoxCol = newAgentCol + dirToColChange( c.dir2 );
                    // .. and that new cell of box is free
                    if (current.level.cellIsFree(newBoxRow, newBoxCol) ) {
                        List<Box> boxes = current.level.boxes.stream()
                                .filter(b -> !(b.x == newAgentCol && b.y == newAgentRow)).collect(Collectors.toList());
                        boxes.add(new Box(anyBox.get().name, newBoxCol, newBoxRow));

                        Agent newAgent = new Agent(agent.number, newAgentCol, newAgentRow);
                        Level lvl = new Level(current.level, boxes);

                        Node n = new Node(current, lvl, c, newAgent);
                        neighbours.add(n);
                    }
                }
            } else if ( c.actType == Command.type.Pull ) {
                // Cell is free where agent is going
                if (current.level.cellIsFree(newAgentRow, newAgentCol)) {
                    int boxRow = agent.y + dirToRowChange( c.dir2 );
                    int boxCol = agent.x + dirToColChange( c.dir2 );
                    // .. and there's a box in "dir2" of the agent
                    Optional<Box> anyBox = boxAt(current.level, boxCol, boxRow);
                    if (anyBox.isPresent()) {
                        List<Box> boxes = current.level.boxes.stream()
                                .filter(b -> !(b.x == boxCol && b.y == boxRow)).collect(Collectors.toList());
                        boxes.add(new Box(anyBox.get().name, agent.x, agent.y));

                        Agent newAgent = new Agent(agent.number, newAgentCol, newAgentRow);
                        Level lvl = new Level(current.level, boxes);

                        Node n = new Node(current, lvl, c, newAgent);
                        neighbours.add(n);
                    }
                }
            }
        }
        return neighbours;
    }


    public int dirToRowChange( Command.dir d ) {
        return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
    }

    public int dirToColChange( Command.dir d ) {
        return ( d == Command.dir.E ? 1 : ( d == Command.dir.W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
    }

    private Optional<Box> boxAt(Level lvl, int col, int row) {
        //return this.boxes[row][col] > 0;
        return lvl.boxes.stream().filter(b -> b.x == col && b.y == row).findAny();
    }
}
