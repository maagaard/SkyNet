package SkyNet.model;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class PathFragment {

    public class Position {
        public final int x, y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public Position goalLocation;
    public Position boxLocation;
    public Position agentLocation;
    public int pathLength;

    public PathFragment(Agent agent, Box box, Goal goal, int length) {
        this.goalLocation = new Position(goal.x, goal.y);
        this.agentLocation = new Position(agent.x, agent.y);
        this.boxLocation = new Position(box.x, box.y);
        this.pathLength = length;
    }
}
