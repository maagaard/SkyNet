package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.Agent;
import SkyNet.model.Box;
import SkyNet.model.Cell;
import SkyNet.model.Level;
import java.util.List;

public interface HTNUtils {
    public List<Cell> findAgentMovePAth(Cell start, Cell goal, Level level);
    public Node accomplishLevel(Agent agent, Level initial);
    public Level boxesToWalls(Level level, List<Box> boxes);
}
