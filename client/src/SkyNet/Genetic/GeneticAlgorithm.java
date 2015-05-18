package SkyNet.Genetic;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;
import static java.util.Collections.swap;

public class GeneticAlgorithm {
    private static Random rnd = new Random();

    public static PopulationMember GenerateSolution(int startX, int startY, List<Gene> genes) {
        if(genes.size() > 1)
            return GenerateSolution(startX, startY,
                    100, genes, 0.24, 0.5, 0.9, 25);
        else
            return new PopulationMember(startX, startY, genes);
    }

    public static PopulationMember GenerateSolution(int startX, int startY,
                                                    int populationCount, List<Gene> genes,
                                                    double fstParentPopulationRange,
                                                    double sndParentPopulationRange,
                                                    double mutationChance,
                                                    int stopCount) {
        PopulationMember[] initialPopulation = new PopulationMember[populationCount];
        for (int i = 0; i < populationCount; i++) {
            LinkedList<Gene> xs = new LinkedList<>(genes);
            shuffle(xs);
            initialPopulation[i] = new PopulationMember(startX, startY, xs);
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
              List<PopulationMember> kids = crossover(fst, population[sndIndex]);
              return kids.stream().map(childGenes -> mutate(childGenes, mutationChance));
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

    private static List<PopulationMember> crossover(PopulationMember fstParent, PopulationMember sndParent) {
        int
          cutLen = (int) Math.ceil((double) fstParent.genes.size() / 3.0),
          cutPoint = (int) (rnd.nextDouble() * ((double) fstParent.genes.size() - cutLen));
        PopulationMember
          child1 = mixGenes(fstParent, sndParent, cutLen, cutPoint),
          child2 = mixGenes(sndParent, fstParent, cutLen, cutPoint);
        return Arrays.asList(child1, child2);
    }

    private static PopulationMember mutate(PopulationMember victim, double mutationPercent) {
        if (rnd.nextDouble() > mutationPercent) {
            List<Gene> xs = new LinkedList<>(victim.genes);
            int
              index1 = rnd.nextInt(victim.genes.size()),
              index2 = rnd.nextInt(victim.genes.size());
            if (index1 == index2)
                if (index1 > 0) index1--;
                else index1++;
                swap(xs, index1, index2);
            return new PopulationMember(xs, victim);
        } else return victim;
    }

    private static PopulationMember mixGenes(PopulationMember fstParent, PopulationMember sndParent, int cutLength, int cutPoint) {
        List<Gene>
          fstPart = fstParent.genes.stream().skip(cutPoint).limit(cutLength).collect(Collectors.toList()),
          remainingFromSnd = sndParent.genes.stream().filter(x -> !fstPart.contains(x)).collect(Collectors.toList()),
          result = remainingFromSnd.stream().limit(cutPoint).collect(Collectors.toList());
        result.addAll(fstPart);
        result.addAll(remainingFromSnd.stream().skip(cutPoint).collect(Collectors.toList()));
        return new PopulationMember(result, fstParent);
    }

}
