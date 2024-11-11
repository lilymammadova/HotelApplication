package org.liliya.hotelapp.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;
import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ConsoleTest {
    @Mock
    private ApartmentService apartmentService;
    @Mock
    private Scanner scanner;
    @InjectMocks
    private Console console;

    @Test
    void givenPrice_whenRegisterApartment_thenCallRegisterMethodWithCorrectPrice() {
        when(scanner.nextInt()).thenReturn(1).thenReturn(5);
        when(scanner.nextDouble()).thenReturn(100.0);
        console.start();
        verify(apartmentService).register(100.0);
    }

    @Test
    void givenAvailableApartment_WhenReserveApartment_ThenClientNameIsValid() {
        String clientName = "client";
        Client client = new Client(clientName);
        when(scanner.nextInt()).thenReturn(2).thenReturn(5);
        when(scanner.nextLine()).thenReturn("").thenReturn("client");
        when(apartmentService.reserve(client)).thenReturn(true);
        console.start();
        verify(apartmentService).reserve(client);
    }

    @Test
    void givenNoAvailableApartments_whenTryingToReserve_thenServiceReturnsFalse() {
        String clientName = "client";
        Client client = new Client(clientName);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        when(scanner.nextInt()).thenReturn(2).thenReturn(5);
        when(scanner.nextLine()).thenReturn("").thenReturn("client");
        when(apartmentService.reserve(client)).thenReturn(false);
        console.start();
        assertTrue(outContent.toString().contains("Reservation failed: No available apartments found."));
        verify(apartmentService).reserve(client);
    }

    @Test
    void givenReservedApartment_whenReleaseApartment_thenIdIsValid() {
        int apartmentId = 1;
        when(scanner.nextInt()).thenReturn(3).thenReturn(apartmentId).thenReturn(5);
        console.start();
        verify(apartmentService).release(apartmentId);
    }

    @Test
    void givenInvalidApartmentId_whenTryingToRelease_thenServiceReturnsFalse() {
        int apartmentId = 999;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        when(scanner.nextInt()).thenReturn(3).thenReturn(apartmentId).thenReturn(5);
        when(apartmentService.release(apartmentId)).thenReturn(false);
        console.start();
        assertTrue(outContent.toString().contains("Reservation failed: No apartments found with such id."));
        verify(apartmentService).release(apartmentId);
    }

    @Test
    void givenApartments_whenValidInputsProvided_thenGetSortedApartments() {
        int page = 1;
        int size = 5;
        Apartment apartment1 = new Apartment(1, 50, ReservationStatus.RESERVED, new Client("John"));
        Apartment apartment2 = new Apartment(2, 40, ReservationStatus.RESERVED, new Client("Martin"));
        List<Apartment> apartments = Arrays.asList(apartment1, apartment2);
        when(scanner.nextInt()).thenReturn(4).thenReturn(page).thenReturn(size);
        when(scanner.nextLine()).thenReturn("").thenReturn("price");
        when(apartmentService.getPaginatedAndSortedApartments(eq(page), eq(size), any(Comparator.class))).thenReturn(apartments);
        console.start();
        verify(apartmentService).getPaginatedAndSortedApartments(eq(page), eq(size), any(Comparator.class));
    }
}
