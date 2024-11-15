package org.liliya.hotelapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ShowFormServlet extends AppServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String option = request.getParameter("option");
        String page;

        switch (option) {
            case "1" -> page = "/jsp/register.jsp";
            case "2" -> page = "/jsp/reserve.jsp";
            case "3" -> page = "/jsp/release.jsp";
            case "4" -> page = "/jsp/sort.jsp";
            case "5" -> page = "/jsp/save.jsp";
            default -> page = "/jsp/error.jsp";
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }
}
