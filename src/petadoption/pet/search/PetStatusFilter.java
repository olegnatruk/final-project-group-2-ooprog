package petadoption.pet.search;

import petadoption.core.SearchStrategy;
import petadoption.pet.Pet;
import petadoption.pet.PetStatus;

/** Strategy (concrete): filters pets by their {@link PetStatus}. */
public class PetStatusFilter implements SearchStrategy<Pet> {

    private final PetStatus status;

    public PetStatusFilter(PetStatus status) {
        this.status = status;
    }

    @Override
    public boolean matches(Pet pet) {
        return pet != null && pet.getStatus() == status;
    }
}
