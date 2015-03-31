package SkyNet.model;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class PathFragment {

    public Box boxLocation;
    public Agent agentLocation;

    public PathFragment(Agent agent, Box box) {
        this.boxLocation = box;
        this.agentLocation = agent;
    }

}
