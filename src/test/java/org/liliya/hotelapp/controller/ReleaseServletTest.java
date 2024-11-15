package org.liliya.hotelapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class ReleaseServletTest extends BaseServletTest {
    @Mock
    private ApartmentService apartmentService;
    @InjectMocks
    private ReleaseServlet releaseServlet;

    @Test
    public void givenApartmentId_WhenReleaseApartment_thenMethodForwardsToSuccessJspPage() throws ServletException, IOException {
        when(request.getParameter("apartmentId")).thenReturn("1");
        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);
        when(apartmentService.release(1)).thenReturn(true);

        releaseServlet.doPost(request, response);
        verify(request).setAttribute("message", "Apartment successfully released.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenMissingApartmentId_whenReleaseApartment_thenMethodForwardsToErrorJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("apartmentId")).thenReturn("");
        when(request.getRequestDispatcher("/jsp/error.jsp")).thenReturn(dispatcher);

        releaseServlet.doPost(request, response);

        verify(request).setAttribute("error", "Apartment id  is required.");
        verify(dispatcher).forward(request, response);
    }

}
