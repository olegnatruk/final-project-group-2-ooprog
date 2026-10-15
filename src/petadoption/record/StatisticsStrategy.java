package petadoption.record;

import java.util.List;

import petadoption.application.AdoptionApplication;

/**
 * Strategy interface for computing adoption statistics.
 *
 * <p>Concrete strategies define how applications and records are aggregated
 * over a given timeframe (e.g. monthly or yearly).
 */
public interface StatisticsStrategy {

    StatisticsReport generate(List<AdoptionApplication> applications, List<AdoptionRecord> records);

    String getPeriodLabel();
}
