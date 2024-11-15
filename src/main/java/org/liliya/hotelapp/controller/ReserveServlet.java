package org.liliya.hotelapp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.liliya.hotelapp.model.Client;

import java.io.IOException;

public class ReserveServlet extends AppServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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

}
