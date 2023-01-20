package Part2;

import java.util.HashSet;

/**
 * this interface will be used to group utility classes for different graph Traverse solutions.
 * @param <T> the type to travers.
 */
public interface ITraverseUtility<T>  {

    /**
     * common getter.
     * @return all the vertices that were not traversed.
     */
    public HashSet<T> getRemainingVertices();

    /**
     * common setter.
     * @param Vertices group of vertices to traverse.
     */
    public void setRemainingVertices(HashSet<T> Vertices);
}
