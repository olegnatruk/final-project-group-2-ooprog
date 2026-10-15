package petadoption.record;

import java.util.List;

import petadoption.application.AdoptionApplication;
import petadoption.application.ApplicationService;
import petadoption.auth.SessionManager;
import petadoption.core.DataStore;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;

/**
 * Strategy context class for generating adoption statistics reports.
 * Holds a {@link StatisticsStrategy} and allows swapping it at runtime via {@link #setStrategy(StatisticsStrategy)}.
 */
public class StatisticsService {

    private StatisticsStrategy strategy;
    private final SessionManager session = SessionManager.getInstance();
    private final ApplicationService applicationService = new ApplicationService();

    public StatisticsService() {
    }

    public StatisticsService(StatisticsStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(StatisticsStrategy strategy) {
        this.strategy = strategy;
    }

    public StatisticsStrategy getStrategy() {
        return strategy;
    }

    public StatisticsReport generateReport() throws PetAdoptionException {
        session.requireAdmin();

        if (strategy == null) {
            throw new ValidationException("Strategy", "No statistics strategy has been set.");
        }

        List<AdoptionApplication> applications;
        try {
            applications = applicationService.getAllApplications();
        } catch (UnsupportedOperationException e) {
            applications = List.of();
        }

        List<AdoptionRecord> records = DataStore.getInstance().records().findAll();

        return strategy.generate(applications, records);
    }
}
