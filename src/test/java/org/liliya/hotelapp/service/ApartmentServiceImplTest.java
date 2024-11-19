package org.liliya.hotelapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;
import org.liliya.hotelapp.persistence.DatabaseConnection;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {
    @Mock
    private DatabaseConnection databaseConnection;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    private ApartmentServiceImpl apartmentService;

    @BeforeEach
    void setUp() throws SQLException {
        apartmentService = new ApartmentServiceImpl(databaseConnection);
        when(databaseConnection.getConnection()).thenReturn(connection);
    }

    @Test
    void register_ShouldReturnGeneratedId() throws SQLException {
        double price = 200.0;
        int generatedId = 1;

        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(generatedId);
        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);

        int result = apartmentService.register(price);

        assertEquals(generatedId, result);

        verify(preparedStatement).setDouble(1, price);
        verify(preparedStatement).setString(2, ReservationStatus.AVAILABLE.name());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void reserve_ShouldReturnTrue_WhenReservationSuccessful() throws SQLException {
        Client client = new Client("Test Client");
        int clientId = 1;
        int apartmentId = 2;

        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(clientId);
        when(resultSet.getInt("id")).thenReturn(apartmentId);

        boolean result = apartmentService.reserve(client);

        assertTrue(result);

        verify(connection).setAutoCommit(false);
        verify(connection).commit();
    }

    @Test
    void release_ShouldReturnTrue_WhenApartmentReleasedSuccessfully() throws SQLException {
        int apartmentId = 1;
        int clientId = 10;

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("client_id")).thenReturn(clientId);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = apartmentService.release(apartmentId);

        assertTrue(result);

        verify(preparedStatement).setInt(1, apartmentId);
        verify(preparedStatement, times(2)).executeUpdate();
        verify(connection).commit();
    }

    @Test
    void getPaginatedAndSortedApartments_ShouldReturnApartmentList() throws SQLException {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("client_id")).thenReturn(1);
        when(resultSet.getDouble("price")).thenReturn(300.0);
        when(resultSet.getString("status")).thenReturn(ReservationStatus.AVAILABLE.name());
        when(resultSet.getString("client_name")).thenReturn("Test Client");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<Apartment> apartments = apartmentService.getPaginatedAndSortedApartments(1, 10, "price");

        assertEquals(1, apartments.size());
        assertEquals(1, apartments.get(0).getId());
        assertEquals(300.0, apartments.get(0).getPrice());
        assertEquals("Test Client", apartments.get(0).getClient().getName());
        assertEquals(ReservationStatus.AVAILABLE, apartments.get(0).getReservationStatus());
    }
}