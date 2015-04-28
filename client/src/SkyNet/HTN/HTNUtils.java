package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.Level;
//https://functionaljava.ci.cloudbees.com/job/master/javadoc/
import fj.data.List;

public interface HTNUtils {
    public List<Command> findAgentMovePath(Position start, Position goal, Level level);
}
