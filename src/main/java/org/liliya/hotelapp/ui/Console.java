package org.liliya.hotelapp.ui;

import org.liliya.hotelapp.exception.ReservationException;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.persistence.StatePersistence;
import org.liliya.hotelapp.service.ApartmentService;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Console {
    private final ApartmentService apartmentService;
    private final Scanner scanner;
    private final StatePersistence<List<Apartment>> statePersistence;

    public Console(ApartmentService apartmentService, Scanner scanner, StatePersistence<List<Apartment>> statePersistence) {
        this.apartmentService = apartmentService;
        this.scanner = scanner;
        this.statePersistence = statePersistence;
    }

    public void start() {
        boolean isWork = true;
        while (isWork) {
            try {
                showMenu();
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> registerApartment();
                    case 2 -> reserveApartment();
                    case 3 -> releaseApartment();
                    case 4 -> getPaginatedAndSortedApartments();
                    case 5 -> {
                        saveStateAndExit();
                        isWork = false;
                    }
                    default -> System.out.println("\nInvalid option. Please try again.");
                }
            } catch (ReservationException e) {
                System.out.println("Reservation failed: " + e.getMessage());
            }

        }
    }

    private void registerApartment() {
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();

        apartmentService.register(price);
        System.out.println("\nApartment successfully registered");
    }


    private void reserveApartment() throws ReservationException {
        System.out.print("Enter client name: ");
        String clientName = scanner.nextLine();

        Client client = new Client(clientName);
        boolean reserved = apartmentService.reserve(client);
        if (!reserved) {
            throw new ReservationException("No available apartments found.");
        }

        System.out.println("\nApartment reserved successfully for " + client);
    }

    private void releaseApartment() throws ReservationException {
        System.out.print("Enter apartment's id: ");
        int id = scanner.nextInt();
        boolean released = apartmentService.release(id);
        if (!released) {
            throw new ReservationException("No apartments found with such id.");
        }
        System.out.println("Apartment successfully released");
    }

    private void getPaginatedAndSortedApartments() {
        System.out.print("Enter page number: ");
        int page = scanner.nextInt();

        System.out.print("Enter page size: ");
        int size = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Sort by (id, price, status, name): ");
        String sortBy = scanner.nextLine();

        Comparator<Apartment> comparator = getComparator(sortBy);
        if (comparator == null) {
            System.out.println("Invalid sort field.");
            return;
        }

        List<Apartment> apartments = apartmentService.getPaginatedAndSortedApartments(page, size, comparator);
        System.out.println("\n--- Paginated and Sorted Apartments ---");
        apartments.forEach(System.out::println);
    }

    private void saveStateAndExit() {
        System.out.println("Storing state...");
        statePersistence.saveState(apartmentService.getAllApartments());
        System.out.println("State stored. Exiting...");
    }

    private Comparator<Apartment> getComparator(String sortBy) {
        Comparator<Apartment> comparator;

        switch (sortBy.toLowerCase()) {
            case "id" -> comparator = Comparator.comparing(Apartment::getId);
            case "price" -> comparator = Comparator.comparing(Apartment::getPrice);
            case "status" -> comparator = Comparator.comparing(Apartment::getReservationStatus);
            case "name" -> comparator = Comparator.comparing(apartment -> apartment.getClient().getName());
            default -> comparator = Comparator.comparing(Apartment::getId);
        }
        return comparator;
    }

    private void showMenu() {
        String menu = """
                \n--- Hotel application ---
                1. Register apartment
                2. Reserve an apartment
                3. Release an apartment
                4. Get apartments
                5. Save state and exit
                Select an option: """;

        System.out.print(menu);
    }
}
