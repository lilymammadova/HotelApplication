package org.liliya.hotelapp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SaveApartmentsServlet extends AppServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
