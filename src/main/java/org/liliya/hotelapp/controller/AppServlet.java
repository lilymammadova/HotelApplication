package org.liliya.hotelapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.liliya.hotelapp.configuration.Configuration;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.persistence.StatePersistence;
import org.liliya.hotelapp.persistence.StatePersistenceImpl;
import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AppServlet extends HttpServlet {
    private ApartmentService apartmentService;
    private StatePersistence<List<Apartment>> statePersistence;

    @Override
    public void init() throws ServletException {
        super.init();
        ObjectMapper objectMapper = new ObjectMapper();
        statePersistence = new StatePersistenceImpl<>(objectMapper);
        Configuration configuration = new Configuration();
        apartmentService = new ApartmentServiceImpl(statePersistence, configuration);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String option = request.getParameter("option");
        switch (option) {
            case "1" -> showRegisterForm(request, response);
            case "registerApartment" -> registerApartment(request, response);
            case "2" -> showReserveForm(request, response);
            case "reserveApartment" -> reserveApartment(request, response);
            case "3" -> showReleaseForm(request, response);
            case "releaseApartment" -> releaseApartment(request, response);
            case "4" -> showSortingForm(request, response);
            case "getPaginatedAndSortedApartments" -> getPaginatedAndSortedApartments(request, response);
            case "5" -> showSaveStateForm(request, response);
            case "saveState" -> saveState(request, response);
            default -> {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid option.");
            }
        }
    }

    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }

    private void registerApartment(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String priceStr = request.getParameter("price");
        if (priceStr == null || priceStr.isEmpty()) {
            request.setAttribute("error", "Price is required.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
            dispatcher.forward(request, response);
            return;
        }
        try {
            double price = Double.parseDouble(priceStr);
            apartmentService.register(price);
            request.setAttribute("message", "Apartment successfully registered.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/success.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid price format.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void showReserveForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/reserve.jsp").forward(request, response);
    }

    private void reserveApartment(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String clientName = request.getParameter("clientName");
        if (clientName == null || clientName.isEmpty()) {
            request.setAttribute("error", "Client name is required.");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            return;
        }
        Client client = new Client(clientName);
        try {
            boolean reserved = apartmentService.reserve(client);
            if (reserved) {
                request.setAttribute("message", "Apartment successfully reserved for " + clientName);
                request.getRequestDispatcher("/jsp/success.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "No available apartments.");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Reservation failed: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    private void showReleaseForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/release.jsp").forward(request, response);
    }

    private void releaseApartment(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idStr = request.getParameter("apartmentId");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Apartment id  is required.");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            boolean released = apartmentService.release(id);
            if (released) {
                request.setAttribute("message", "Apartment successfully released.");
                request.getRequestDispatcher("/jsp/success.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "No apartment found with id: " + id);
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid apartment ID format.");
        }
    }

    private void showSortingForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/sort.jsp").forward(request, response);
    }

    private void getPaginatedAndSortedApartments(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    private void showSaveStateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/save.jsp").forward(request, response);
    }

    private void saveState(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            statePersistence.saveState(apartmentService.getAllApartments());
            request.setAttribute("message", "State successfully saved.");
            request.getRequestDispatcher("/jsp/success.jsp").forward(request, response);
        } catch (IOException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error occurred when saving state");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }
}
