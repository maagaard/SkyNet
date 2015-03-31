package SkyNet;

import SkyNet.model.Box;
import SkyNet.model.Level;

import java.util.ArrayList;
import java.util.Random;

/**
 * client
 * Created by maagaard on 31/03/15.
 * Copyright (c) maagaard 2015.
 */
public class Node {

    public Level level;

    public ArrayList<Box> boxes;

    private static Random rnd = new Random( 1 );
    public static int MAX_ROW = 25;     //Default setting
    public static int MAX_COLUMN = 25;  //Default setting

    public int agentRow;
    public int agentCol;

    private int g;
    public int g() {
        return g;
    }

    public boolean isGoalState() {
//        for ( int row = 1; row < MAX_ROW - 1; row++ ) {
//            for ( int col = 1; col < MAX_COLUMN - 1; col++ ) {
//                char g = goals[row][col];
//                char b = Character.toLowerCase( boxes[row][col] );
//                if ( g > 0 && b != g) {
//                    return false;
//                }
//            }
//        }
        return true;
    }
}
