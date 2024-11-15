package org.liliya.hotelapp.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.ReservationStatus;
import org.liliya.hotelapp.persistence.StatePersistence;
import org.liliya.hotelapp.service.ApartmentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SaveApartmentServletTest extends BaseServletTest {
    @Mock
    private ApartmentService apartmentService;
    @Mock
    private StatePersistence<List<Apartment>> statePersistence;
    @InjectMocks
    private SaveApartmentsServlet saveApartmentsServlet;

    @Test
    public void givenValidRequest_whenSaveState_thenForwardToSuccessJspPage() throws ServletException, IOException {
        Apartment apartment = new Apartment(1, 200, ReservationStatus.AVAILABLE, null);
        List<Apartment> apartments = List.of(apartment);
        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);
        when(apartmentService.getAllApartments()).thenReturn(apartments);

        saveApartmentsServlet.doPost(request, response);

        verify(request).setAttribute("message", "State successfully saved.");
        verify(dispatcher).forward(request, response);
        verify(statePersistence).saveState(apartments);
    }
}
