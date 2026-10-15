package petadoption.pet.search;

import petadoption.core.SearchStrategy;
import petadoption.pet.Pet;

/**
 * Strategy (concrete): searches pets by keyword matching name, breed, or type label (case-insensitive,
 * partial).
 */
public class PetKeywordSearch implements SearchStrategy<Pet> {

    private final String keyword;

    public PetKeywordSearch(String keyword) {
        this.keyword = keyword == null ? "" : keyword.trim().toLowerCase();
    }

    @Override
    public boolean matches(Pet pet) {
        if (keyword.isEmpty()) {
            return true;
        }
        if (pet == null) {
            return false;
        }
        String name = pet.getName().toLowerCase();
        String breed = pet.getBreed().toLowerCase();
        String type = pet.getType().getLabel().toLowerCase();
        return name.contains(keyword) || breed.contains(keyword) || type.contains(keyword);
    }
}
