package Part1;


import org.jetbrains.annotations.NotNull;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

/**
 * this class will create a link between given Type-name(as String) to unique id,
 * it provides the abilities { Encode a Type-name,
 *      Decode a given UUID to a Type-name,
 *      Compare one MyUUID object to another MyUUID object(by their MSBits) }
 */

public final class MyUUID implements Comparable<MyUUID>{

    // not extendable (1) means that no one needs a direct access to class members.
    private final String TypeName; // = Key, user input.
    private final String UniqueID; // = (Key + UUID), will be generated.
    private static final int lengthOfUUID = 36;

    /**
     * a typical constructor.
     * @param Key the string representation of the Name of given type.
     */
    public MyUUID (@NotNull final String Key) {
        this.TypeName = Key;
        this.UniqueID = Key + Encoder(Key).toString();
    }

    /**
     * an Utility method that allows to separate the UniqueID {Key + UUID} to its components.
     * @param input some MyUUID object.
     * @return a set of Type-name, UUID linked to the Type-name.
     */
    private static Map.Entry<String,UUID> splitID(MyUUID input) {
        String mixedID = input.getStringUUID();
        int TypeLength = mixedID.length() - MyUUID.lengthOfUUID;
        String Type = mixedID.substring(0, TypeLength);
        return new AbstractMap.SimpleImmutableEntry<String, UUID>(Type,UUID.nameUUIDFromBytes(Type.getBytes()));
    }

    /**
     * generates for given string (which represents a Type-name) an Unique id.
     * @param Key the string representation of the Name of given type.
     * @return a unique id represented by UUID object.
     */
    public static UUID Encoder (@NotNull final String Key) {
        try {
            return UUID.nameUUIDFromBytes(Key.getBytes());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * returns the Type-name that was linked by the provided MyUUID object.
     * @param myUUID a MyUUID object.
     * @return string, which represents a Type-name.
     */
    public static String Decoder (@NotNull final MyUUID myUUID) {
        return MyUUID.splitID(myUUID).getKey();
    }

    /**
     * a method that allows to receive the unique id that stored in THIS MyUUID.
     * @return the string that links a Type-name to its UUID object.
     */
    public String getStringUUID () {
        return this.UniqueID;
    }

    /**
     * a method that allows to receive the Type-name that stored in THIS MyUUID.
     * @return string, which represents a Type-name.
     */
    @Override
    public String toString() {
        return MyUUID.splitID(this).getKey();
    }

    /**
     * a method that compares the UUID of THIS MyUUID object,
     *      to UUID of another MyUUID object, by its "Most Significant Bits".
     * @param o a MyUUID object.
     * @return 0 if they are the same, -1 if this UUID considered "bigger",
     *      1 if foreign UUID considered "bigger".
     */
    @Override
    public int compareTo(@NotNull MyUUID o) {
        // NOTICE that UUID is compared by its "Most Significant Bits"
        //UUID left = MyUUID.splitID(this).getValue();
        //UUID right = MyUUID.splitID(o).getValue();
        //System.out.println("Left MSB:"+ left.getMostSignificantBits());
        //System.out.println("Right MSB:" + right.getLeastSignificantBits());
        //System.out.println("Left > Right ? "+ left.compareTo(right));
        //System.out.println("Right > Left ? "+ right.compareTo(left));
        return MyUUID.splitID(this).getValue().compareTo(MyUUID.splitID(o).getValue());
    }
}
