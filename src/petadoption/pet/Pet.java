package petadoption.pet;

import java.time.LocalDate;

import petadoption.core.Storable;
import petadoption.exception.PetAdoptionException;

/**
 * Skeleton of Julyan's abstract Pet class.
 */
public abstract class Pet implements Storable {

    private final String petId;
    private final String name;
    private final String breed;

    protected Pet(String petId, String name, String breed) {
        this.petId = petId;
        this.name = name;
        this.breed = breed;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public abstract PetType getType();

    @Override
    public String getId() {
        return petId;
    }

    @Override
    public String toRecord() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
