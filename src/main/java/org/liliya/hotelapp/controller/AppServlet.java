package org.liliya.hotelapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import org.liliya.hotelapp.configuration.Configuration;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.persistence.StatePersistence;
import org.liliya.hotelapp.persistence.StatePersistenceImpl;
import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;

import java.util.List;

public abstract class AppServlet extends HttpServlet {
    protected ApartmentService apartmentService;
    protected StatePersistence<List<Apartment>> statePersistence;

    @Override
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        statePersistence = new StatePersistenceImpl<>(objectMapper);
        Configuration configuration = new Configuration();

        if (getServletContext().getAttribute("apartmentService") == null) {
            apartmentService = new ApartmentServiceImpl(statePersistence, configuration);
            getServletContext().setAttribute("apartmentService", apartmentService);
        } else {
            apartmentService = (ApartmentService) getServletContext().getAttribute("apartmentService");
        }
    }
}

