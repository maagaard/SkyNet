package SkyNet.model;

import jdk.internal.util.xml.impl.Pair;

import javax.swing.text.Position;

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

    public Position boxLocation;
    public Position agentLocation;

    public PathFragment(Agent agent, Box box) {
        this.agentLocation = new Position(agent.x, agent.y);
        this.boxLocation = new Position(box.x, box.y);
    }
}
