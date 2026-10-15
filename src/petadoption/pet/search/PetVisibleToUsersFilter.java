package petadoption.pet.search;

import petadoption.core.SearchStrategy;
import petadoption.pet.Pet;
import petadoption.pet.PetStatus;

/**
 * Strategy (concrete): matches pets that Regular Users are allowed to see (ADR-020: status is
 * {@link PetStatus#AVAILABLE} or {@link PetStatus#PENDING_ADOPTION}, i.e. not {@link PetStatus#ADOPTED}).
 */
public class PetVisibleToUsersFilter implements SearchStrategy<Pet> {

    @Override
    public boolean matches(Pet pet) {
        return pet != null && pet.getStatus() != PetStatus.ADOPTED;
    }
}
