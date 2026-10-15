package petadoption.pet.search;

import petadoption.core.SearchStrategy;
import petadoption.pet.Pet;
import petadoption.pet.PetType;

/** Strategy (concrete): filters pets by their {@link PetType}. */
public class PetTypeFilter implements SearchStrategy<Pet> {

    private final PetType type;

    public PetTypeFilter(PetType type) {
        this.type = type;
    }

    @Override
    public boolean matches(Pet pet) {
        return pet != null && pet.getType() == type;
    }
}
