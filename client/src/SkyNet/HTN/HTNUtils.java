package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.Level;
import SkyNet.model.PathFragment;
import java.util.List;

public interface HTNUtils {
    public List<Command> findAgentMovePath(PathFragment.Position start, PathFragment.Position goal, Level level);
}
