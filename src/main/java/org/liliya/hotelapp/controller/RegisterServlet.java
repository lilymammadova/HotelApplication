package org.liliya.hotelapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RegisterServlet extends AppServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
}

