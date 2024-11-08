package org.liliya.hotelapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApartmentServiceImplTest {
    private ApartmentService apartmentService;

    @BeforeEach
    void setUp() {
        apartmentService = new ApartmentServiceImpl();
    }

    //Test for  method register
    @Test
    void givenPrice_WhenRegister_ThenApartmentIsAdded() {
        //Arrange
        apartmentService.register(100);

        //Act
        List<Apartment> apartments = apartmentService.getAllApartments();

        //Assert
        assertEquals(1, apartments.size(), "Should be one apartment registered");
        assertEquals(100, apartments.get(0)
                .getPrice(), "The price of new registered apartment should be 100");
        assertEquals(ReservationStatus.AVAILABLE, apartments.get(0)
                .getReservationStatus(), "Reservation status of new registered apartment should be AVAILABLE");
    }

    //Test for method reserve
    @Test
    void givenAvailableApartment_WhenReserve_ThenApartmentIsReserved() {
        //Arrange
        Client client = new Client("client");
        apartmentService.register(100);

        //Act
        boolean isReserved = apartmentService.reserve(client);

        //Assert
        assertTrue(isReserved, "Apartment should be reserved successfully");

        List<Apartment> apartments = apartmentService.getAllApartments();

        assertEquals(ReservationStatus.RESERVED, apartments.get(0)
                .getReservationStatus(), "Apartment status should be RESERVED");
        assertEquals(client, apartments.get(0).getClient(), "Apartment should be reserved for the client");
    }

    // Test for negative result in reserve method
    @Test
    void givenNoAvailableApartments_WhenTryingToReserve_ThenReturnFalse() {
        //Arrange
        apartmentService.register(100);
        apartmentService.register(150);

        apartmentService.reserve(new Client("client_one"));
        apartmentService.reserve(new Client("client_two"));

        //Act & Assert
        boolean isReserved = apartmentService.reserve(new Client("client_three"));
        assertFalse(isReserved);
    }

    //Test for method release
    @Test
    void givenReservedApartment_WhenRelease_ThenApartmentIsAvailable() {
        //Arrange
        Client client = new Client("client");
        apartmentService.register(200);
        apartmentService.reserve(client);

        //Act
        int apartmentId = apartmentService.getAllApartments().get(0).getId();
        boolean isReleased = apartmentService.release(apartmentId);

        //Assert
        assertTrue(isReleased, "Apartment should be released successfully");

        List<Apartment> apartments = apartmentService.getAllApartments();
        assertEquals(ReservationStatus.AVAILABLE, apartments.get(0)
                .getReservationStatus(), "Apartment status should be RELEASED");
        assertNull(apartments.get(0).getClient(), "Apartment should not have a client");
    }

    // Test for negative result in release method
    @Test
    void givenInvalidApartmentId_WhenTryingToRelease_ThenReturnFalse() {
        //Arrange
        apartmentService.register(100);

        int invalidId = 1000;

        boolean isReleased = apartmentService.release(invalidId);
        //Act & Assert
        assertFalse(isReleased);
    }

    // Test for paginated and sorted method with full page
    @Test
    void givenApartments_WhenPaginatedAndSorted_ThenReturnsCorrectPage() {
        // Arrange
        registerApartments(200, 150, 400, 50, 70);

        //Act
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(1, 2, Comparator.comparing(Apartment::getPrice));

        //Assert
        assertEquals(2, sortedApartments.size(), "Page size should be 2");
        assertEquals(50, sortedApartments.get(0).getPrice(), "First apartment on the page should have the lowest price");
        assertEquals(70, sortedApartments.get(1).getPrice(), "Second apartment on the page should have the next lowest price");
    }

    // Test for last incomplete page
    @Test
    void givenApartments_WhenGetPaginatedAndSortedWithIncompletePage_ThenReturnsPartialPage() {

        // Arrange
        registerApartments(100, 200, 50, 60, 70);

        //Act
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(2, 3, Comparator.comparing(Apartment::getPrice));

        //Assert
        assertEquals(2, sortedApartments.size(), "Last page should contain two apartments");
        assertEquals(100, sortedApartments.get(0).getPrice(), "Price of first apartment in last page should be 100");
        assertEquals(200, sortedApartments.get(1).getPrice(), "Price of second apartment in last page should be 200");
    }

    // Test for empty list of apartments
    @Test
    void givenNoApartments_WhenGetPaginatedAndSorted_ThenReturnsEmptyList() {
        //Arrange

        //Act
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(1, 5, Comparator.comparing(Apartment::getPrice));

        //Assert
        assertEquals(0, sortedApartments.size(), "Sorted apartment list should be empty");
    }

    // Test for page out of range
    @Test
    void givenPageOutOfRange_WhenGetPaginatedAndSorted_ThenReturnsEmptyList() {
        // Arrange
        registerApartments(100, 200, 50, 60, 70);

        //Act
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(2, 10, Comparator.comparing(Apartment::getPrice));

        //Assert
        assertTrue(sortedApartments.isEmpty(), "When page is out of range should return empty list");
    }

    private void registerApartments(double... prices) {
        for (double price : prices) {
            apartmentService.register(price);
        }
    }
}