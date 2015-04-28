package SkyNet.Genetic;
import fj.Equal;
import fj.data.List;
import fj.Ord;
import fj.control.parallel.ParModule;
import java.util.Random;
import static fj.data.List.list;
import static java.util.Collections.shuffle;
import static java.util.Collections.swap;

//TODO: use immutable array for population
public class GeneticAlgorithm {
    private Random rnd = new Random();
    public GeneticAlgorithm(int populationCount, List<PopulationMemberGene> genes){
        List<PopulationMember> initialPop =
            List.range(0, populationCount)
                .map(i -> {
                    java.util.List<PopulationMemberGene> xs = genes.toJavaList();
                    shuffle(xs);
                    return new PopulationMember(list(xs));
                }).sort(Ord.intOrd.comap(x -> x.score));
    }

    private List<PopulationMemberGene> compose(List<PopulationMemberGene> genes1,
                                               List<PopulationMemberGene> genes2, int cutPoint){
        return genes2.take(cutPoint)
                .append(genes1)
                .append(genes2.drop(cutPoint));
    }

    private Kids crossover(PopulationMember mother, PopulationMember father){
        int cutLen = (int)Math.ceil((double) mother.genes.length() / 5.0);
        int cutPoint1 = (int)(rnd.nextDouble() * ((double)mother.genes.length() - cutLen));

        List<PopulationMemberGene> father_part =
                father.genes.drop(cutPoint1).take(cutLen);
        List<PopulationMemberGene> remaining_from_mother =
                mother.genes.minus(Equal.<PopulationMemberGene>anyEqual(), father_part);
        List<PopulationMemberGene> child1 =
                compose(father_part, remaining_from_mother, cutPoint1);

        List<PopulationMemberGene> mother_part =
                father.genes.drop(cutPoint1).take(cutLen);
        List<PopulationMemberGene> remaining_from_father =
                father.genes.minus(Equal.<PopulationMemberGene>anyEqual(), mother_part);
        List<PopulationMemberGene> child2 =
                compose(mother_part, remaining_from_father, cutPoint1);

        return new Kids(new PopulationMember(child1),
                new PopulationMember(child2));
    }

    private PopulationMember mutate(PopulationMember popMember, double mutationChance){
        if(rnd.nextDouble() > mutationChance){
            java.util.List<PopulationMemberGene> xs = popMember.genes.toJavaList();
            int index1 = rnd.nextInt(popMember.genes.length());
            int index2 = rnd.nextInt(popMember.genes.length());
            if (index1 == index2)
                if (index1 > 0) index1--;
                else index1++;
            swap(xs, index1, index2);
            return new PopulationMember(list(xs));
        } else return popMember;
    }

    /*
    private List<PopulationMember> iterate(double matingPopPercent, double mutationPercent,
                                           double matePercent,
                                           List<PopulationMember> population){
        List<Integer> test = list(1,2,3);
        int toMutate = (int)((double)population.length() * matePercent);
        List<PopulationMember> freshBlood =
                //ParModule.parModule().parMap(population.take(toMutate))
                population.take(toMutate).map(mother -> {
                    int father_index =  (int)(((double) population.length()) *
                            matingPopPercent * rnd.nextDouble());
                    Kids kids = crossover(mother, population.index(father_index));
                    PopulationMember child1 = mutate(kids.child1, mutationPercent);
                    PopulationMember child2 = mutate(kids.child2, mutationPercent);
                    return list(child1,child2);
                }).co;

        return null;
    }
    */

    private class Kids {
        private final PopulationMember child1;
        private final PopulationMember child2;
        private Kids(PopulationMember child1, PopulationMember child2) {
            this.child1 = child1;
            this.child2 = child2;
        }
    }

    private class PopulationMember {
        private final List<PopulationMemberGene> genes;
        private final int score;

        private PopulationMember(List<PopulationMemberGene> genes){
            this.genes = genes;
            this.score = score(0, genes);
        }

        private int score(int acc, List<PopulationMemberGene> popMemberGenes){
            if (popMemberGenes.length() == 1)
                return acc;
            else if(popMemberGenes.length() == 2)
                return acc + popMemberGenes.head().distanceTo(popMemberGenes.tail().head());
            else {
                return score(
                        acc + popMemberGenes.head().distanceTo(popMemberGenes.tail().head()),
                        popMemberGenes.tail());
            }
        }
    }
}
