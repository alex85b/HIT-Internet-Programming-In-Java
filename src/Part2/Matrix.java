package Part2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * this class represents a 2D array, aka "Matrix".
 */
public class Matrix implements Serializable {

    protected int[][] primitiveMatrix;
    protected int howManyRows = 0;

    /**
     * common constructor.
     * @param oArray primitive 2D array of integers.
     */
    public Matrix(int[][] oArray){
        primitiveMatrix = Arrays
                .stream(oArray)
                .map(row -> row.clone())
                .toArray(value -> new int[value][]);
        howManyRows = primitiveMatrix.length;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * this will offer an adjacent indices for given Index.
     * @param index an object of the type Index, that represents a "location" in 2D array, location is [i][j].
     * @return all the neighboring cells of given index,
     * locations from all sides including diagonals can be considered neighbours.
     */
    public Collection<Index> getAdjacentIndices(final Index index){
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{ // go down
            extracted = primitiveMatrix[index.row+1][index.column];
            list.add(new Index(index.row+1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ // go down right
            extracted = primitiveMatrix[index.row+1][index.column+1];
            list.add(new Index(index.row+1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ // go right
            extracted = primitiveMatrix[index.row][index.column+1];
            list.add(new Index(index.row,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ // go up + right
            extracted = primitiveMatrix[index.row-1][index.column+1];
            list.add(new Index(index.row-1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ // go up
            extracted = primitiveMatrix[index.row-1][index.column];
            list.add(new Index(index.row-1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ // go left + up
            extracted = primitiveMatrix[index.row-1][index.column-1];
            list.add(new Index(index.row-1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ // go left
            extracted = primitiveMatrix[index.row][index.column-1];
            list.add(new Index(index.row,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ // go left + down
            extracted = primitiveMatrix[index.row+1][index.column-1];
            list.add(new Index(index.row+1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }

    /**
     * common getter.
     * @return the amount of rows that given 2D array has.
     */
    public int getHowManyRows() {
        return howManyRows;
    }

    /**
     * return the value in the specific matrix cell.
     * @param index a location inside the matrix.
     * @return value of given location inside the matrix.
     */
    public int getValue(Index index) {
        try {
            return primitiveMatrix[index.row][index.column];
        } catch (Exception e){
            System.err.println("{Matrix::getValue} has failed");
            System.err.println(e.toString());
            throw new NoSuchElementException();
        }

    }
}