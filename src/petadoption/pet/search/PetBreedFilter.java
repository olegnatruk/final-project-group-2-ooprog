package petadoption.pet.search;

import petadoption.core.SearchStrategy;
import petadoption.pet.Pet;

/** Strategy (concrete): filters pets by breed containing the given text (case-insensitive, partial). */
public class PetBreedFilter implements SearchStrategy<Pet> {

    private final String breedKeyword;

    public PetBreedFilter(String breedKeyword) {
        this.breedKeyword = breedKeyword == null ? "" : breedKeyword.trim().toLowerCase();
    }

    @Override
    public boolean matches(Pet pet) {
        if (breedKeyword.isEmpty()) {
            return true;
        }
        return pet != null && pet.getBreed().toLowerCase().contains(breedKeyword);
    }
}
