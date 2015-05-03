package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.Level;
import SkyNet.model.PathFragment;
//https://functionaljava.ci.cloudbees.com/job/master/javadoc/
import fj.data.List;

public interface HTNUtils {
    public List<Command> findAgentMovePath(PathFragment.Position start, PathFragment.Position goal, Level level);
}
