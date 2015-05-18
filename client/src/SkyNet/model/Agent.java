package SkyNet.model;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class Agent {

    public Agent(char number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
    }

    public char number;
    public int x;
    public int y;
    public String color;
}
