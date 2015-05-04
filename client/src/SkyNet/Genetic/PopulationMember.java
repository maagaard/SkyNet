package SkyNet.Genetic;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PopulationMember implements Comparable{
    public final List<Gene> genes;
    public final int score;

    public PopulationMember(List<Gene> genes) {
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
}


