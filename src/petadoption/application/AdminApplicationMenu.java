package petadoption.application;

import java.util.ArrayList;
import java.util.List;

import petadoption.application.search.ApplicantNameSearch;
import petadoption.application.search.ApplicationPetSearch;
import petadoption.application.search.ApplicationStatusFilter;
import petadoption.core.AllOfStrategy;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.ui.Menu;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;
import petadoption.util.Validator;

/**
 * Admin menu for reviewing, approving, rejecting, revoking, deleting, and searching adoption applications.
 */
public class AdminApplicationMenu extends Menu {

    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected String getTitle() {
        return "Manage Adoption Applications";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
            "View all applications",
            "Review application",
            "Approve",
            "Reject",
            "Revoke approval",
            "Delete invalid application",
            "Search & filter applications",
            "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        // Options 1-7 all read application records.
        if (choice <= 7 && applicationService.getAllApplications().isEmpty()) {
            ConsoleUI.printInfo(ApplicationDisplay.NO_APPLICATIONS_MESSAGE);
            return true;
        }
        switch (choice) {
            case 1 -> {
                List<AdoptionApplication> list = applicationService.getAllApplications();
                ApplicationDisplay.printApplicationTable(list);
                InputHelper.pause();
            }
            case 2 -> {
                String id = InputHelper.readId("Enter Application ID (e.g. APP0001): ", "APP");
                AdoptionApplication app = applicationService.getApplicationById(id);
                ApplicationDisplay.printDetails(app);
                InputHelper.pause();
            }
            case 3 -> approveFlow();
            case 4 -> rejectFlow();
            case 5 -> revokeFlow();
            case 6 -> deleteFlow();
            case 7 -> searchAndFilterFlow();
            case 8 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    private void approveFlow() throws PetAdoptionException {
        String id = InputHelper.readId("Enter Application ID to approve (e.g. APP0001): ", "APP");
        AdoptionApplication app = applicationService.getApplicationById(id);
        ApplicationDisplay.printDetails(app);

        if (!app.isPending()) {
            ConsoleUI.printError("Cannot approve an application with status " + app.getStatus().getLabel()
                    + ". Only pending applications can be approved.");
            return;
        }

        if (!InputHelper.confirm("approve application " + id)) {
            return;
        }

        int autoRejected = applicationService.approveApplication(id);
        ConsoleUI.printSuccess("Application " + id + " has been approved.");
        if (autoRejected > 0) {
            ConsoleUI.printInfo(autoRejected + " other pending application(s) for this pet were automatically rejected.");
        }
    }

    private void rejectFlow() throws PetAdoptionException {
        String id = InputHelper.readId("Enter Application ID to reject (e.g. APP0001): ", "APP");
        AdoptionApplication app = applicationService.getApplicationById(id);
        ApplicationDisplay.printDetails(app);

        if (!app.isPending()) {
            ConsoleUI.printError("Cannot reject an application with status " + app.getStatus().getLabel()
                    + ". Only pending applications can be rejected.");
            return;
        }

        RejectionReason reasonChoice = InputHelper.readEnum("Select rejection reason:", RejectionReason.values());
        String finalReason;
        if (reasonChoice == RejectionReason.OTHER) {
            finalReason = InputHelper.readValidated("Enter specific reason (5-200 characters): ",
                    input -> Validator.requireLength("Rejection reason", input, 5, 200));
        } else {
            finalReason = reasonChoice.getLabel();
        }

        if (!InputHelper.confirm("reject application " + id)) {
            return;
        }

        applicationService.rejectApplication(id, finalReason);
        ConsoleUI.printSuccess("Application " + id + " rejected. Reason: " + finalReason);
    }

    private void revokeFlow() throws PetAdoptionException {
        String id = InputHelper.readId("Enter Application ID to revoke approval (e.g. APP0001): ", "APP");
        AdoptionApplication app = applicationService.getApplicationById(id);
        ApplicationDisplay.printDetails(app);

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            ConsoleUI.printError("Cannot revoke approval for an application with status "
                    + app.getStatus().getLabel() + ". Only approved applications can have approval revoked.");
            return;
        }

        RejectionReason reasonChoice = InputHelper.readEnum("Select reason for revocation:", RejectionReason.values());
        String finalReason;
        if (reasonChoice == RejectionReason.OTHER) {
            finalReason = InputHelper.readValidated("Enter specific reason (5-200 characters): ",
                    input -> Validator.requireLength("Revocation reason", input, 5, 200));
        } else {
            finalReason = reasonChoice.getLabel();
        }

        if (!InputHelper.confirm("revoke approval for application " + id)) {
            return;
        }

        applicationService.revokeApproval(id, finalReason);
        ConsoleUI.printSuccess("Approval for application " + id + " has been revoked. Pet returned to Available.");
    }

    private void deleteFlow() throws PetAdoptionException {
        String id = InputHelper.readId("Enter Application ID to delete (e.g. APP0001): ", "APP");
        AdoptionApplication app = applicationService.getApplicationById(id);
        ApplicationDisplay.printDetails(app);

        if (app.getStatus() == ApplicationStatus.APPROVED) {
            ConsoleUI.printError("Approved applications cannot be deleted. Revoke approval first.");
            return;
        }

        if (!InputHelper.confirm("delete application " + id)) {
            return;
        }

        applicationService.deleteApplication(id);
        ConsoleUI.printSuccess("Application " + id + " deleted.");
    }

    private void searchAndFilterFlow() throws PetAdoptionException {
        ConsoleUI.printHeader("Search & Filter Applications");
        System.out.println("  1. Filter by status");
        System.out.println("  2. Search by applicant name");
        System.out.println("  3. Search by pet (ID or name)");
        System.out.println("  4. Combined search & filter");
        System.out.println("  5. Back");

        int choice = InputHelper.readMenuChoice(5);
        SearchStrategy<AdoptionApplication> strategy = null;

        switch (choice) {
            case 1 -> {
                ApplicationStatus status = InputHelper.readEnum("Select status:", ApplicationStatus.values());
                strategy = new ApplicationStatusFilter(status);
            }
            case 2 -> {
                String kw = InputHelper.readText("Enter applicant name keyword: ", 1, 50);
                strategy = new ApplicantNameSearch(kw);
            }
            case 3 -> {
                String kw = InputHelper.readText("Enter pet ID or name keyword: ", 1, 50);
                strategy = new ApplicationPetSearch(kw);
            }
            case 4 -> {
                List<SearchStrategy<AdoptionApplication>> filters = new ArrayList<>();
                if (InputHelper.readYesNo("Filter by status?")) {
                    ApplicationStatus status = InputHelper.readEnum("Select status:", ApplicationStatus.values());
                    filters.add(new ApplicationStatusFilter(status));
                }
                if (InputHelper.readYesNo("Filter by applicant name?")) {
                    String kw = InputHelper.readText("Applicant name keyword: ", 1, 50);
                    filters.add(new ApplicantNameSearch(kw));
                }
                if (InputHelper.readYesNo("Filter by pet?")) {
                    String kw = InputHelper.readText("Pet ID or name keyword: ", 1, 50);
                    filters.add(new ApplicationPetSearch(kw));
                }
                if (filters.isEmpty()) {
                    ConsoleUI.printInfo("No filters selected.");
                    return;
                }
                strategy = new AllOfStrategy<>(filters);
            }
            case 5 -> {
                return;
            }
        }

        if (strategy != null) {
            List<AdoptionApplication> results = applicationService.search(strategy);
            ApplicationDisplay.printApplicationTable(results);
            InputHelper.pause();
        }
    }
}
