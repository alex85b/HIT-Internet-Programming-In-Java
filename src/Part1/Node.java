package Part1;

import java.util.Collection;

/**
 * interface that represents a node which also has UUID and as such,it implements the HasUUID interface.
 */
public interface Node extends HasUUID {

    /**
     * generates a collection of nodes from a Class that extends HasUUID.
     * @param x some class that extends HasUUID.
     * @return return collection of Nodes.
     */
    public Collection<Node> getCollection(Class<? extends HasUUID> x);
    // Class<T extends HasUUID> x <--- Unexpected bound.
    // answer found in : https://www.journaldev.com/1663/java-generics-example-method-class-interface
    // "upper bounded wildcard is helpful.
    // We use generics wildcard with extends keyword
    // and the upper bound class or interface that will allow us to pass argument of upper bound"
}
