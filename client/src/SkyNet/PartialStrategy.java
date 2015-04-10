package SkyNet;

import SkyNet.model.Level;
import SkyNet.model.PathFragment;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * client
 * Created by maagaard on 01/04/15.
 * Copyright (c) maagaard 2015.
 */
public abstract class PartialStrategy {

    public HashSet<PathFragment> explored;
    public long startTime = System.currentTimeMillis();

    public PartialStrategy() {
        explored = new HashSet<PathFragment>();
    }

    public void addToExplored(PathFragment n) {
        explored.add(n);
    }

    public boolean isExplored(PathFragment n) {
        return explored.contains(n);
    }

    public int countExplored() {
        return explored.size();
    }

    public String searchStatus() {
        return String.format("#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", countExplored(), countFrontier(), timeSpent(), POP.Memory.stringRep());
    }

    public float timeSpent() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public abstract PathFragment getAndRemoveLeaf();

    public abstract void addToFrontier(PathFragment n);

    public abstract boolean inFrontier(PathFragment n);

    public abstract int countFrontier();

    public abstract boolean frontierIsEmpty();

    public abstract String toString();

    
    
    public static class StrategyBFS extends PartialStrategy {

        private ArrayDeque<PathFragment> frontier;

        public StrategyBFS() {
            super();
            frontier = new ArrayDeque<PathFragment>();
        }

        public PathFragment getAndRemoveLeaf() {
            return frontier.pollFirst();
        }

        public void addToFrontier(PathFragment n) {
            frontier.addLast(n);
        }

        public int countFrontier() {
            return frontier.size();
        }

        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        public boolean inFrontier(PathFragment n) {
            return frontier.contains(n);
        }

        public String toString() {
            return "Breadth-first Search";
        }
    }

    public static class StrategyDFS extends PartialStrategy {

        private Stack<PathFragment> frontier;

        public StrategyDFS() {
            super();
            frontier = new Stack<PathFragment>();
        }

        public PathFragment getAndRemoveLeaf() {
            return frontier.pop();
        }

        public void addToFrontier(PathFragment n) {
            frontier.push(n);

        }

        public int countFrontier() {
            return frontier.size();
        }

        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        public boolean inFrontier(PathFragment n) {
            return frontier.contains(n);
        }

        public String toString() {
            return "Depth-first Search";
        }
    }


    public static class StrategyBestFirst extends PartialStrategy {
        private PartialPlanHeuristic heuristic;
        private Level level;
        private PriorityQueue<PathFragment> frontier;
        private PriorityQueue<PathFragment> oldFrontier;

        public StrategyBestFirst(PartialPlanHeuristic h, Level l) {
            super();
            heuristic = h;
            level = l;
            frontier = new PriorityQueue<PathFragment>(11, heuristic); //11 is default initial capacity
            oldFrontier = new PriorityQueue<PathFragment>(11, heuristic); //11 is default initial capacity
        }

        public PathFragment getAndRemoveLeaf() {

            PathFragment node;
            if (frontier.size() == 0) {
                node = oldFrontier.poll();
            } else {
                node = frontier.poll();
                oldFrontier.addAll(frontier);
                frontier.clear();
            }

            System.err.format("Heuristics: " + heuristic.f(node) + "\n");

            return node;
        }

        public void addToFrontier(PathFragment n) {
            frontier.add(n);
        }

        public int countFrontier() {
            return frontier.size() + oldFrontier.size();
        }

        public boolean frontierIsEmpty() {
            return frontier.isEmpty() && oldFrontier.isEmpty();
        }

        public boolean inFrontier(PathFragment n) {
            return frontier.contains(n);
        }

        public String toString() {
            return "Best-first Search (PriorityQueue) using " + heuristic.toString();
        }
    }
}

