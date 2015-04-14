package SkyNet.model;

import java.util.LinkedList;
import java.util.List;

/**
 * HTN Method
 * has: (name, precondition, reduction)
 */
public abstract class Method {
    private final List<Atom> preconditions;
    private final List<Method> reduction;

    public Method(List<Atom> preconditions, List<Method> reduction){
        this.preconditions = preconditions;
        this.reduction = reduction;
    }

    public Method(List<Atom> preconditions) {
        this.preconditions = preconditions;
        this.reduction = new LinkedList<>(); //empty list
    }

    public boolean PreconditionsHold(List<Atom> state){
        return preconditions.stream().allMatch(); //TODO: lambda stuff
    }

    //reduction
    public List<Method> Decompose(List<Atom> state){
        //check if preconditions holds in state
        //if they do then return reduction

        return null;
    }
}
