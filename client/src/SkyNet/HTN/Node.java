package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.model.Agent;
import SkyNet.model.Level;

/**
 * Created by michel on 5/14/15.
 */
public class Node {
    public final int g;
    public final Level level;
    public final Node parent;
    public final Command command;
    public final Agent agent;

    public Node(Level lvl, Agent agent){
        this.agent = agent;
        this.g = 0;
        this.level = lvl;
        this.parent = null;
        this.command = null;
    }

    public Node(Node parent, Level lvl, Command cmd, Agent agent){
        this.agent = agent;
        this.g = parent.g + 1;
        this.parent = parent;
        this.level = lvl;
        this.command = cmd;
    }
}
