package Part2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * this class represents the board for a game of submarines.
 * the class inherits the functionality of MatrixAsGraph.
 */
public class submarines extends MatrixAsGraph{
    /**
     * common constructor.
     * will be used to build up submarines class using the MatrixAsGraph class.
     * @param input a binary 2D array that will be the base of Matrix which will be considered as Graph.
     */
    public submarines(int[][] input) {
        super(input);
    }

    /**
     * this method checks all the submarines on the game board and finds the amount of valid submarines.
     * @return number that represents the amount of valid submarines on the board.
     */
    public int howManySubmarines() {

        int SubmarinesAmount = 0;
        List<HashSet<IndexAsNode>> submarines = null;

        try{ // use Super::getConnectivity to discover all the submarines.
            submarines = super.getConnectivity(new bfsTraverse());
            if(submarines == null) throw new NullPointerException("submarines::none");
        } catch (Exception e){
            System.err.println("{submarines::howManySubmarines} submarine discovery has failed.");
            System.err.println(e.toString());
            return 0;
        }

        if (submarines.isEmpty()) return 0;
        for(HashSet submarine : submarines){
            try{ // for each submarine, run tests.
                if (submarine.size() < 2) continue; // failed part of rule (1) and (2)
                boolean abort = false;
                Iterator<IndexAsNode> dot = submarine.iterator();
                while(dot.hasNext()) { // if some Index of the submarine,
                    // is part of "L shape" or "\ shape", abort!.
                    Index test = dot.next().unWrap();
                    boolean hasUp = hasNeighbour(test,-1,0);
                    boolean hasDown = hasNeighbour(test,1,0);
                    boolean hasLeft = hasNeighbour(test,0,-1);
                    boolean hasRight = hasNeighbour(test,0,1);
                    boolean hasDownLeft = hasNeighbour(test,1,-1);
                    boolean hasDownRight = hasNeighbour(test,1,1);
                    boolean hasUpRight = hasNeighbour(test,-1,1);
                    boolean hasUpLeft = hasNeighbour(test,-1,-1);

                    abort = hasUpLeft && (!hasUp || !hasLeft);
                    if(abort) break;
                    abort = hasUpRight && (!hasUp || !hasRight);
                    if(abort) break;
                    abort = hasDownLeft && (!hasDown || !hasLeft);
                    if(abort) break;
                    abort = hasDownRight && (!hasDown || !hasRight);
                    if(abort) break;
                }
                if(abort) continue; // submarine is flawed.
                SubmarinesAmount++; // submarine is perfect! count it.

            } catch (Exception e){
                System.err.println("{submarines::howManySubmarines} forEach has failed: "+submarine.toString());
                System.err.println(e.toString());
                return SubmarinesAmount;
            }
        }
        return SubmarinesAmount;
    }

    /**
     * utility method, finds if give index has a neighbour in the base primitive 2D array.
     * @param ind the index we want to test.
     * @param i_Offset the offset in the X axis, in which we expect to find a neighbour.
     * @param j_Offset the offset in the Y axis, in which we expect to find a neighbour.
     * @return indication if there is a neighbour, in the offset from the given index.
     */
    private boolean hasNeighbour (Index ind , int i_Offset, int j_Offset) {
        try {
            int i = ind.row;
            int j = ind.column;
            if(local.primitiveMatrix[i+i_Offset][j+j_Offset] == 1)
                return true;
            return false;
        } catch (Exception ignored) {
            // no neighbour at that index.
            return false;
        }
    }
}
