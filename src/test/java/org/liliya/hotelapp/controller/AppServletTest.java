package org.liliya.hotelapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppServletTest {

    @Mock
    private ApartmentService apartmentService;
    @Mock
    private StatePersistence<List<Apartment>> statePersistence;
    @InjectMocks
    private AppServlet appServlet;

    @Test
    public void givenIndexJspPageRequested_WhenCallingDoGetMethod_ThenMethodForwardsToIndexJspPage() throws ServletException, IOException {
        AppServlet servlet = new AppServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenCorrectPriceFormat_whenRegisterApartment_thenMethodForwardsToSuccessJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("option")).thenReturn("registerApartment");
        when(request.getParameter("price")).thenReturn("200");
        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);

        appServlet.doPost(request, response);

        verify(request).setAttribute("message", "Apartment successfully registered.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenMissingPrice_whenRegisterApartment_thenMethodForwardsToErrorJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("option")).thenReturn("registerApartment");
        when(request.getParameter("price")).thenReturn("");
        when(request.getRequestDispatcher("/jsp/error.jsp")).thenReturn(dispatcher);

        appServlet.doPost(request, response);

        verify(request).setAttribute("error", "Price is required.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenClientName_whenReserveApartment_thenMethodForwardsToSuccessJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);
        when(request.getParameter("option")).thenReturn("reserveApartment");
        when(request.getParameter("clientName")).thenReturn("Emily");
        when(apartmentService.reserve(any())).thenReturn(true);

        appServlet.doPost(request, response);

        verify(request).setAttribute("message", "Apartment successfully reserved for Emily");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenMissingName_whenReserveApartment_thenMethodForwardsToErrorJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("option")).thenReturn("reserveApartment");
        when(request.getParameter("clientName")).thenReturn("");
        when(request.getRequestDispatcher("/jsp/error.jsp")).thenReturn(dispatcher);

        appServlet.doPost(request, response);

        verify(request).setAttribute("error", "Client name is required.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenApartmentId_WhenReleaseApartment_thenMethodForwardsToSuccessJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("option")).thenReturn("releaseApartment");
        when(request.getParameter("apartmentId")).thenReturn("1");
        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);
        when(apartmentService.release(1)).thenReturn(true);

        appServlet.doPost(request, response);
        verify(request).setAttribute("message", "Apartment successfully released.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenMissingApartmentId_whenReleaseApartment_thenMethodForwardsToErrorJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("option")).thenReturn("releaseApartment");
        when(request.getParameter("apartmentId")).thenReturn("");
        when(request.getRequestDispatcher("/jsp/error.jsp")).thenReturn(dispatcher);

        appServlet.doPost(request, response);

        verify(request).setAttribute("error", "Apartment id  is required.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenValidSortingParameters_whenSortApartments_thenMethodForwardsToSuccessJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("option")).thenReturn("getPaginatedAndSortedApartments");
        when(request.getParameter("pageNumber")).thenReturn("1");
        when(request.getParameter("pageSize")).thenReturn("10");
        when(request.getParameter("sortParam")).thenReturn("price");
        when(response.getWriter()).thenReturn(new PrintWriter("test"));

        Apartment apartment = new Apartment(1, 60, ReservationStatus.AVAILABLE, null);
        List<Apartment> apartments = Collections.singletonList(apartment);
        when(apartmentService.getPaginatedAndSortedApartments(anyInt(), anyInt(), any(Comparator.class)))
                .thenReturn(apartments);

        appServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
    }

    @Test
    public void givenValidRequest_whenSaveState_thenForwardToSuccessJspPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("option")).thenReturn("saveState");
        when(request.getRequestDispatcher("/jsp/success.jsp")).thenReturn(dispatcher);

        appServlet.doPost(request, response);

        verify(request).setAttribute("message", "State successfully saved.");
        verify(dispatcher).forward(request, response);
    }
}
