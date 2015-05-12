package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.Box;
import SkyNet.model.Cell;
import SkyNet.model.Level;
import java.util.List;

public interface HTNUtils {
    public List<Command> findAgentMovePath(Cell start, Cell goal, Level level);
    public Level boxesToWalls(Level level, List<Box> boxes);
}
