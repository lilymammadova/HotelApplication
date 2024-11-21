package org.liliya.hotelapp.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Apartment.class)
                    .addAnnotatedClass(Client.class)
                    .buildSessionFactory();
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutDown() {
        getSessionFactory().close();
    }
}
