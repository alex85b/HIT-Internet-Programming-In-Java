package Part1;

import java.util.UUID;

/**
 *  this interface is a representation of a group of classes that have an UUID identifier.
 *  it forces the participants in said group, to return their UUID.
 */
public interface HasUUID {

    /**
     * this method returns an UUID object,
     * that is associated with the class that implemented the HasUUID interface.
     * @return an UUID.
     */
    public UUID getUUID ();
}
