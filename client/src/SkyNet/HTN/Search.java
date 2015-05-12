package SkyNet.HTN;

import SkyNet.Command;
import SkyNet.Genetic.Gene;
import SkyNet.Genetic.GeneticAlgorithm;
import SkyNet.Genetic.PopulationMember;
import SkyNet.model.Agent;
import SkyNet.model.Box;
import SkyNet.model.Level;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.stream.Collectors;

public class Search {

    private List<SubGoal> calcOptimalSubGoalOrder(Level lvl){
        //single agent for now
        Agent agent = lvl.agents.get(0);
        List<Gene> sgs = lvl.goals.stream().map(g ->
          lvl.boxes.stream()
            .filter(b -> b.name == g.name)
            .map(b -> new SubGoal(b, g))
            .min(SubGoal::compareTo).get())
            .collect(Collectors.toList());
        PopulationMember bestSubGoals = GeneticAlgorithm.GenerateSolution(agent.x, agent.y, sgs);
        return bestSubGoals.genes.stream()
          .map(g -> (SubGoal) g)
          .collect(Collectors.toList());
    }

    public List<Command> completeLevel(Level lvl){
        List<SubGoal> subGoals = calcOptimalSubGoalOrder(lvl);
        Box firstBox = subGoals.get(0).box;
        throw new NotImplementedException();
    }

}
