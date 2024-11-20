package org.liliya.hotelapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.ReservationStatus;
import org.liliya.hotelapp.service.ApartmentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaginationServletTest extends BaseServletTest {
    @Mock
    private ApartmentService apartmentService;
    @InjectMocks
    private PaginationServlet paginationServlet;

    @Test
    public void givenValidSortingParameters_whenSortApartments_thenMethodForwardsToSuccessJspPage() throws IOException {
        when(request.getParameter("pageNumber")).thenReturn("1");
        when(request.getParameter("pageSize")).thenReturn("10");
        when(request.getParameter("sortParam")).thenReturn("price");
        when(response.getWriter()).thenReturn(new PrintWriter("test"));

        Apartment apartment = new Apartment(1, 60, ReservationStatus.AVAILABLE, null);
        List<Apartment> apartments = Collections.singletonList(apartment);
        when(apartmentService.getPaginatedAndSortedApartments(anyInt(), anyInt(), anyString()))
                .thenReturn(apartments);

        paginationServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
    }

}
