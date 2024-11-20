package org.liliya.hotelapp.controller;

import jakarta.servlet.http.HttpServlet;
import org.liliya.hotelapp.persistence.DatabaseConnection;
import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;

public abstract class AppServlet extends HttpServlet {
    protected ApartmentService apartmentService;
    @Override
    public void init() {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        if (getServletContext().getAttribute("apartmentService") == null) {
            apartmentService = new ApartmentServiceImpl(databaseConnection);
            getServletContext().setAttribute("apartmentService", apartmentService);
        } else {
            apartmentService = (ApartmentService) getServletContext().getAttribute("apartmentService");
        }
    }
}

