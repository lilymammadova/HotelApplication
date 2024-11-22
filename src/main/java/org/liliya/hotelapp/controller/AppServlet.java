package org.liliya.hotelapp.controller;

import jakarta.servlet.http.HttpServlet;
import org.hibernate.SessionFactory;
import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;
import org.liliya.hotelapp.util.HibernateUtil;

public abstract class AppServlet extends HttpServlet {
    protected ApartmentService apartmentService;
    @Override
    public void init() {
        if (getServletContext().getAttribute("apartmentService") == null) {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            apartmentService = new ApartmentServiceImpl(sessionFactory);
            getServletContext().setAttribute("apartmentService", apartmentService);
        } else {
            apartmentService = (ApartmentService) getServletContext().getAttribute("apartmentService");
        }
    }
}

