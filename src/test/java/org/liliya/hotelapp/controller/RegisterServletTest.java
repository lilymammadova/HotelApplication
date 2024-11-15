package org.liliya.hotelapp.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.service.ApartmentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegisterServletTest extends BaseServletTest {
    @Mock
    private ApartmentService apartmentService;
    @InjectMocks
    private RegisterServlet registerServlet;

    @Test
    public void givenCorrectPriceFormat_whenRegisterApartment_thenMethodForwardsToSuccessJspPage() throws ServletException, IOException {
        when(request.getParameter("price")).thenReturn("200");
        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);

        registerServlet.doPost(request, response);

        verify(request).setAttribute("message", "Apartment successfully registered.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenMissingPrice_whenRegisterApartment_thenMethodForwardsToErrorJspPage() throws ServletException, IOException {
        when(request.getParameter("price")).thenReturn("");
        when(request.getRequestDispatcher("/jsp/error.jsp")).thenReturn(dispatcher);

        registerServlet.doPost(request, response);

        verify(request).setAttribute("error", "Price is required.");
        verify(dispatcher).forward(request, response);
    }

}

