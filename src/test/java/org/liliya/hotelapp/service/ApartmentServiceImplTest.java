package org.liliya.hotelapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApartmentServiceImplTest {

    private final ApartmentService apartmentService = new ApartmentServiceImpl();

    @Test
    void givenPrice_WhenRegister_ThenApartmentIsAdded() {
        apartmentService.register(100);
        List<Apartment> apartments = apartmentService.getAllApartments();
        assertEquals(1, apartments.size());
        assertEquals(100, apartments.get(0)
                .getPrice());
        assertEquals(ReservationStatus.AVAILABLE, apartments.get(0)
                .getReservationStatus());
    }

    @Test
    void givenAvailableApartment_WhenReserve_ThenApartmentIsReserved() {
        Client client = new Client("client");
        apartmentService.register(100);
        boolean isReserved = apartmentService.reserve(client);
        assertTrue(isReserved);
        List<Apartment> apartments = apartmentService.getAllApartments();
        assertEquals(ReservationStatus.RESERVED, apartments.get(0)
                .getReservationStatus());
        assertEquals(client, apartments.get(0).getClient());
    }

    @Test
    void givenNoAvailableApartments_WhenTryingToReserve_ThenReturnFalse() {
        apartmentService.register(100);
        apartmentService.register(150);
        apartmentService.reserve(new Client("client_one"));
        apartmentService.reserve(new Client("client_two"));
        boolean isReserved = apartmentService.reserve(new Client("client_three"));
        assertFalse(isReserved);
    }

    @Test
    void givenReservedApartment_WhenRelease_ThenApartmentIsAvailable() {
        Client client = new Client("client");
        apartmentService.register(200);
        apartmentService.reserve(client);
        int apartmentId = apartmentService.getAllApartments().get(0).getId();
        boolean isReleased = apartmentService.release(apartmentId);
        assertTrue(isReleased);
        List<Apartment> apartments = apartmentService.getAllApartments();
        assertEquals(ReservationStatus.AVAILABLE, apartments.get(0)
                .getReservationStatus());
        assertNull(apartments.get(0).getClient());
    }

    @Test
    void givenInvalidApartmentId_WhenTryingToRelease_ThenReturnFalse() {
        apartmentService.register(100);
        int invalidId = 1000;
        boolean isReleased = apartmentService.release(invalidId);
        assertFalse(isReleased);
    }

    @Test
    void givenApartments_WhenPaginatedAndSorted_ThenReturnsCorrectPage() {
        registerApartments(200, 150, 400, 50, 70);
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(1, 2, Comparator.comparing(Apartment::getPrice));
        assertEquals(2, sortedApartments.size());
        assertEquals(50, sortedApartments.get(0).getPrice());
        assertEquals(70, sortedApartments.get(1).getPrice());
    }

    @Test
    void givenApartments_WhenGetPaginatedAndSortedWithIncompletePage_ThenReturnsPartialPage() {
        registerApartments(100, 200, 50, 60, 70);
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(2, 3, Comparator.comparing(Apartment::getPrice));
        assertEquals(2, sortedApartments.size());
        assertEquals(100, sortedApartments.get(0).getPrice());
        assertEquals(200, sortedApartments.get(1).getPrice());
    }

    @Test
    void givenNoApartments_WhenGetPaginatedAndSorted_ThenReturnsEmptyList() {
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(1, 5, Comparator.comparing(Apartment::getPrice));
        assertEquals(0, sortedApartments.size());
    }

    @Test
    void givenPageOutOfRange_WhenGetPaginatedAndSorted_ThenReturnsEmptyList() {
        registerApartments(100, 200, 50, 60, 70);
        List<Apartment> sortedApartments = apartmentService
                .getPaginatedAndSortedApartments(2, 10, Comparator.comparing(Apartment::getPrice));
        assertTrue(sortedApartments.isEmpty());
    }

    private void registerApartments(double... prices) {
        for (double price : prices) {
            apartmentService.register(price);
        }
    }
}