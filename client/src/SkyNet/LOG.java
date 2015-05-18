package SkyNet;

/**
 * client
 * Created by maagaard on 18/05/15.
 * Copyright (c) maagaard 2015.
 */
public class LOG {

    private static boolean debug = false;
    private static boolean verbose = true;


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

    public static void d(String message) {
        if (debug) {
            System.err.println(message);
        }
    }


    public static void v(String message) {
        if (verbose) {
            System.err.println(message);
        }
    }

}
