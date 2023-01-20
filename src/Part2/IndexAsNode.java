package Part2;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * this class will both encapsulate Index and represent a Node which will be part of a graph.
 * this class is bot Comparable with other Nodes and Serializable,
 * this class also Implements the INode interface and as such, it implements a wrapping and un wrapping methods.
 */
public class IndexAsNode implements INode<Index>, Comparable, Serializable {

    private Index wrapped = null;
    private UUID id = null;

    /**
     * generic constructor.
     */
    public IndexAsNode() {}

    /**
     * common constructor.
     * @param input an index that will be wrapped in THIS.
     *              wrapping will generate an UUID which will be held in a class member.
     */
    public IndexAsNode(Index input) {
        this.id = wrap(input);
    }

    /**
     * the method will wrap an index and will generate an UUID for the index that will allow to identify the node.
     * @param wouldBeVertex an index.
     * @return unique ID of given index.
     */
    @Override
    public UUID wrap(@NotNull Index wouldBeVertex) {
        this.wrapped = wouldBeVertex;
        this.id = UUID.nameUUIDFromBytes(wouldBeVertex.toString().getBytes());
        return this.id;
    }

    /**
     * common getter.
     * @return the index that contained within the node.
     */
    @Override
    public Index unWrap() {
        if(wrapped != null) return wrapped;
        else throw new NullPointerException();
    }

    /**
     * a common toString override.
     * @return a String representation of THIS, that consist of {Index representation + UUID}.
     */
    @Override
    public String toString() {
        return "indexAsNode{" +
                "wrapped=" + wrapped +
                ", id=" + id +
                '}';
    }

    /**
     * common getter.
     * @return the UUID that represent the inner Index that wrapped inside THIS.
     */
    public UUID getId() {
        return id;
    }

    /**
     * this method allows to compare THIS to another object which may or may not be Index as Node.
     * @param o non null object.
     * @return 0 if the foreign object is index-as-node and also has the same UUID as THIS.
     * if its not an Index-as-node return 2, other cases (-1,1) are irrelevant and generic.
     */
    @Override
    public int compareTo(@NotNull Object o) {
        try {
            IndexAsNode other = (IndexAsNode) o;
            return this.id.compareTo( other.getId() );
        }catch (Exception e){
            return 2;
        }
    }

    /**
     * generates an hash code that represents THIS.
     * @return a hash code of the UUID that represents the wrapped index.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * indicates if this object is equal to the provided object, equality is decided by UUID.
     * @param obj an object for comparison.
     * @return true if equal false if different.
     */
    @Override
    public boolean equals(Object obj) {
        try {
            IndexAsNode o = (IndexAsNode) obj;
            if (this.id.compareTo(o.getId()) == 0) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    };
}
