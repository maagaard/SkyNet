package SkyNet.Genetic;

import fj.Equal;
import fj.Ord;
import fj.data.Array;
import fj.data.List;
import fj.data.Stream;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

import static fj.data.List.list;
import static fj.data.Stream.range;
import static fj.data.Stream.stream;
import static java.util.Collections.shuffle;
import static java.util.Collections.swap;

public class GeneticAlgorithm {
    private static Random rnd = new Random();

    public static PopulationMember GenerateSolution(List<Gene> genes) {
        return GenerateSolution(50, genes, 0.24, 0.5, 0.9, 25);
    }

    public static PopulationMember GenerateSolution(int populationCount, List<Gene> genes,
                                                    double fstParentPopulationRange,
                                                    double sndParentPopulationRange,
                                                    double mutationChance,
                                                    int correctCount) {
        Array<PopulationMember> initialPopulation =
                range(0, populationCount).map(i -> {
                    java.util.List<Gene> xs = genes.toJavaList();
                    shuffle(xs);
                    return new PopulationMember(list(xs));
                }).sort(Ord.intOrd.comap(x -> x.score)).toArray();

        return evolve(correctCount, (int)((double) populationCount * fstParentPopulationRange),
                sndParentPopulationRange, mutationChance, 0, initialPopulation);
    }

    private static PopulationMember evolve(int minCorrectCount, int fstParentPopulationRange,
                                           double sndParentPopulationRange, double mutationChance,
                                           int correctCount, Array<PopulationMember> population) {
        if (minCorrectCount <= correctCount)
            return population.get(0);

        Collection<PopulationMember>
                newMembers = population.toStream()
                .take(fstParentPopulationRange)
                .toCollection()
                .parallelStream()
                .map(fst -> {
                    int sndIndex = (int) ((double) population.length() * sndParentPopulationRange * rnd.nextDouble());
                    return crossover(fst.genes, population.get(sndIndex).genes)
                            .map(childGenes -> mutate(childGenes, mutationChance))
                            .toCollection().stream();
                }).flatMap(x -> x)
                .collect(Collectors.toList());
        Array<PopulationMember>
                newPopulation = list(newMembers)
                .append(population.toStream()
                        .take(population.length() - 2 * fstParentPopulationRange).toList())
                .sort(Ord.intOrd.comap(x -> x.score))
                .toArray();
        if(newPopulation.get(0).score == population.get(0).score)
            correctCount++;
        else correctCount = 0;
        return evolve(minCorrectCount, fstParentPopulationRange,
                sndParentPopulationRange, mutationChance,
                correctCount, newPopulation);
    }

    private static Stream<List<Gene>> crossover(List<Gene> fstParent, List<Gene> sndParent) {
        int
                cutLen = (int) Math.ceil((double) fstParent.length() / 5.0),
                cutPoint = (int) (rnd.nextDouble() * ((double) fstParent.length() - cutLen));
        List<Gene>
                child1 = mixGenes(fstParent, sndParent, cutLen, cutPoint),
                child2 = mixGenes(sndParent, fstParent, cutLen, cutPoint);
        return stream(child1, child2);
    }

    private static PopulationMember mutate(List<Gene> genes, double mutationPercent) {
        if (rnd.nextDouble() > mutationPercent) {
            java.util.List<Gene> xs = genes.toJavaList();
            int
                    index1 = rnd.nextInt(genes.length()),
                    index2 = rnd.nextInt(genes.length());
            if (index1 == index2)
                if (index1 > 0) index1--;
                else index1++;
            swap(xs, index1, index2);
            return new PopulationMember(list(xs));
        } else return new PopulationMember(genes);
    }

    private static List<Gene> mixGenes(List<Gene> fstParent, List<Gene> sndParent, int cutLength, int cutPoint) {
        List<Gene>
                fstPart = fstParent.drop(cutPoint).take(cutLength),
                remainingFromSnd = sndParent.minus(Equal.<Gene>anyEqual(), fstPart);
        return remainingFromSnd
                .take(cutPoint)
                .append(fstPart)
                .append(remainingFromSnd.drop(cutPoint));
    }

    public static class PopulationMember {
        public final List<Gene> genes;
        public final int score;

        public PopulationMember(List<Gene> genes) {
            this.genes = genes;
            this.score = score(0, genes);
        }

        private int score(int acc, List<Gene> popMemberGenes) {
            if (popMemberGenes.length() == 1)
                return acc;
            else if (popMemberGenes.length() == 2)
                return acc + popMemberGenes.head().distanceTo(popMemberGenes.tail().head());
            else {
                return score(
                        acc + popMemberGenes.head().distanceTo(popMemberGenes.tail().head()),
                        popMemberGenes.tail());
            }
        }
    }
}
