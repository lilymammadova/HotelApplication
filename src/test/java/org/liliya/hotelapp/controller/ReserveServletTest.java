package org.liliya.hotelapp.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.service.ApartmentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReserveServletTest extends BaseServletTest {
    @Mock
    private ApartmentService apartmentService;
    @InjectMocks
    private ReserveServlet reserveServlet;

    @Test
    public void givenClientName_whenReserveApartment_thenMethodForwardsToSuccessJspPage() throws ServletException, IOException {
        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);
        when(request.getParameter("clientName")).thenReturn("Emily");
        when(apartmentService.reserve(any())).thenReturn(true);

        reserveServlet.doPost(request, response);

        verify(request).setAttribute("message", "Apartment successfully reserved for Emily");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenMissingName_whenReserveApartment_thenMethodForwardsToErrorJspPage() throws ServletException, IOException {
        when(request.getParameter("clientName")).thenReturn("");
        when(request.getRequestDispatcher("/jsp/error.jsp")).thenReturn(dispatcher);

        reserveServlet.doPost(request, response);

        verify(request).setAttribute("error", "Client name is required.");
        verify(dispatcher).forward(request, response);
    }

}
