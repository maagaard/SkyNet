package SkyNet.model;

import java.util.Set;

public class Action extends Method {
    private final Set<Atom> reduction;

    public Action(Set<Atom> preconditions, Set<Atom> reduction) {
        super(preconditions);
        this.reduction = reduction;
    }

    public Set<Atom> Decompose() {
        return reduction;
    }
}
