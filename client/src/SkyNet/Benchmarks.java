package SkyNet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Benchmarks {
    public class Stats {
        public float usedMem;
        public long time;
        public String memoryString;
    }

    public Stats before;
    public Stats after;
    public String clientName;
    public Strategy strategy;

    public Benchmarks(String clientName, Strategy strategy) {
        this.clientName = clientName;
        this.strategy = strategy;
    }

    public void setBefore() {
        before = getCurrentStats();
    }

    public void setAfter() {
       after = getCurrentStats();
    }

    public Stats getCurrentStats() {
        Stats stats = new Stats();
        stats.memoryString = Memory.stringRep();
        stats.usedMem = Memory.used();
        stats.time = System.nanoTime();
        return stats;
    }

    public String prettyPrint() {
        if(before != null && after != null) {
            String res = "\n";
            res += "Client: " + clientName + "\n";
            res += "Strategy: " + strategy.toString();
            res += strategy.searchStatus() + "\n";
            res += "Memory used: " + (after.usedMem - before.usedMem) + "MB"+ "\n";
            res += "Time used: " + (after.time - before.time) + "ns";
            return res;
        }
        else return "Benchmark unfinished for client: " + clientName;
    }

    public void printToFile(String path) throws IOException {
        Files.write(Paths.get(path), prettyPrint().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public void printDiff() {
        System.err.println("Memory used:");
        System.err.println(after.usedMem - before.usedMem + "MB");
        System.err.println("Time used:");
        System.err.println(after.time - before.time + "nanoseconds");
    }

    public static void printDiff(Stats s1, Stats s2) {
        System.err.println("Memory used:");
        System.err.println(s2.usedMem - s1.usedMem + "MB");
        System.err.println("Time used:");
        System.err.println(s2.time - s1.time + "nanoseconds");
    }
}
