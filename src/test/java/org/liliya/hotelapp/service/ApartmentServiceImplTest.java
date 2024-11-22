package org.liliya.hotelapp.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Transaction transaction;
    @Mock
    private Session session;
    @Mock
    private Query query;

    private ApartmentService apartmentService;

    @BeforeEach
    void setUp() {
        apartmentService = new ApartmentServiceImpl(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }

    @Test
    void givenValidPrice_whenRegister_thenApartmentShouldBeAddedToDatabase() {
        double price = 200.0;
        int generatedId = 1;

        when(session.save(any(Apartment.class))).thenReturn(generatedId);
        when(session.beginTransaction()).thenReturn(transaction);

        int result = apartmentService.register(price);

        assertEquals(generatedId, result);

        verify(session).save(any(Apartment.class));
        verify(session).beginTransaction();
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void givenClientName_whenReserve_ThenClientShouldBeAddedToDatabase() {
        Client client = new Client("Test Client");
        int clientId = 1;
        int apartmentId = 2;

        Apartment availableApartment = new Apartment();
        availableApartment.setId(apartmentId);

        List<Apartment> availableApartments = new ArrayList<>();
        availableApartments.add(availableApartment);

        when(session.save(any(Client.class))).thenReturn(clientId);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createQuery(anyString(), eq(Apartment.class)))
                .thenReturn(query);
        when(query.setParameter("status", ReservationStatus.AVAILABLE)).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.getResultList()).thenReturn(availableApartments);
        when(session.get(Apartment.class, apartmentId)).thenReturn(availableApartment);
        when(session.merge(any(Apartment.class))).thenReturn(availableApartment);

        boolean result = apartmentService.reserve(client);

        assertTrue(result);

        verify(session).beginTransaction();
        verify(session).save(client);
        verify(session).createQuery("FROM Apartment where reservationStatus =:status", Apartment.class);
        verify(query).setParameter("status", ReservationStatus.AVAILABLE);
        verify(query).getResultList();
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void givenApartmentId_whenRelease_ThenClientShouldBeRemovedFromDatabase() {
        int apartmentId = 1;
        int clientId = 10;
        Client client = new Client(10, "Test Client");
        Apartment apartment = new Apartment(1, 200, ReservationStatus.RESERVED, client);

        when(session.get(Apartment.class, apartmentId)).thenReturn(apartment);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.get(Client.class, clientId)).thenReturn(client);
        doNothing().when(session).remove(client);
        when(session.merge(any(Apartment.class))).thenReturn(apartment);

        boolean result = apartmentService.release(1);

        assertTrue(result);

        verify(session).beginTransaction();
        verify(session).remove(client);
        verify(transaction).commit();
        verify(session).merge(apartment);
        verify(session).close();
    }

    @Test
    void givenApartments_WhenPaginatedAndSorted_ThenReturnsCorrectPage() {
        int page = 1;
        int size = 5;
        String sortBy = "price";
        Apartment apartment = new Apartment(1, 200, ReservationStatus.AVAILABLE, null);
        List<Apartment> apartments = new ArrayList<>();
        apartments.add(apartment);

        when(session.createQuery("FROM Apartment a LEFT JOIN FETCH a.client ORDER BY a." + sortBy, Apartment.class))
                .thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(5)).thenReturn(query);
        when(query.getResultList()).thenReturn(apartments);

        List<Apartment> result = apartmentService.getPaginatedAndSortedApartments(page, size, sortBy);

        assertEquals(1, result.size());
        assertEquals(200, result.get(0).getPrice());
        assertEquals(ReservationStatus.AVAILABLE, result.get(0).getReservationStatus());
    }
}