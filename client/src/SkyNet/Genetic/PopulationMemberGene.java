package SkyNet.Genetic;

public interface PopulationMemberGene {
    public int distanceTo(PopulationMemberGene other);
    public boolean areEqual(PopulationMemberGene other);
}
