package org.liliya.hotelapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;

public abstract class BaseServletTest {
    protected HttpServletRequest request = mock(HttpServletRequest.class);
    protected HttpServletResponse response = mock(HttpServletResponse.class);
    protected RequestDispatcher dispatcher = mock(RequestDispatcher.class);
}
