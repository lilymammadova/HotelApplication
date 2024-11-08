package org.liliya.hotelapp.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;
import org.liliya.hotelapp.service.ApartmentService;
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

    @BeforeEach
    void setUp() {
        console = new Console(apartmentService, scanner);
    }

    //Test for method registerApartment
    @Test
    void givenPrice_whenRegisterApartment_thenCallRegisterMethodWithCorrectPrice() {
        // Simulating the input for the apartment price
        when(scanner.nextInt()).thenReturn(1).thenReturn(5);
        when(scanner.nextDouble()).thenReturn(100.0);
        // Call the start method
        console.start();

        // Verifying that the register method was called with the correct price
        verify(apartmentService).register(100.0);
    }

    //Test for method reserveApartment
    @Test
    void givenAvailableApartment_WhenReserveApartment_ThenClientNameIsValid() {
        //Arrange
        String clientName = "client";
        Client client = new Client(clientName);

        //Simulate
        when(scanner.nextInt()).thenReturn(2).thenReturn(5);
        when(scanner.nextLine()).thenReturn("").thenReturn("client");
        when(apartmentService.reserve(client)).thenReturn(true);

        //Act
        console.start();

        //Assert
        verify(apartmentService).reserve(client);
    }

    // Test for method reserveApartment when exception is thrown
    @Test
    void givenNoAvailableApartments_whenTryingToReserve_thenServiceReturnsFalse() {
        // Arrange
        String clientName = "client";
        Client client = new Client(clientName);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        //Simulate
        when(scanner.nextInt()).thenReturn(2).thenReturn(5);
        when(scanner.nextLine()).thenReturn("").thenReturn("client");
        when(apartmentService.reserve(client)).thenReturn(false);

        //Act & Assert
        console.start();
        assertTrue(outContent.toString().contains("Reservation failed: No available apartments found."));

        verify(apartmentService).reserve(client);
    }

    // Test for method releaseApartment
    @Test
    void givenReservedApartment_whenReleaseApartment_thenIdIsValid() {
        //Arrange
        int apartmentId = 1;

        //Simulate
        when(scanner.nextInt()).thenReturn(3).thenReturn(apartmentId).thenReturn(5);

        //Act
        console.start();

        //Assert
        verify(apartmentService).release(apartmentId);
    }

    // Test for method releaseApartment when exception is thrown
    @Test
    void givenInvalidApartmentId_whenTryingToRelease_thenServiceReturnsFalse() {
        // Arrange
        int apartmentId = 999;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        //Simulate
        when(scanner.nextInt()).thenReturn(3).thenReturn(apartmentId).thenReturn(5);
        when(apartmentService.release(apartmentId)).thenReturn(false);

        //Act & Assert
        console.start();
        assertTrue(outContent.toString().contains("Reservation failed: No apartments found with such id."));
        verify(apartmentService).release(apartmentId);
    }

    // Test for method getPaginatedAndSortedApartments
    @Test
    void givenApartments_whenValidInputsProvided_thenGetSortedApartments() {
        // Arrange
        int page = 1;
        int size = 5;

        Apartment apartment1 = new Apartment(1, 50, ReservationStatus.RESERVED, new Client("John"));
        Apartment apartment2 = new Apartment(2, 40, ReservationStatus.RESERVED, new Client("Martin"));
        List<Apartment> apartments = Arrays.asList(apartment1, apartment2);

        // Simulate
        when(scanner.nextInt()).thenReturn(4).thenReturn(page).thenReturn(size);
        when(scanner.nextLine()).thenReturn("").thenReturn("price");
        when(apartmentService.getPaginatedAndSortedApartments(eq(page), eq(size), any(Comparator.class))).thenReturn(apartments);

        // Act
        console.start();

        // Assert
        verify(apartmentService).getPaginatedAndSortedApartments(eq(page), eq(size), any(Comparator.class));
    }
}
