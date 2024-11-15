package org.liliya.hotelapp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ReleaseServlet extends AppServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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

}
