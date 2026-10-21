package petadoption.application.search;

import petadoption.application.AdoptionApplication;
import petadoption.core.SearchStrategy;
import petadoption.exception.RecordNotFoundException;
import petadoption.pet.Pet;
import petadoption.pet.PetService;

/**
 * Strategy (concrete): searches applications by pet ID or pet name (looks up pet via PetService).
 */
public class ApplicationPetSearch implements SearchStrategy<AdoptionApplication> {

    private final String keyword;
    private final PetService petService;

    public ApplicationPetSearch(String keyword) {
        this(keyword, new PetService());
    }

    public ApplicationPetSearch(String keyword, PetService petService) {
        this.keyword = keyword;
        this.petService = petService;
    }

    @Override
    public boolean matches(AdoptionApplication item) {
        if (item == null || keyword == null || keyword.isBlank()) {
            return false;
        }
        String cleanKeyword = keyword.trim().toLowerCase();
        if (item.getPetId().toLowerCase().contains(cleanKeyword)) {
            return true;
        }
        try {
            Pet pet = petService.getPetById(item.getPetId());
            if (pet != null && pet.getName() != null && pet.getName().toLowerCase().contains(cleanKeyword)) {
                return true;
            }
        } catch (RecordNotFoundException e) {
            // pet was deleted: match on the pet ID only
        }
        return false;
    }
}
