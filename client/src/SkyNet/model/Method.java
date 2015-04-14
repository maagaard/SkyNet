package SkyNet.model;

import java.util.HashSet;
import java.util.Set;

/**
 * HTN Method
 * has: (name, precondition, reduction)
 */
public abstract class Method {
    private final Set<Atom> preconditions;
    private final Set<Method> reduction;

    public Method(Set<Atom> preconditions, Set<Method> reduction){
        this.preconditions = preconditions;
        this.reduction = reduction;
    }

    public Method(Set<Atom> preconditions) {
        this.preconditions = preconditions;
        this.reduction = new HashSet<>(); //empty list
    }

    public boolean PreconditionsHold(Set<Atom> state){
        return state.stream()
                    .filter(preconditions::contains)
                    .count() == preconditions.size();
    }

    public Set<Method> Decompose(Set<Atom> state){
        if(this.PreconditionsHold(state))
            return reduction;
        return null;
    }
}
