package petadoption.application.search;

import petadoption.application.AdoptionApplication;
import petadoption.core.SearchStrategy;

/**
 * Strategy (concrete): searches applications by applicant name (case-insensitive substring).
 */
public class ApplicantNameSearch implements SearchStrategy<AdoptionApplication> {

    private final String keyword;

    public ApplicantNameSearch(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean matches(AdoptionApplication item) {
        if (item == null || keyword == null || keyword.isBlank()) {
            return false;
        }
        return item.getApplicantName().toLowerCase().contains(keyword.trim().toLowerCase());
    }
}
