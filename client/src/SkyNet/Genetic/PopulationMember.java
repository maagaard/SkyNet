package SkyNet.Genetic;

import java.util.List;
import java.util.stream.Collectors;

public class PopulationMember implements Comparable{
    public final List<Gene> genes;
    public final int score;

    public PopulationMember(List<Gene> genes) {
        this.genes = genes;
        this.score = score(0, genes);
    }

    private int score(int acc, List<Gene> popMemberGenes) {
        if (popMemberGenes.size() == 1)
            return acc;
        else if (popMemberGenes.size() == 2)
            return acc + popMemberGenes.get(0)
              .distanceTo(popMemberGenes.stream()
                .skip(1).findFirst().get());
        else {
            return score(
              acc + popMemberGenes.get(0)
                .distanceTo(popMemberGenes.stream().skip(1).findFirst().get()),
              popMemberGenes.stream()
                .skip(1)
                .collect(Collectors.toList()));
        }
    }

    @Override
    public int compareTo(Object o) {
        return this.score - ((PopulationMember)o).score;
    }
}

