package SkyNet;

/**
 * client
 * Created by maagaard on 18/05/15.
 * Copyright (c) maagaard 2015.
 */
public class LOG {

    public static boolean debug = true;
    public static boolean verbose = true;


//    static {
//        DLOG(String printString) {
//            if (debug) {
//
//            }
//
//        }
//    }

    public LOG() {

    }

    public static void D(String message) {
        if (debug) {
            System.err.println(message);
        }
    }


    public static void V(String message) {
        if (verbose) {
            System.err.println(message);
        }
    }

}
