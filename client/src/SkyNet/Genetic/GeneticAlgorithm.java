package SkyNet.Genetic;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;
import static java.util.Collections.swap;

public class GeneticAlgorithm {
    private static Random rnd = new Random();

    public static PopulationMember GenerateSolution(List<Gene> genes) {
        return GenerateSolution(100, genes, 0.24, 0.5, 0.9, 25);
    }

    public static PopulationMember GenerateSolution(int populationCount, List<Gene> genes,
                                                    double fstParentPopulationRange,
                                                    double sndParentPopulationRange,
                                                    double mutationChance,
                                                    int stopCount) {
        PopulationMember[] initialPopulation = new PopulationMember[populationCount];
        for (int i = 0; i < populationCount; i++) {
            LinkedList<Gene> xs = new LinkedList<>(genes);
            shuffle(xs);
            initialPopulation[i] = new PopulationMember(xs);
        }

        return evolve(stopCount, (int) ((double) populationCount * fstParentPopulationRange),
          sndParentPopulationRange, mutationChance, 0, initialPopulation);
    }

    private static PopulationMember evolve(int minCorrectCount, int fstParentPopulationRange,
                                           double sndParentPopulationRange, double mutationChance,
                                           int correctCount, PopulationMember[] population) {
        if (minCorrectCount <= correctCount)
            return population[0];
        List<PopulationMember> newMembers = Arrays.stream(population)
          .limit(fstParentPopulationRange)
          .flatMap(fst -> {
              int sndIndex = (int) ((double) population.length * sndParentPopulationRange * rnd.nextDouble());
              return crossover(fst.genes, population[sndIndex].genes).stream()
                .map(childGenes -> mutate(childGenes, mutationChance));
          }).collect(Collectors.toList());

        newMembers.addAll(
          Arrays.stream(population)
            .limit(population.length - 2 * fstParentPopulationRange)
            .collect(Collectors.toList()));
        sort(newMembers);

        if(newMembers.get(0).score == population[0].score)
            correctCount++;
        else
            correctCount = 0;

        return evolve(minCorrectCount, fstParentPopulationRange,
          sndParentPopulationRange, mutationChance,
          correctCount, newMembers.toArray(new PopulationMember[newMembers.size()]));
    }

    private static List<List<Gene>> crossover(List<Gene> fstParent, List<Gene> sndParent) {
        int
          cutLen = (int) Math.ceil((double) fstParent.size() / 3.0),
          cutPoint = (int) (rnd.nextDouble() * ((double) fstParent.size() - cutLen));
        List<Gene>
          child1 = mixGenes(fstParent, sndParent, cutLen, cutPoint),
          child2 = mixGenes(sndParent, fstParent, cutLen, cutPoint);
        return new LinkedList<List<Gene>>(){{
            add(child1);
            add(child2);
        }};
    }

    private static PopulationMember mutate(List<Gene> genes, double mutationPercent) {
        if (rnd.nextDouble() > mutationPercent) {
            List<Gene> xs = new LinkedList<>(genes);
            int
              index1 = rnd.nextInt(genes.size()),
              index2 = rnd.nextInt(genes.size());
            if (index1 == index2)
                if (index1 > 0) index1--;
                else index1++;
            swap(xs, index1, index2);
            return new PopulationMember(xs);
        } else return new PopulationMember(genes);
    }

    private static List<Gene> mixGenes(List<Gene> fstParent, List<Gene> sndParent, int cutLength, int cutPoint) {
        List<Gene>
          fstPart = fstParent.stream().skip(cutPoint).limit(cutLength).collect(Collectors.toList()),
          remainingFromSnd = sndParent.stream().filter(x -> !fstPart.contains(x)).collect(Collectors.toList()),
          result = remainingFromSnd.stream().limit(cutPoint).collect(Collectors.toList());
        result.addAll(fstPart);
        result.addAll(remainingFromSnd.stream().skip(cutPoint).collect(Collectors.toList()));
        return result;
    }

}
