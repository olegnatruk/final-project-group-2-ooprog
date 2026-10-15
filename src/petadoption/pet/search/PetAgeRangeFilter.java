package petadoption.pet.search;

import petadoption.core.SearchStrategy;
import petadoption.pet.Pet;

/** Strategy (concrete): filters pets by an inclusive age range {@code minAge <= age <= maxAge}. */
public class PetAgeRangeFilter implements SearchStrategy<Pet> {

    private final int minAge;
    private final int maxAge;

    public PetAgeRangeFilter(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    @Override
    public boolean matches(Pet pet) {
        if (pet == null) {
            return false;
        }
        int age = pet.getAgeYears();
        return age >= minAge && age <= maxAge;
    }
}
