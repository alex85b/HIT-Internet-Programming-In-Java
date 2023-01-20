package Part2;


/**
 * Utility Class that provides BFS discovery of a vertex.
 */
public class bfsVertex {

    private Index Parent = null;
    private VertexStatus Status = VertexStatus.notVisited;
    private Integer Distance = null;

    /**
     * common getter.
     * @return the parent of THIS vertex. parent is the vertex from which BFS traverse arrived to THIS.
     */
    public Index getParent() {
        return Parent;
    }

    /**
     * common getter.
     * @return one of the three possible statuses ={Not Visited, Visited, Done}
     */
    public VertexStatus getStatus() {
        return Status;
    }

    /**
     * common getter.
     * @return the distance(in integer) of THIS, from the vertex from which BFS traverse begun.
     */
    public Integer getDistance() {
        return Distance;
    }

    /**
     * common setter.
     * @param parent the parent of THIS vertex. parent is the vertex from which BFS traverse arrived to THIS.
     */
    public void setParent(Index parent) {
        Parent = parent;
    }

    /**
     * common setter.
     * @param status one of the three possible statuses ={Not Visited, Visited, Done}
     */
    public void setStatus(VertexStatus status) {
        Status = status;
    }

    /**
     * common setter.
     * @param distance the distance(in integer) of THIS, from the vertex from which BFS traverse begun.
     */
    public void setDistance(Integer distance) {
        Distance = distance;
    }
}
