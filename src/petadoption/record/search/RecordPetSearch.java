package petadoption.record.search;

import petadoption.core.SearchStrategy;
import petadoption.exception.RecordNotFoundException;
import petadoption.pet.Pet;
import petadoption.pet.PetService;
import petadoption.record.AdoptionRecord;

/**
 * Strategy to match adoption records where the pet ID equals or pet name contains the keyword.
 */
public class RecordPetSearch implements SearchStrategy<AdoptionRecord> {

    private final String keyword;
    private final PetService petService;

    public RecordPetSearch(String keyword) {
        this(keyword, new PetService());
    }

    public RecordPetSearch(String keyword, PetService petService) {
        this.keyword = keyword == null ? "" : keyword.trim().toLowerCase();
        this.petService = petService;
    }

    @Override
    public boolean matches(AdoptionRecord item) {
        if (item == null || keyword.isEmpty()) {
            return false;
        }
        if (item.getPetId().toLowerCase().contains(keyword)) {
            return true;
        }
        try {
            Pet pet = petService.getPetById(item.getPetId());
            return pet.getName().toLowerCase().contains(keyword);
        } catch (RecordNotFoundException | UnsupportedOperationException e) {
            return false;
        }
    }
}
