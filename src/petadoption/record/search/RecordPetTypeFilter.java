package petadoption.record.search;

import petadoption.core.SearchStrategy;
import petadoption.exception.RecordNotFoundException;
import petadoption.pet.Pet;
import petadoption.pet.PetService;
import petadoption.pet.PetType;
import petadoption.record.AdoptionRecord;

/**
 * Filter adoption records where the adopted pet's type matches the given PetType.
 */
public class RecordPetTypeFilter implements SearchStrategy<AdoptionRecord> {

    private final PetType type;
    private final PetService petService;

    public RecordPetTypeFilter(PetType type) {
        this(type, new PetService());
    }

    public RecordPetTypeFilter(PetType type, PetService petService) {
        this.type = type;
        this.petService = petService;
    }

    @Override
    public boolean matches(AdoptionRecord item) {
        if (item == null || type == null) {
            return false;
        }
        try {
            Pet pet = petService.getPetById(item.getPetId());
            return pet.getType() == type;
        } catch (RecordNotFoundException e) {
            return false;
        }
    }
}
