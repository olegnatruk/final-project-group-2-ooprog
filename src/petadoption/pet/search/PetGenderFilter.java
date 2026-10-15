package petadoption.pet.search;

import petadoption.core.SearchStrategy;
import petadoption.pet.Gender;
import petadoption.pet.Pet;

/** Strategy (concrete): filters pets by {@link Gender}. */
public class PetGenderFilter implements SearchStrategy<Pet> {

    private final Gender gender;

    public PetGenderFilter(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean matches(Pet pet) {
        return pet != null && pet.getGender() == gender;
    }
}
