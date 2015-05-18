package SkyNet.Genetic;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PopulationMember implements Comparable{
    public final List<Gene> genes;
    public final int score;
    private final int startX;
    private final int startY;

    public PopulationMember(int startX, int startY, List<Gene> genes) {
        this.startX = startX;
        this.startY = startY;
        this.genes = Collections.unmodifiableList(genes);
        this.score = score(0, genes);
    }

    public PopulationMember(List<Gene> genes, PopulationMember parent) {
        this.startX = parent.startX;
        this.startY = parent.startY;
        this.genes = Collections.unmodifiableList(genes);
        this.score = score(0, genes);
    }

    private int score(int acc, List<Gene> popMemberGenes) {
        if (popMemberGenes.size() == 1)
            return acc;
        else {
            Gene head = popMemberGenes.get(0);
            List<Gene> tail = popMemberGenes.stream().skip(1).collect(Collectors.toList());
            int score = acc + head.distanceTo(tail.get(0));
            if(tail.size() >= 2)
                return score(score, tail);
            else
                return score;
        }
    }

    @Override
    public int compareTo(Object o) {
        return this.score - ((PopulationMember)o).score;
    }

    private int dist(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}

