package Part2;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * this class represents an index:(i,j) in a 2D array.
 * the class will store i and j values and the value of the cell[i][j] of the 2D array.
 * it will also provide a hash code that produced using the data members,
 * and the ability to compare one index to another.
 */
public class Index implements Serializable , Comparable {
    int row, column;
    Integer value = null;

    /**
     * common constructor.
     * @param oRow the i value of a Specific cell in the matrix.
     * @param oColumn the j value of a Specific cell in the matrix.
     */
    public Index(int oRow, int oColumn){
        this.row = oRow;
        this.column = oColumn;
    }

    /**
     * common constructor.
     * @param oRow the i value of a Specific cell in the matrix.
     * @param oColumn the j value of a Specific cell in the matrix.
     * @param oValue the i value the 2D[i][j].
     */
    public Index(int oRow, int oColumn, Integer oValue){
        this(oRow,oColumn);
        this.value = oValue;
    }

    /**
     * a common toString override.
     * @return a string representation of THIS.
     */
    @Override
    public String toString(){
        return "(" + row + "," + column + ")";
    }

    /**
     * allows to decide if this object equals to another object.
     * @param o an Object that can be Index or any other object.
     * @return an indication if THIS equals ti the provided object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return row == index.row &&
                column == index.column;
    }

    /**
     * common override of hashCode. hashing by i,j class members.
     * @return a hash code of i,j class members.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * common getter.
     * @return the j value of the index.
     */
    public int getRow() {
        return row;
    }

    /**
     * common getter.
     * @return the i value of the index.
     */
    public int getColumn() {
        return column;
    }

    /**
     * common getter.
     * @return the value of the cell in the 2D array that represented by THIS index.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * compares this index to another index by their hash code.
     * @param o an object that will be compared to THIS.
     * @return 1 if given object has the same hash code as THIS, -1 if THIS has "smaller" hash code.
     * 1 if Foreign object has "bigger" hash code.
     * the -1 and 1 options are useless.
     */
    @Override
    public int compareTo(@NotNull Object o) {
        return Integer.compare(this.hashCode(),o.hashCode());
    }
}