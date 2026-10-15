package petadoption.application.search;

import petadoption.application.AdoptionApplication;
import petadoption.application.ApplicationStatus;
import petadoption.core.SearchStrategy;

/**
 * Strategy (concrete): filters applications by status.
 */
public class ApplicationStatusFilter implements SearchStrategy<AdoptionApplication> {

    private final ApplicationStatus status;

    public ApplicationStatusFilter(ApplicationStatus status) {
        this.status = status;
    }

    @Override
    public boolean matches(AdoptionApplication item) {
        return item != null && item.getStatus() == status;
    }
}
