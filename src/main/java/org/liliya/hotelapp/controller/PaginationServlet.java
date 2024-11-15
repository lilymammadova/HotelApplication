package org.liliya.hotelapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.liliya.hotelapp.model.Apartment;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class PaginationServlet extends AppServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pageStr = request.getParameter("pageNumber");
        String sizeStr = request.getParameter("pageSize");
        String sortBy = request.getParameter("sortParam");

        if (pageStr == null || sizeStr == null || sortBy == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Page, size, and sortBy parameters are required.");
            return;
        }
        try {
            int page = Integer.parseInt(pageStr);
            int size = Integer.parseInt(sizeStr);

            Comparator<Apartment> comparator = getComparator(sortBy);
            if (comparator == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println(sortBy);
                response.getWriter().write("Invalid sort field.");
                return;
            }
            List<Apartment> apartments = apartmentService.getPaginatedAndSortedApartments(page, size, comparator);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(apartments));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Page and size must be integers.");
        }
    }

    private Comparator<Apartment> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "apartmentid" -> Comparator.comparing(Apartment::getId);
            case "price" -> Comparator.comparing(Apartment::getPrice);
            case "availability" -> Comparator.comparing(Apartment::getReservationStatus);
            case "clientname" ->
                    Comparator.comparing(apartment -> apartment.getClient() != null ? apartment.getClient().getName() : "");
            default -> null;
        };
    }
}
